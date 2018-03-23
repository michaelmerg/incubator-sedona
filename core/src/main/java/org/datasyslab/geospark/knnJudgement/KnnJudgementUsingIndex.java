/*
 * FILE: KnnJudgementUsingIndex
 * Copyright (c) 2015 - 2018 GeoSpark Development Team
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package org.datasyslab.geospark.knnJudgement;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.index.strtree.GeometryItemDistance;
import com.vividsolutions.jts.index.strtree.STRtree;
import org.apache.spark.api.java.function.FlatMapFunction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// TODO: Auto-generated Javadoc

/**
 * The Class KnnJudgementUsingIndex.
 */
public class KnnJudgementUsingIndex<U extends Geometry, T extends Geometry>
        implements FlatMapFunction<Iterator<SpatialIndex>, T>, Serializable
{

    /**
     * The k.
     */
    int k;

    /**
     * The query center.
     */
    U queryCenter;

    /**
     * Instantiates a new knn judgement using index.
     *
     * @param queryCenter the query center
     * @param k the k
     */
    public KnnJudgementUsingIndex(U queryCenter, int k)
    {
        this.queryCenter = queryCenter;
        this.k = k;
    }

    /* (non-Javadoc)
     * @see org.apache.spark.api.java.function.FlatMapFunction#call(java.lang.Object)
     */
    @Override
    public Iterator<T> call(Iterator<SpatialIndex> treeIndexes)
            throws Exception
    {
        SpatialIndex treeIndex = treeIndexes.next();
        final Object[] localK;
        if (treeIndex instanceof STRtree) {
            localK = ((STRtree) treeIndex).kNearestNeighbour(queryCenter.getEnvelopeInternal(), queryCenter, new GeometryItemDistance(), k);
        }
        else {
            throw new Exception("[KnnJudgementUsingIndex][Call] QuadTree index doesn't support KNN search.");
        }
        List<T> result = new ArrayList();
        for (int i = 0; i < localK.length; i++) {
            result.add((T) localK[i]);
        }
        return result.iterator();
    }
}
