package fr.upem.easymow

import java.io.FileNotFoundException

import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import fr.upem.easymow.file.IO._
import fr.upem.easymow.playground.{Field, South, West}

import scala.util.{Failure, Success, Try}

class IOtest extends FlatSpec with Matchers with GeneratorDrivenPropertyChecks {

  "Read file" should "return a list of String for an existing file" in {
      val f: Try[List[String]] = read("src/main/resources/input")
      assert(
        f match {
          case Success(_) => true
          case Failure(_) => false
        }
      )
  }

  "Read file .inputscalatest" should "return the content of .inputscalatest" in {
    val f: Try[List[String]] = read("src/main/resources/.inputscalatest")
    val c: List[String] = List("4 5", "3 4 N", "ADDGAA", "43 5 W", "AGGD" )

    val res = f match {
      case Success(content) =>
        val pair: List[(String, String)] = content zip c
        pair.forall{case (x,y) => x == y}
      case Failure(_) => false
    }
    assert(res)
  }

  "Read file .inputscalatest" should "failed on no existing file" in {
    val f: Try[List[String]] = read("WRONG PATH")
    val res = f match {
      case Success(_) => false
      case Failure(e) =>
        a [FileNotFoundException] should be thrownBy {
          throw e
        }
        true
    }
    assert(res)
  }


  "Analyze format" should "return a list of errors" in {
    val c: List[String] = List("4", "3", "ADDGAAZ", "43 5 N", "AGGD", "invalid", "AD", "", "", "3 4 N" )
    val analyseRes: Either[List[String], Field] = analyzeFormat(c)
    val res = analyseRes match {
      case Left(content) =>
        assert(content.length == 7)
        assert(content(0) == "Some instructions may be missing : 10 instruction found")
        assert(content(1) == "Field size definition 4 is incorrect")
        assert(content(2) == "Initialization format is incorrect : 3")
        assert(content(3) == "Initialization format is incorrect : invalid")
        assert(content(4) == "Initialization format is incorrect : ")
        assert(content(5) == "Instruction format is incorrect : ADDGAAZ")
        assert(content(6) == "Instruction format is incorrect : ")
        true
      case Right(_) => false
    }
    assert(res)
  }

  "Analyze format" should "return Field" in {
    val c: List[String] = List("4 5", "0 2 W", "ADDGAAD", "0 0 S", "ggg")
    val analyseRes: Either[List[String], Field] = analyzeFormat(c)
    val res = analyseRes match {
      case Left(_) => false
      case Right(field) =>
        assert(field.vehicles.length == 2)
        // First lawnmower
        assert(field.vehicles(0).pos.x == 0)
        assert(field.vehicles(0).pos.y == 2)
        assert(field.vehicles(0).pos.orientation == West)
        assert(field.vehicles(0).instruction == "ADDGAAD")
        // Second lawnmower
        assert(field.vehicles(1).pos.x == 0)
        assert(field.vehicles(1).pos.y == 0)
        assert(field.vehicles(1).pos.orientation == South)
        assert(field.vehicles(1).instruction == "ggg")
        true
    }
    assert(res)
  }

  "Field format" should "be false" in {
    assert(!isFieldValidFormat(""))
    assert(!isFieldValidFormat(" "))
    assert(!isFieldValidFormat("      "))
    assert(!isFieldValidFormat("    false"))
    assert(!isFieldValidFormat("43324 4342 N"))
    assert(!isFieldValidFormat("4"))
    assert(!isFieldValidFormat("4n5"))
  }

  "Field format" should "be true" in {
    assert(isFieldValidFormat("    5  645   "))
    assert(isFieldValidFormat(" 63256 3810"))
    assert(isFieldValidFormat("3 4"))
  }


