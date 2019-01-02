package fr.upem.easymow

import java.io.FileNotFoundException

import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import fr.upem.easymow.file.IO._

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
}

