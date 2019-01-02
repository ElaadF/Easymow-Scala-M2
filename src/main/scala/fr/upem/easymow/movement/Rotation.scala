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
object Rotation {

  def apply[A](implicit e: Rotation[A]): Rotation[A] = e

  def leftRotation[A: Rotation](a: A): A = Rotation[A].leftRotation(a)
  def rightRotation[A: Rotation](a: A): A = Rotation[A].rightRotation(a)

  implicit class RotateVehicle[A: Rotation](a: A) {
    def leftRotation: A = Rotation[A].leftRotation(a)
    def rightRotation: A = Rotation[A].rightRotation(a)
  }

  implicit val CardRotation: Rotation[Cardinal] =
    new Rotation[Cardinal] {
      /** Give the Cardinal to the left of the cardinal specified
        *
        *  @return the cardinal to the left of the actual orientation
        */
      def leftRotation(card: Cardinal): Cardinal = card match {
        case North => West
        case East => North
        case South => East
        case West => South
      }

      /** Give the Cardinal to the right of the cardinal specified
        *
        *  @return the cardinal to the right of the actual orientation
        */
      def rightRotation(card: Cardinal): Cardinal = card match {
        case North => East
        case East => South
        case South => West
        case West => North
      }
    }

  implicit val LawmowerRotation: Rotation[Lawnmower] =
    new Rotation[Lawnmower] {
      /** Rotate a Lawnmower to the left
        *
        *  @return the lawnmower after being rotate to the left
        */
      def leftRotation(lm: Lawnmower): Lawnmower =
        lm.copy(lm.pos.copy(orientation = lm.pos.orientation.leftRotation))

      /** Rotate a Lawnmower to the right
        *
        *  @return the lawnmower after being rotate to the right
        */
      def rightRotation(lm: Lawnmower): Lawnmower =
        lm.copy(lm.pos.copy(orientation = lm.pos.orientation.rightRotation))
    }
}