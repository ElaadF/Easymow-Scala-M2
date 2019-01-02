package fr.upem.easymow

import fr.upem.easymow.file.RegexUtils._
import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.prop.GeneratorDrivenPropertyChecks



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
}
