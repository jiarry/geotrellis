/*
 * Copyright (c) 2014 Azavea.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package geotrellis.raster.op.focal

import org.scalatest._
import geotrellis.engine._
import geotrellis.testkit._

class MaxSpec extends FunSpec with TileBuilders
                              with Matchers 
                              with TestEngine {


  describe("Max") {
    it("should square max for raster source") {
      val rs1 = createRasterSource(
        Array( nd,7,1,      1,1,1,      1,1,1,
                9,1,1,      2,2,2,      1,3,1,

                3,8,1,      3,3,3,      1,1,2,
                2,1,7,     1,nd,1,      8,1,1
        ),
        3,2,3,2
      )

      run(rs1.focalMax(Square(1))) match {
        case Complete(result,success) =>
//          println(success)
          assertEqual(result,
            Array(9, 9, 7,    2, 2, 2,    3, 3, 3,
                  9, 9, 8,    3, 3, 3,    3, 3, 3,

                  9, 9, 8,    7, 3, 8,    8, 8, 3,
                  8, 8, 8,    7, 3, 8,    8, 8, 2))
        case Error(msg,failure) =>
          println(msg)
          println(failure)
          assert(false)

      }
    }

    it("should square max with 5x5 neighborhood") {
      val rs1 = createRasterSource(
        Array( nd,7,1,      1,1,1,      1,1,1,
                9,1,1,      2,2,2,      1,3,1,

                3,8,1,      3,3,3,      1,1,2,
                2,1,7,     1,nd,1,      8,1,1
        ),
        3,2,3,2
      )

      run(rs1.focalMax(Square(2))) match {
        case Complete(result,success) =>
//          println(success)
          assertEqual(result,
            Array(9, 9, 9,    8, 3, 3,    3, 3, 3,
                  9, 9, 9,    8, 8, 8,    8, 8, 8,

                  9, 9, 9,    8, 8, 8,    8, 8, 8,
                  9, 9, 9,    8, 8, 8,    8, 8, 8))
        case Error(msg,failure) =>
          println(msg)
          println(failure)
          assert(false)

      }
    }

    it("should circle max for raster source") {
      val rs1 = createRasterSource(
        Array( nd,7,1,     1, 1,1,      1,1,1,
                9,1,1,     2, 2,2,      1,3,1,

                3,8,1,     3, 3,3,      1,1,2,
                2,1,7,     1,nd,1,      8,1,1
        ),
        3,2,3,2
      )

      run(rs1.focalMax(Circle(1))) match {
        case Complete(result,success) =>
          //println(success)
          assertEqual(result,
            Array(9, 7, 7,    2, 2, 2,    1, 3, 1,
                  9, 9, 2,    3, 3, 3,    3, 3, 3,

                  9, 8, 8,    3, 3, 3,    8, 3, 2,
                  3, 8, 7,    7, 3, 8,    8, 8, 2))
        case Error(msg,failure) =>
          // println(msg)
          // println(failure)
          assert(false)

      }
    }
  }
}
