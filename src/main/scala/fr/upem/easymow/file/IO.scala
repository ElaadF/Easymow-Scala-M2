package fr.upem.easymow.file

import scala.io.Source
import fr.upem.easymow.playground.CardinalUtils._
import fr.upem.easymow.error._
import fr.upem.easymow.playground._
import fr.upem.easymow.file.RegexUtils._
import fr.upem.easymow.vehicle._

import scala.util.Try

object IO {
  def read(path: String): Try[List[String]] = Try(Source.fromFile(path).getLines.toList)

  def analyzeFormat(content: List[String]): Either[List[String], Field] = {
    val contentSize = content.length

    val invalidSizeFormat: Option[String] = isValidNumberOfLine(contentSize)
    val errorsLines: Option[String] = errorSizeField(content)


    val RegexAnalysis.patternFieldSize(length, width) = content.head
    val vehicles = content.drop(1)
    val positions: List[String] = getEvenIndexElement(vehicles)
    val instructions: List[String] = getOddIndexElement(vehicles)

    val posAnalysisIncorrect: List[String] =  positions.filter(instr => !isVehiclePosValidFormat(instr))
    val posAnalysisErrors: List[Option[String]] = posAnalysisIncorrect.map(s => Some(VehiclePositionFormatIncorrect.errorMessage(s)))

    val instrAnalysisIncorrect: List[String] =  instructions.filter(instr => !isInstructionValidFormat(instr))
    val instrAnalysisErrors: List[Option[String]] = instrAnalysisIncorrect.map(s => Some(InstructionFormatIncorrect.errorMessage(s)))


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

  def isValidNumberOfLine(contentSize: Int): Option[String] = contentSize match {
    case c if c < 3 || c%2 == 0 => Some(FileNumberLineWrong.errorMessage(c.toString))
    case _ => None
  }

  def errorSizeField(content: List[String]): Option[String] = content match {
    case c if !isFieldValidFormat(c.head) => Some(FieldSizeFormatIncorrect.errorMessage(c.head))
    case _ => None
  }

  def errorPosFormat(content: List[String]): Option[String] = content match {
    case c if !isVehiclePosValidFormat(c.head) => Some(VehiclePositionFormatIncorrect.errorMessage(c.head))
    case _ => None
  }

  def isNonEmptyErrorsList(content: List[Option[String]]): Option[List[Option[String]]] = content match {
    case c if c.nonEmpty => Some(c)
    case _ => None
  }
}