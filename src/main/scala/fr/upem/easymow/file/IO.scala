package fr.upem.easymow.file

import scala.io.Source
import fr.upem.easymow.playground.CardinalUtils._
import fr.upem.easymow.error.{ErrorMsg, _}
import fr.upem.easymow.playground._
import fr.upem.easymow.file.RegexUtils._
import fr.upem.easymow.vehicle._

import scala.util.Try

object IO {
  def read(path: String): Try[List[String]] = Try(Source.fromFile(path).getLines.toList)

  def analyzeFormat(content: List[String]): Either[List[ErrorMsg[String]], Field] = {
    val contentSize = content.length

    val invalidSizeFormat: Option[ErrorMsg[String]] = isValidNumberOfLine(contentSize)
    val errorsLines: Option[ErrorMsg[String]] = errorSizeField(content)


    val RegexAnalysis.patternVehiclePos(length, width) = content(1)
    val vehicles = content.drop(1)
    val positions: List[String] = getEvenIndexElement(vehicles)
    val instructions: List[String] = getOddIndexElement(vehicles)

    val posAnalysisIncorrect: List[String] =  positions.filter(instr => !isVehiclePosValidFormat(instr))
    val posAnalysisErrors: List[Option[ErrorMsg[String]]] = posAnalysisIncorrect.map(s => Some(VehiclePositionFormatIncorrect))

    val instrAnalysisIncorrect: List[String] =  instructions.filter(instr => !isInstructionValidFormat(instr))
    val instrAnalysisErrors: List[Option[ErrorMsg[String]]] = instrAnalysisIncorrect.map(s => Some(InstructionFormatIncorrect))


    val errors = List(List(invalidSizeFormat), List(errorsLines), isNonEmptyErrorsList(posAnalysisErrors).getOrElse(List()), isNonEmptyErrorsList(instrAnalysisErrors).getOrElse(List()))
    val flatErrors = errors.flatten.flatten

    if(flatErrors.nonEmpty)
      Left(flatErrors)

    else {
      val vehiclesTuples = positions zip instructions
      val vehicles = vehiclesTuples.map{case(pos, instr) =>
        val RegexAnalysis.patternVehiclePos(x, y, orientation) = pos
        Lawnmower(Position(x.toInt, y.toInt, orientation.toCardinal), instr)}
      Right(Field(length.toInt, width.toInt, vehicles))
    }
  }

  def isFieldValidFormat(s: String): Boolean = RegexAnalysis.patternFieldSize matches s

  def isVehiclePosValidFormat(s: String): Boolean = RegexAnalysis.patternVehiclePos matches s

  def isInstructionValidFormat(s: String): Boolean = RegexAnalysis.patternInstructions matches s

  def getEvenIndexElement(l: List[String]): List[String] = l.indices.collect { case i if i % 2 == 0 => l(i) }.toList

  def getOddIndexElement(l: List[String]): List[String] = l.indices.collect { case i if i % 2 != 0 => l(i) }.toList

  def isValidNumberOfLine(contentSize: Int): Option[ErrorMsg[String]] = contentSize match {
    case c if c < 3 || c%2 == 0 => Some(FileNumberLineWrong)
    case _ => None
  }

  def errorSizeField(content: List[String]): Option[ErrorMsg[String]] = content match {
    case c if isFieldValidFormat(c.head) => Some(FieldSizeFormatIncorrect)
    case _ => None
  }

  def errorPosFormat(content: List[String]): Option[ErrorMsg[String]] = content match {
    case c if isFieldValidFormat(c.head) => Some(FieldSizeFormatIncorrect)
    case _ => None
  }

  def isNonEmptyErrorsList(content: List[Option[ErrorMsg[String]]]): Option[List[Option[ErrorMsg[String]]]] = content match {
    case c if c.nonEmpty => Some(c)
    case _ => None
  }
}

object crashtest {
  import scala.util.Success
  import scala.util.Failure
  import org.apache.logging.log4j.scala.Logging
  fr.upem.easymow.file.IO
  //val logger: log4j.Logger = LogManager.getLogger(getClass().getName())

  def main (args: Array[String] ): Unit = {

   //logger.info("toto")
   IO.read("../resources/input") match {
     case Success(content) =>
       IO.analyzeFormat(content) match {
         case Left(e) => e.foreach(println)
         case Right(f) => println(f)
       }
     case Failure(ex) => println(s"Erreur lecture fichier : $ex")
   }

//    b match {
//      case Left(s) => logger.warn(s.errorMessage(0,3))
//      case Right(s) => logger.info(s)
//    }
  }
}