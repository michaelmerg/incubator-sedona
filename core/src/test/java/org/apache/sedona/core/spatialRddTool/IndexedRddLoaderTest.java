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

import org.apache.sedona.core.TestBase;
import org.apache.sedona.core.enums.GridType;
import org.apache.sedona.core.enums.IndexType;
import org.apache.sedona.core.spatialRDD.SpatialRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class IndexedRddLoaderTest extends TestBase {

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  @BeforeClass
  public static void onceExecutedBeforeAll() {
    initialize(IndexedRddLoaderTest.class.getSimpleName());
  }

  @AfterClass
  public static void TearDown() {
    spark.stop();
  }

  @Test
  public void test() throws Exception {
    tempFolder.create();

    SpatialRDD<Geometry> mapRdd = new SpatialRDD<>();
    String csvPath = IndexedRddLoaderTest.class.getClassLoader().getResource("./small/points.csv").getPath();
    Dataset<Row> map = spark.read().option("delimiter", "\t").csv(csvPath);
    mapRdd.rawSpatialRDD = map.javaRDD().map(row -> {
      Geometry geom = new WKTReader().read((String) row.getAs("_c1"));
      geom.setUserData(row.copy());
      return geom;
    });
    Envelope boundaryEnvelope = new Envelope(180, -180, 90, -90);
    mapRdd.analyze(boundaryEnvelope, Math.toIntExact(map.count()));
    mapRdd.spatialPartitioning(GridType.KDBTREE, 2);
    mapRdd.buildIndex(IndexType.RTREE, true);
    String indexedRddPath = tempFolder.newFolder().getPath() + "/indexedRDD";
    mapRdd.indexedRDD.saveAsObjectFile(indexedRddPath);
    String partitionerPath = tempFolder.newFolder().getPath() + "/partitioner.ser";
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(partitionerPath))) {
      oos.writeObject(mapRdd.getPartitioner());
    }

    SpatialRDD<Geometry> loadedSpatialRdd = new IndexedRddLoader<>(sc).load(indexedRddPath, partitionerPath);

    JavaRDD<Boolean> hasCorrectPartitionId = loadedSpatialRdd.indexedRDD
            .mapPartitionsWithIndex((index, spatialIndexIterator) -> {
              int partitionIndex = ((PartitionAwareSpatialIndex) spatialIndexIterator.next()).getPartitionIndex();
              return Collections.singleton(partitionIndex == index).iterator();
            }, true);
    hasCorrectPartitionId.collect().forEach(Assert::assertTrue);
    assertEquals(mapRdd.getPartitioner().getGridType(), loadedSpatialRdd.getPartitioner().getGridType());
    assertEquals(mapRdd.getPartitioner().getGrids(), loadedSpatialRdd.getPartitioner().getGrids());
  }

}
