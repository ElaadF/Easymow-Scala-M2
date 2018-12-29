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

  def analyzeFormat(content: List[String]): Either[List[ErrorMsg[String]], Field] = {
    val errors = List[ErrorMsg[String]]()
    val contentSize = content.length

    if(contentSize< 3 || contentSize%2 == 0)
      errors :+ Left(FileNumberLineWrong(contentSize))

    content match {
      case _ if contentSize < 3 =>  errors :+ Left(FileNumberLineWrong(contentSize.toString))
      case _ if contentSize%2 == 0 => errors :+ Left(FileNumberLineWrong(contentSize.toString))
      case c if isFieldValidFormat(c.head) => errors :+ Left(FieldSizeFormatIncorrect(c.head))
    }

    val RegexAnalysis.patternVehiclePos(length, width) = content(1)
    val vehicles = content.drop(1)
    val positions = getEvenIndexElement(vehicles)
    val instructions = getOddIndexElement(vehicles)

    val PosAnalysisIncorrect =  positions.filter(instr => !isVehiclePosValidFormat(instr))
    val PosAnalysisErrors = PosAnalysisIncorrect.map(s => Left(VehiclePositionFormatIncorrect(s)))

    val instrAnalysisIncorrect =  instructions.filter(instr => !isInstructionValidFormat(instr))
    val instrAnalysisErrors = instrAnalysisIncorrect.map(s => Left(InstructionFormatIncorrect(s)))

    if(instrAnalysisErrors.nonEmpty || PosAnalysisErrors.nonEmpty) {
      errors :+ instrAnalysisErrors
      errors :+ PosAnalysisErrors
    }

    if(errors.nonEmpty)
      Left(errors)
    else {
      val vehiclesTuples = positions zip instructions
      val vehicles = vehiclesTuples.map{case(pos, instr) =>
        val RegexAnalysis.patternVehiclePos(x, y, orientation) = pos
        Lawnmower(Position(x.toInt, y.toInt, orientation.toCardinal), instr)}
      Right(Field(length.toInt, width.toInt, vehicles))
    }





//    val (leftPos, rightPos) = posAnalysisResults.separate
//    val (leftInstr, rightInstr) = instrAnalysisResults.separate
   //


  }
  def isFieldValidFormat(s: String): Boolean = RegexAnalysis.patternFieldSize matches s
  def isVehiclePosValidFormat(s: String): Boolean = RegexAnalysis.patternVehiclePos matches s
  def isInstructionValidFormat(s: String): Boolean = RegexAnalysis.patternInstructions matches s
  def getEvenIndexElement(l: List[String]): List[String] = l.indices.collect { case i if i % 2 == 0 => l(i) }.toList
  def getOddIndexElement(l: List[String]): List[String] = l.indices.collect { case i if i % 2 != 0 => l(i) }.toList

}
