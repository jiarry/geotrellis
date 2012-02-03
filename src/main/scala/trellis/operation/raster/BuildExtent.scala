package trellis.operation

import trellis.Extent
import trellis.process._

/**
 *
 */
case class BuildExtent(xmin:Op[Double], ymin:Op[Double], xmax:Op[Double], ymax:Op[Double])
extends Op4 (xmin, ymin, xmax, ymax) ((x1,y1,x2,y2) => Result(Extent(x1,y1,x2,y2)))

case class ParseExtent(s:String) extends Op1(s)({
  s => try {
    val Array(x1, y1, x2, y2) = s.split(",").map(_.toDouble)
    Result(Extent(x1, y1, x2, y2))
  } catch {
    case _ => sys.error("couldn't parse %s")
  }
})