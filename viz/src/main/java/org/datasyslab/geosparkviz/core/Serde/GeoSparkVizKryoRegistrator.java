/*
 * FILE: GeoSparkVizKryoRegistrator
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
package org.datasyslab.geosparkviz.core.Serde;

import com.esotericsoftware.kryo.Kryo;
import org.apache.log4j.Logger;
import org.apache.spark.serializer.KryoRegistrator;
import org.datasyslab.geospark.serde.GeoSparkKryoRegistrator;
import org.datasyslab.geosparkviz.core.ImageSerializableWrapper;
import org.datasyslab.geosparkviz.utils.Pixel;

public class GeoSparkVizKryoRegistrator
        implements KryoRegistrator
{
    final static Logger log = Logger.getLogger(GeoSparkVizKryoRegistrator.class);

    @Override
    public void registerClasses(Kryo kryo)
    {
        GeoSparkKryoRegistrator geosparkKryoRegistrator = new GeoSparkKryoRegistrator();
        ImageWrapperSerializer imageWrapperSerializer = new ImageWrapperSerializer();
        PixelSerializer pixelSerializer = new PixelSerializer();
        geosparkKryoRegistrator.registerClasses(kryo);
        log.info("Registering custom serializers for visualization related types");
        kryo.register(ImageSerializableWrapper.class, imageWrapperSerializer);
        kryo.register(Pixel.class, pixelSerializer);
    }
}
