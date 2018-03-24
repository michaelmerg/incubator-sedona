/*
 * FILE: GeoSparkSQLRegistrator.scala
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
package org.datasyslab.geosparksql.utils

import org.apache.spark.sql.geosparksql.strategy.join.JoinQueryDetector
import org.apache.spark.sql.{SQLContext, SparkSession}
import org.datasyslab.geosparksql.UDF.UdfRegistrator
import org.datasyslab.geosparksql.UDT.UdtRegistrator

object GeoSparkSQLRegistrator {
  def registerAll(sqlContext: SQLContext): Unit = {
    sqlContext.experimental.extraStrategies = JoinQueryDetector :: Nil
    UdtRegistrator.registerAll()
    UdfRegistrator.registerAll(sqlContext)
  }

  def registerAll(sparkSession: SparkSession): Unit = {
    sparkSession.experimental.extraStrategies = JoinQueryDetector :: Nil
    UdtRegistrator.registerAll()
    UdfRegistrator.registerAll(sparkSession)
  }

  def dropAll(sparkSession: SparkSession): Unit = {
    UdfRegistrator.dropAll(sparkSession)
  }
}
