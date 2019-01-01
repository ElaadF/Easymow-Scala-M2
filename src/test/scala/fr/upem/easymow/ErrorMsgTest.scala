package fr.upem.easymow

import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}
import fr.upem.easymow.error._

class ErrorMsgTest extends FlatSpec with Matchers with GeneratorDrivenPropertyChecks {

  "Add Occupied location error" should "return" in {
    forAll("x", "y") { (x: Int, y: Int) =>
      AddOccupiedLocation.errorMessage((x, y)) should be(s"Location ${(x, y)} is already occupied by an other vehicle: vehicles ignored")
    }
  }

  "Add out of bound location error" should "return" in {
    forAll("x", "y") { (x: Int, y: Int) =>
      AddOutOfBound.errorMessage((x, y)) should be(s"Location ${(x, y)} is out of bound: vehicles ignored")
    }
  }

  "Read file error" should "return" in {
    forAll("path") { path: String =>
      ReadFileFailed.errorMessage(path) should be(s"ead $path have failed")
    }
  }
}