  "Vehicle position format" should "be false" in {
    assert(!isVehiclePosValidFormat("9 5 f"))
    assert(!isVehiclePosValidFormat("   56 99 f  "))
    assert(!isVehiclePosValidFormat("   56 99 ffuqe   "))
    assert(!isVehiclePosValidFormat("51"))
    assert(!isVehiclePosValidFormat("   51   1 "))
    assert(!isVehiclePosValidFormat("N 52 0"))
    assert(!isVehiclePosValidFormat("false"))
  }

  "Vehicle position format" should "be true" in {
    assert(isVehiclePosValidFormat("    5  645   N"))
    assert(isVehiclePosValidFormat("54 6 W"))
    assert(isVehiclePosValidFormat("54 6 s"))
    assert(isVehiclePosValidFormat("3 4      e"))
  }

  "Instruction format" should "be false" in {
    assert(!isInstructionValidFormat("95f"))
    assert(!isInstructionValidFormat("4 5"))
    assert(!isInstructionValidFormat("54 7 N"))
    assert(!isInstructionValidFormat("AGDGGD  "))
    assert(!isInstructionValidFormat("   AGDGGD"))
    assert(!isInstructionValidFormat("AGDGGD  "))
    assert(!isInstructionValidFormat("   AGDGGD  "))
    assert(!isInstructionValidFormat("agdz"))
  }

  "Instruction format" should "be true" in {
    assert(isInstructionValidFormat("AGGDAAGDG"))
    assert(isInstructionValidFormat("agdaa"))
  }

  "Even index element" should "be amy, elaad, lele" in {
    val l: List[String] = List("amy", "osef1", "elaad", "osef2", "lele", "osef3")
    val l2: List[String] = List()
    assert(getEvenIndexElement(l) == List("amy", "elaad", "lele"))
    assert(getEvenIndexElement(l2) == List())
  }

  "Odd index element" should "be amy, elaad, lele" in {
    val l: List[String] = List("osef1", "amy", "osef2", "elaad", "osef3", "lele")
    val l2: List[String] = List()
    assert(getOddIndexElement(l) == List("amy", "elaad", "lele"))
    assert(getOddIndexElement(l2) == List())
  }

  "A file" should "contain odd number of line" in {
    assert(!(isValidNumberOfLine(4).get == "Field size definition 4 is incorrect"))
    assert(isValidNumberOfLine(7) match {
      case Some(_) => false
      case None => true
    })
  }

  "Number of line" should "at least be equal to 3" in {
    assert(isValidNumberOfLine(0) match {
      case Some(_) => true
      case None => false
    })
    assert(isValidNumberOfLine(1) match {
      case Some(_) => true
      case None => false
    })
    assert(isValidNumberOfLine(2) match {
      case Some(_) => true
      case None => false
    })
    assert(isValidNumberOfLine(3) match {
      case Some(_) => false
      case None => true
    })
  }

  "A field initialization" should "contains only 2 digits" in {
    assert(errorSizeField("3").get == "Field size definition 3 is incorrect")
    assert(errorSizeField("3 N").get == "Field size definition 3 N is incorrect")
    assert(errorSizeField("   3    3   ") match {
      case Some(_) => false
      case None => true
    })
  }

  "An vehicle position initialization" should "contains only 2 digits and one cardinal character" in {
    assert(errorPosFormat("3").get == "Initialization format is incorrect : 3")
    assert(errorPosFormat("3 N").get == "Initialization format is incorrect : 3 N")
    assert(errorPosFormat("   3    3   W  ") match {
      case Some(_) => false
      case None => true
    })
  }

  "A list of error" should "be returned" in {
    assert(isNonEmptyErrorsList(List(None, None)) match {
      case Some(c) =>
        assert(c(0) == None)
        assert(c(1) == None)
        true
      case None => false
    })
    assert(isNonEmptyErrorsList(List(Some("ERROR1"))) match {
      case Some(c) =>
        assert(c(0) == Some("ERROR1"))
        true
      case None => false
    })

    assert(isNonEmptyErrorsList(List()) match {
      case Some(_) => false
      case None => true
    })
  }


}

