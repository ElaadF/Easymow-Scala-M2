package fr.upem.easymow

import fr.upem.easymow.playground._
import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class PositionTest  extends FlatSpec with Matchers with GeneratorDrivenPropertyChecks {
  "Create position" should "return a right position object" in {
    forAll("x", "y") { (x: Int, y: Int) =>
      val p1: Position = Position(x, y, West)
      val p2: Position = Position(x, y, South)
      val p3: Position = Position(x, y, East)
      val p4: Position = Position(x, y, North)

      assert(p1.x == x)
      assert(p1.y == y)
      assert(p1.orientation == West)

      assert(p2.x == x)
      assert(p2.y == y)
      assert(p2.orientation == South)

      assert(p3.x == x)
      assert(p3.y == y)
      assert(p3.orientation == East)

      assert(p4.x == x)
      assert(p4.y == y)
      assert(p4.orientation == North)
    }
  }

}
