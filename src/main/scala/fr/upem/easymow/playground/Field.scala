package fr.upem.easymow.playground

import fr.upem.easymow.vehicle._
import fr.upem.easymow.error._
//import org.apache.logging.log4j
//import org.apache.logging.log4j.scala.Logger
//import org.apache.logging.log4j.scala.Logging
//import org.apache.logging.log4j.{Level, LogManager}

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
//  def apply(x : Int, y : Int, vehicles: List[Lawnmower]) =
//    vehicles match {
//      case vehicles if !vehicles.forall(
//        lm => (lm.pos.x != x || lm.pos.y != y) &&  (x >= 0 && y >=0 && x <=length && y <= width) )
//            => Left(AddOutOfBound)
//      case _ => Right(Field(x, y, vehicles))
//    }

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

  def isFreeZone(x: Int, y: Int): Boolean =
    vehicles.forall(lm => lm.pos.x != x || lm.pos.y != y)

  def isInField(x: Int, y: Int): Boolean =
    x >= 0 && y >=0 && x <=length && y <= width
}



//object crashtest {
//  import fr.upem.easymow.error._
//  import org.apache.logging.log4j.scala.Logging
//  val logger: log4j.Logger = LogManager.getLogger(getClass().getName())
//  def main (args: Array[String] ): Unit = {
//    val v = Lawnmower(Position(3, 3, North), "toto")
//    val v2 = Lawnmower(Position(3, 3, North), "toto")
//    val l = List(v, v2)
//    val a = Field(3,3, l)
//   logger.info("toto")
//
//    val b = Field(3,3, List(v)).add(v2)
////    println(a.isInField(0,-1))
//    b match {
//      case Left(s) => logger.warn(s.errorMessage(0,3))
//      case Right(s) => logger.info(s)
//    }
//  }
//}