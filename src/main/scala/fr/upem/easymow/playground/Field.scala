package fr.upem.easymow.playground

import fr.upem.easymow.vehicle._
import fr.upem.easymow.error._

/** Enum of Cardinal */
sealed trait Cardinal


/** North  */ case object North extends Cardinal
/** East  */ case object East extends Cardinal
/** South  */ case object South extends Cardinal
/** West  */ case object West extends Cardinal

/** Tools apply to Cardinal */
object CardinalUtils {

  /** Transform implicitly a char or a word to a Cardinal
    * North is the cardinal by default
    *
    *  @param str the word or the character to transform
    */
  implicit class StrToCardinal(val str: String) extends AnyVal {

    /** Transform a word or a character to Cardinal
      *
      *  @return the cardinal associated
      */
    def toCardinal: Cardinal = str.toUpperCase match {
      case "N" => North
      case "NORTH" => North
      case "S" => South
      case "SOUTH" => South
      case "E" => East
      case "EAST" => East
      case "W" => West
      case "WEST" => West
      case _ => North
    }
  }
}

/**
  *  @constructor Create a new `Field` by specifying the `length`, `width`,
  *               and list of [[fr.upem.easymow.vehicle.Lawnmower Lawnmower]]
  *  @param length upper right corner ordinate
  *  @param width upper right corner abscissa
  *  @param vehicles list of vehicles on the field
  */
case class Field(length : Int, width : Int, vehicles : List[Lawnmower] = List[Lawnmower]()){

  /** Add a lawnmower to the field
    *
    *  @param lm the lawnmower to add
    *
    *  @return an error's message if the location is occupied
    *          by another vehicle or if the vehicle is out of bound,
    *          otherwise a new Field we the lawnmower added
    */
  def add(lm: Lawnmower): Either[ErrorMsg[(Int, Int)], Field] =
    lm match {
      case l if !isInField(l.pos.x, l.pos.y) => Left(AddOutOfBound)
      case l if !isFreeZone(l.pos.x, l.pos.y) => Left(AddOccupiedLocation)
      case _ => Right(Field(length, width, vehicles :+ lm))
    }

  /** Get vehicles who is not conform to the rules of a Field
    * 1) two lawnmower can't share same location
    * 2) a lawnmower must be in the field's area
    *
    *  @return a list of invalid vehicles according to the rules
    */
  def getInvalidVehicles: List[Lawnmower] = vehicles.filter(lm =>
    !Field.isFreeZone(Field(length, width, vehicles diff List(lm)), lm)
      || !Field.isInField(Field(length, width, vehicles diff List(lm)), lm)
  )

  /** See if a location in the field is free
    *
    *  @param x abscissa of the location to verify
    *  @param y ordinate of the location to verify
    *  @return true the location is free
    *          false otherwise
    */
  def isFreeZone(x: Int, y: Int): Boolean =
    vehicles.forall(lm => lm.pos.x != x || lm.pos.y != y)

  /** See if a location is in the field
    *
    *  @param x abscissa of the location to verify
    *  @param y ordinate of the location to verify
    *  @return true the location is in the field
    *          false otherwise
    */
  def isInField(x: Int, y: Int): Boolean =
    x >= 0 && y >=0 && x <=length && y <= width
}

/** Companion object of [[fr.upem.easymow.playground.Field Field]]
  * contains static methods and implicit class to process vehicles
  * instructions
  * */
object Field {

  /** See if a lawnmower can occupied a location in the field
    *
    *  @param field the field which the lawnmower want to go in
    *  @param lm the lawnmower who want to go in the field
    *  @return true the location is free
    *          false otherwise
    */
  def isFreeZone(field: Field, lm: Lawnmower): Boolean =
    field.vehicles.forall(v => v.pos.x != lm.pos.x || v.pos.y != lm.pos.y)

  /** See if a lawnmower is still in the field
    *
    *  @param field the field which the lawnmower want to go in
    *  @param lm the lawnmower who want to go in the field
    *  @return true the location is in the field
    *          false otherwise
    */
  def isInField(field: Field, lm: Lawnmower): Boolean =
    lm.pos.x >= 0 && lm.pos.y >=0 && lm.pos.x <=field.length && lm.pos.y <= field.width

  /** Implicit class to process all the vehicles's instructions
    *
    *  @param field containing the vehicles to process
    */
    implicit class Compute(field: Field) {

    /** Process all the vehicles's instructions in a field
      *
      *  @return a tuple of error's message, lawnmower and the final
      *          field after all process
      *          (List[ErrorMsg and Lawnmower], Field)
      */
      def computeField: (List[Either[String, Lawnmower]], Field) = {
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



}