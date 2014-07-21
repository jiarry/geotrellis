package geotrellis.raster.op.focal

import geotrellis.raster._

trait ElevationOpMethods[+Repr <: RasterSource] { self: Repr =>
  def aspect() =
    focalWithExtent(Square(1)){ (tile, hood, re) =>
      Aspect(tile, hood, re.cellSize)
    }

  def slope(zFactor: Double) =
    focalWithExtent(Square(1)){ (tile, hood, re) =>
      Slope(tile, hood, re.cellSize, zFactor)
    }

  /**
   * Creates a slope operation with a default zFactor of 1.0.
   */
  def slope() =
    focalWithExtent(Square(1)){ (tile, hood, re) =>
      Slope(tile, hood, re.cellSize, 1.0)
    }

  def hillshade(azimuth: Double, altitude: Double, zFactor: Double) =
    focalWithExtent(Square(1)){ (tile, hood, re) =>
      Hillshade(tile, hood, re.cellSize, azimuth, altitude, zFactor)
    }

  /**
   * Create a default hillshade raster, using a default azimuth of 315 degrees,
   * altitude of 45 degrees, and z factor of 1.0.
   */
  def hillshade() =
    focalWithExtent(Square(1)){ (tile, hood, re) =>
      Hillshade(tile, hood, re.cellSize,  315.0, 45.0, 1.0)
    }
}
