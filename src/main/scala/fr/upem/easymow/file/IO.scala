package fr.upem.easymow.file

import scala.io.Source
import fr.upem.easymow.playground.CardinalUtils._
import fr.upem.easymow.error._
import fr.upem.easymow.playground._
import fr.upem.easymow.file.RegexUtils._
import fr.upem.easymow.vehicle._
import scala.util.Try

/** Read and perform syntax analysis from the file readed */
object IO {

  /** Read a file using a path
    *
    *  @param path path's file
    *  @return a list of lines of the file
    */
  def read(path: String): Try[List[String]] = Try(Source.fromFile(path).getLines.toList)

  /** Analyze a list of lines to search errors and create a [[fr.upem.easymow.playground.Field Field]]
    *
    *  @param content the list of line to analyze
    *  @return a list of errors or a Field if no fatal errors occurred
    */
  def analyzeFormat(content: List[String]): Either[List[String], Field] = {
    val contentSize = content.length
    val invalidSizeFormat: Option[String] = isValidNumberOfLine(contentSize)
    val errorFieldSizeFormat: Option[String] = errorSizeField(content.head)

    val sizeField: Option[(Int,Int)] = errorFieldSizeFormat match {
      case None =>
        val RegexAnalysis.patternFieldSize(length, width) = content.head
        Some(length.toInt, width.toInt)
      case Some(_) => None
    }

    val vehicles = content.drop(1)
    val positions: List[String] = getEvenIndexElement(vehicles)
    val instructions: List[String] = getOddIndexElement(vehicles)

    val posAnalysisIncorrect: List[String] =  positions.filter(instr => !isVehiclePosValidFormat(instr))
    val posAnalysisErrors: List[Option[String]] = posAnalysisIncorrect.map(s =>
      Some(VehiclePositionFormatIncorrect.errorMessage(s))
    )

    val instrAnalysisIncorrect: List[String] =  instructions.filter(instr => !isInstructionValidFormat(instr))
    val instrAnalysisErrors: List[Option[String]] = instrAnalysisIncorrect.map(s =>
      Some(InstructionFormatIncorrect.errorMessage(s))
    )

    val errors = List(
      List(invalidSizeFormat),
      List(errorFieldSizeFormat),
      isNonEmptyErrorsList(posAnalysisErrors).getOrElse(List()),
      isNonEmptyErrorsList(instrAnalysisErrors).getOrElse(List())
    )

    val flatErrors = errors.flatten.flatten

    if(flatErrors.nonEmpty)
      Left(flatErrors)

    else {
      val vehiclesTuples = positions zip instructions
      val vehicles = vehiclesTuples.map{
        case(pos, instr) =>
        val RegexAnalysis.patternVehiclePos(x, y, orientation) = pos
        Lawnmower(Position(x.toInt, y.toInt, orientation.toCardinal), instr)
      }
      Right(Field(sizeField.get._1, sizeField.get._2, vehicles))
    }
  }

  /** See if a word match the field's size initialisation according to
    * [[fr.upem.easymow.file patternFieldSize]]
    *
    *  @param s the word
    *  @return true if the word match the regex
    */
  def isFieldValidFormat(s: String): Boolean = RegexAnalysis.patternFieldSize matches s

  /** See if a word match the lawnmower's position initialisation according to
    * [[fr.upem.easymow.file patternInstructions]]
    *
    *  @param s the word
    *  @return true if the word match the regex
    */
  def isVehiclePosValidFormat(s: String): Boolean = RegexAnalysis.patternVehiclePos matches s

  /** See if a word match the lawnmower's instruction initialisation according to
    * [[fr.upem.easymow.file patternInstructions]]
    *
    *  @param s the word
    *  @return true if the word match the regex
    */
  def isInstructionValidFormat(s: String): Boolean = RegexAnalysis.patternInstructions matches s

  /** Extract elements at even indexes
    *
    *  @param l the list to extract element
    *  @return a list of elements at even indexes
    */
  def getEvenIndexElement(l: List[String]): List[String] = l.indices.collect { case i if i % 2 == 0 => l(i) }.toList

  /** Extract elements at odd indexes
    *
    *  @param l the list to extract element
    *  @return a list of elements at odd indexes
    */
  def getOddIndexElement(l: List[String]): List[String] = l.indices.collect { case i if i % 2 != 0 => l(i) }.toList

  /** Test if a number of line is valid according to the file's format
    *
    *  @param contentSize number of line of the file
    *  @return an [[fr.upem.easymow.error error message]] if contentSize is even or < 3
    */
  def isValidNumberOfLine(contentSize: Int): Option[String] = contentSize match {
    case c if c < 3 || c%2 == 0 => Some(FileNumberLineWrong.errorMessage(c.toString))
    case _ => None
  }

  /** Test if a field's size initialisation is valid according to
    * [[fr.upem.easymow.file.IO]]
    *
    *  @param content the word to analyze
    *  @return an [[fr.upem.easymow.error.FieldSizeFormatIncorrect error message]] if the format is not valid
    */
  def errorSizeField(content: String): Option[String] = content match {
    case c if !isFieldValidFormat(c) => Some(FieldSizeFormatIncorrect.errorMessage(c))
    case _ => None
  }

  /** Test if a field's size initialisation is valid according to
    * [[fr.upem.easymow.file.IO.isVehiclePosValidFormat ]]
    *
    *  @param content nthe word to analyze
    *  @return an [[fr.upem.easymow.error error message]] if the format is not valid
    */
  def errorPosFormat(content: String): Option[String] = content match {
    case c if !isVehiclePosValidFormat(c) => Some(VehiclePositionFormatIncorrect.errorMessage(c))
    case _ => None
  }

  /** Test a list contains error's messages
    *
    *  @param content a list of potential errors
    *  @return None if content is empty, Some(content) otherwise
    */
  def isNonEmptyErrorsList(content: List[Option[String]]): Option[List[Option[String]]] = content match {
    case c if c.nonEmpty => Some(c)
    case _ => None
  }
}