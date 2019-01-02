package fr.upem.easymow.movement
import fr.upem.easymow.playground._
import fr.upem.easymow.vehicle._

/** Rotation on [[fr.upem.easymow.playground.Field Cardinal]]
  * and [[fr.upem.easymow.vehicle.Lawnmower Lawnmower]] */
@scala.annotation.implicitNotFound("No way to Rotate ${A}." +
  "An implicit Rotation[${A}] must be in scope")
trait Rotation[A] {
  def leftRotation(a: A): A
  def rightRotation(a: A): A
}

/** Contains rotations for [[fr.upem.easymow.playground.Field Cardinal]] */
object RotationCardinal {

  implicit class RotateCard(c: Cardinal) {
    /** Give the Cardinal to the left of the cardinal specified
      *
      *  @return the cardinal to the left of the actual orientation
      */
    def leftRotation: Cardinal = c match {
      case North => West
      case East => North
      case South => East
      case West => South
    }

    /** Give the Cardinal to the right of the cardinal specified
      *
      *  @return the cardinal to the right of the actual orientation
      */
    def rightRotation: Cardinal = c match {
      case North => East
      case East => South
      case South => West
      case West => North
    }
  }
}

/** Contains rotations for [[fr.upem.easymow.vehicle.Lawnmower Lawnmower]] */
object RotationLawnmower {
  implicit class RotateLm(lm: Lawnmower) {
    import RotationCardinal.RotateCard

    /** Rotate a Lawnmower to the left
      *
      *  @return the lawnmower after being rotate to the left
      */
    def leftRotation: Lawnmower = lm.copy(lm.pos.copy(orientation = lm.pos.orientation.leftRotation))

    /** Rotate a Lawnmower to the right
      *
      *  @return the lawnmower after being rotate to the right
      */
    def rightRotation: Lawnmower = lm.copy(lm.pos.copy(orientation = lm.pos.orientation.rightRotation))
  }
}
