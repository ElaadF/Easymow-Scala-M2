package fr.upem.easymow.playground

import fr.upem.easymow.vehicle._
import fr.upem.easymow.error._

sealed trait Cardinal

case object North extends Cardinal
case object East extends Cardinal
case object South extends Cardinal
case object West extends Cardinal

object CardinalUtils {
  implicit class StrToCardinal(val str: String) extends AnyVal {
    def toCardinal: Cardinal = str.toUpperCase match {
      case "N" => North
      case "NORTH" => North
      case "S" => South
      case "SOUTH" => South
      case "E" => East
      case "EAST" => East
      case "W" => West
      case "WEST" => West
    }
  }
}

case class Field(length : Int, width : Int, vehicles : List[Lawnmower] = List[Lawnmower]()){

  def add(lm: Lawnmower): Either[ErrorMsg[(Int, Int)], Field] =
    lm match {
      case l if !isInField(l.pos.x, l.pos.y) => Left(AddOutOfBound)
      case l if !isFreeZone(l.pos.x, l.pos.y) => Left(AddOccupiedLocation)
      case _ => Right(Field(length, width, vehicles :+ lm))
    }
//    Either.cond(
//      isFreeZone(lm.pos.x, lm.pos.y) ,
//      Field(length, width, vehicles :+ lm),
//      AddOccupiedLocation
//    )

  def isValidPlayground: List[Lawnmower] = vehicles.filter(lm => !Field.isFreeZone(Field(length, width, vehicles diff List(lm)), lm) || !Field.isInField(Field(length, width, vehicles diff List(lm)), lm))

  def isFreeZone(x: Int, y: Int): Boolean =
    vehicles.forall(lm => lm.pos.x != x || lm.pos.y != y)

  def isInField(x: Int, y: Int): Boolean =
    x >= 0 && y >=0 && x <=length && y <= width
}

object Field {

  def isFreeZone(field: Field, lm: Lawnmower): Boolean =
    field.vehicles.forall(v => v.pos.x != lm.pos.x || v.pos.y != lm.pos.y)

  def isInField(field: Field, lm: Lawnmower): Boolean =
    lm.pos.x >= 0 && lm.pos.y >=0 && lm.pos.x <=field.length && lm.pos.y <= field.width

  def computeField(field: Field): (List[Either[String, Lawnmower]], Field) = {
    def computeFieldAcc(f: Field, l: List[Lawnmower], res: List[Either[String, Lawnmower]]): (List[Either[String, Lawnmower]], Field) = {
      l match {
        case x :: xs =>
          val fieldCopy = f.copy(vehicles = f.vehicles diff List(x))
          val resAndField = x.executeInstructionRec(fieldCopy)
          computeFieldAcc(resAndField._2, xs, res ::: resAndField._1)
        case Nil => (res, f)
      }
    }
    computeFieldAcc(field, field.vehicles, List())
  }
}