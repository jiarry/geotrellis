/*
 * Copyright 2016 Azavea
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package geotrellis.spark.etl.config

import geotrellis.proj4.CRS
import geotrellis.raster.resample.PointResampleMethod
import geotrellis.raster.{CellSize, CellType, RasterExtent}
import geotrellis.spark.io.index.{HilbertKeyIndexMethod, KeyIndexMethod, RowMajorKeyIndexMethod, ZCurveKeyIndexMethod}
import geotrellis.spark.pyramid.Pyramid
import geotrellis.spark.tiling._
import geotrellis.vector.Extent

import org.apache.spark.HashPartitioner

case class Output(
  backend: Backend,
  resampleMethod: PointResampleMethod,
  reprojectMethod: ReprojectMethod,
  keyIndexMethod: IngestKeyIndexMethod,
  tileSize: Int = 256,
  pyramid: Boolean = true,
  partitions: Option[Int] = None,
  layoutScheme: Option[String] = None,
  layoutExtent: Option[Extent] = None,
  crs: Option[String] = None,
  resolutionThreshold: Option[Double] = None,
  cellSize: Option[CellSize] = None,
  cellType: Option[CellType] = None,
  encoding: Option[String] = None,
  breaks: Option[String] = None,
  maxZoom: Option[Int] = None
) extends Serializable {

  require(maxZoom.isEmpty || layoutScheme == Some("zoomed"),
    "maxZoom can only be used with 'zoomed' layoutScheme")

  def getCrs = crs.map(CRS.fromName)

  def getLayoutScheme: LayoutScheme = (layoutScheme, getCrs, resolutionThreshold) match {
    case (Some("floating"), _, _)            => FloatingLayoutScheme(tileSize)
    case (Some("zoomed"), Some(c), Some(rt)) => ZoomedLayoutScheme(c, tileSize, rt)
    case (Some("zoomed"), Some(c), _)        => ZoomedLayoutScheme(c, tileSize)
    case _ => throw new Exception("unsupported layout scheme definition")
  }

  def getLayoutDefinition = (layoutExtent, cellSize) match {
    case (Some(le), Some(cs)) => LayoutDefinition(RasterExtent(le, cs), tileSize)
    case _ => throw new Exception("unsupported layout definition")
  }

  def getKeyIndexMethod[K] = (((keyIndexMethod.`type`, keyIndexMethod.temporalResolution) match {
    case ("rowmajor", None)    => RowMajorKeyIndexMethod
    case ("hilbert", None)     => HilbertKeyIndexMethod
    case ("hilbert", Some(tr)) => HilbertKeyIndexMethod(tr.toInt)
    case ("zorder", None)      => ZCurveKeyIndexMethod
    case ("zorder", Some(tr))  => ZCurveKeyIndexMethod.byMilliseconds(tr)
    case _                     => throw new Exception("unsupported keyIndexMethod definition")
  }): KeyIndexMethod[_]).asInstanceOf[KeyIndexMethod[K]]

  def getPyramidOptions = Pyramid.Options(resampleMethod, partitions.map(new HashPartitioner(_)))
}
