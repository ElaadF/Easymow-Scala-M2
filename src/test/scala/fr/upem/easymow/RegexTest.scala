package fr.upem.easymow

import fr.upem.easymow.file.RegexAnalysis
import fr.upem.easymow.file.RegexUtils._
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}



class RegexTest extends FlatSpec with Matchers with GeneratorDrivenPropertyChecks {

  "A string" should "match regex" in {
    assert( """\d+""".r matches "1234")
  }

  "All string in a list" should "match regex" in {
    assert( """\d+""".r matches List("1", "124", "43232"))
  }

  "All string in a vector" should "match regex" in {
    assert( """\d+""".r matches Vector("1", "124", "43232"))
  }

  "All string in a vector" should "not match regex" in {
    assert( !("""\d+""".r matches Vector("a", "b", "c")))
  }

  "A string" should "not match regex" in {
    assert( !("""\d+""".r matches "elaad"))
  }

  "All string in a list" should "not match regex" in {
    assert( !("""\d+""".r matches List("d", "e", "h")))
  }

  "Position vehicle regex" should "be equal to patternVehiclePos" in {
    assert(RegexAnalysis.patternVehiclePos.toString() == """^[\t ]*(\d+)[\t ]+(\d+)[\t ]+([newsNEWS]{1})[\t ]*$""".r.toString())
  }

  "Field size regex" should "be equal to patternVehiclePos" in {
    assert(RegexAnalysis.patternFieldSize.toString() == """[\t ]*(\d+)[\t ]+(\d+)[\t ]*""".r.toString())
  }

  "Instructions regex" should "be equal to patternInstructions" in {
    assert(RegexAnalysis.patternInstructions.toString() == """[agdAGD]+""".r.toString())
  }
}
