package fr.upem.easymow.file

import scala.util.matching.Regex

/** Typeclass derivation for List[String], String and Vector[String] type
  * ==Overview==
  *
  *  {{{
  *  scala> "12" matches """\d+""".r
  *  true: Boolean
  *
  *  scala> List("1","toto") matches """\d+""".r
  *  false: Boolean
  *
  *  scala> Vector("1","4") matches """\d+""".r
  *  true: Boolean
  *  }}}
  * */
object RegexUtils {

  /** Match the regex on a string
    *
    *  @param reg the regex to match
    */
  implicit class RegexString(val reg: Regex) extends AnyVal {
    def matches(s: String): Boolean = reg.pattern.matcher(s).matches
  }

  /** Match the regex on all String in Vector[String]
  *
  *  @param reg the regex to match
  */
  implicit class RegexVector(val reg: Regex) extends AnyVal {
    def matches(vs: Vector[String]): Boolean = vs.forall(s => reg.pattern.matcher(s).matches)
  }

  /** Match the regex on all String in List[String]
    *
    *  @param reg the regex to match
    */
  implicit class RegexList(val reg: Regex) extends AnyVal {
    def matches(ls: List[String]): Boolean = ls.forall(s => reg.pattern.matcher(s).matches)
  }
}

/** The regex rules for the file's format */
object RegexAnalysis {
  val patternVehiclePos: Regex = """^(\d+)[\t ]+(\d+)[\t ]+([newsNEWS]{1})[\t ]*$""".r
  val patternFieldSize: Regex = """(\d+)[\t ]+(\d+)[\t ]*""".r
  val patternInstructions: Regex = """[agdAGD]+""".r
}