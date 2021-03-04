/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.sedona.core.spatialRddTool;

import org.apache.sedona.core.spatialPartitioning.KeyPartitioner;
import org.apache.sedona.core.spatialPartitioning.SpatialPartitioner;
import org.apache.sedona.core.spatialRDD.SpatialRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.index.SpatialIndex;
import scala.Tuple2;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collections;

public class IndexedRddLoader<T extends Geometry> {

  private final JavaSparkContext sparkContext;

  public IndexedRddLoader(JavaSparkContext sparkContext) {
    this.sparkContext = sparkContext;
  }

  public SpatialRDD<T> load(String indexedRddPath, String partitionerPath) throws IOException {
    SpatialRDD<T> spatialRDD = new SpatialRDD<>();
    spatialRDD.indexedRDD = loadIndexedRdd(indexedRddPath);
    spatialRDD.setPartitioner(loadPartitioner(partitionerPath));
    return spatialRDD;
  }

  private JavaRDD<SpatialIndex> loadIndexedRdd(String indexedRddPath) {
    JavaRDD<SpatialIndex> inputRdd = sparkContext.objectFile(indexedRddPath);
    return inputRdd
            .flatMapToPair(spatialIndex -> {
              if (!(spatialIndex instanceof PartitionAwareSpatialIndex))
                throw new Exception("RDD does not PartitionAwareSpatialIndex.");
              Integer partitionIndex = ((PartitionAwareSpatialIndex) spatialIndex).getPartitionIndex();
              return Collections.singleton(new Tuple2<>(partitionIndex, spatialIndex)).iterator();
            })
            .partitionBy(new KeyPartitioner(inputRdd.getNumPartitions()))
            .mapPartitions(iter -> Collections.singleton(iter.next()._2).iterator(), true);
  }

  private SpatialPartitioner loadPartitioner(String partitionerPath) throws IOException {
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(partitionerPath))) {
      return (SpatialPartitioner) ois.readObject();
    } catch (ClassNotFoundException e) {
      throw new IOException("Error loading partitioner", e);
    }
  }

}
