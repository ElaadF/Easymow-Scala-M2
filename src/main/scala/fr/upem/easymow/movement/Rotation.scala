package fr.upem.easymow.movement
import fr.upem.easymow.playground._
import fr.upem.easymow.vehicle._


trait Rotation[A] {
  def leftRotation(a: A): A
  def rightRotation(a: A): A
}

object RotationTools {

  implicit object CardinalRotation extends Rotation[Cardinal] {
    def leftRotation(card: Cardinal): Cardinal = card match {
      case North => West
      case East => North
      case South => East
      case West => South
    }

    def rightRotation(card: Cardinal): Cardinal = card match {
      case North => East
      case East => South
      case South => West
      case West => North
    }
  }

  implicit object LawnmowerRotation extends Rotation[Lawnmower] {
    def leftRotation(vehicle: Lawnmower): Lawnmower = Lawnmower(Position(vehicle.pos.x, vehicle.pos.y, vehicle.pos.orientation.leftRotation), vehicle.instruction)
    def rightRotation(vehicle: Lawnmower): Lawnmower = Lawnmower(Position(vehicle.pos.x, vehicle.pos.y, vehicle.pos.orientation.rightRotation), vehicle.instruction)
  }

  implicit class RotationUtil[A](x: A) {
    def leftRotation(implicit obj: Rotation[A]): A = {
      obj.leftRotation(x)
    }

    def rightRotation(implicit obj: Rotation[A]): A = {
      obj.rightRotation(x)
    }
  }
}

//object crashtest {
//  import RotationTools._
//  import playground._
//  def main (args: Array[String] ): Unit = {
//    val li = Lawnmower(Position(3, 4, North), "toto")
//  }
//}
