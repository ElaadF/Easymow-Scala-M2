package fr.upem.easymow

import fr.upem.easymow.playground._
import fr.upem.easymow.movement.Rotation._
import fr.upem.easymow.vehicle.Lawnmower
import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class RotationTest extends FlatSpec with Matchers with GeneratorDrivenPropertyChecks {

  "Left Rotation according to West" should "return South" in {
    val c: Cardinal = West
    assert(c.leftRotation == South)
  }
  "Left Rotation according to South" should "return East" in {
    val c: Cardinal = South
    assert(c.leftRotation == East)
  }
  "Left Rotation according to East" should "return North" in {
    val c: Cardinal = East
    assert(c.leftRotation == North)
  }
  "Left Rotation according to North" should "return West" in {
    val c: Cardinal = North
    assert(c.leftRotation == West)
  }

  "Right Rotation according to West" should "return North" in {
    val c: Cardinal = West
    assert(c.rightRotation == North)
  }
  "Right Rotation according to North" should "return East" in {
    val c: Cardinal = North
    assert(c.rightRotation == East)
  }
  "Right Rotation according to East" should "return South" in {
    val c: Cardinal = East
    assert(c.rightRotation == South)
  }
  "Right Rotation according to South" should "return West" in {
    val c: Cardinal = South
    assert(c.rightRotation == West)
  }

  "Rotate a lawnmower to the left" should "return a rotated vehicle according to his orientation" in {
    val lmNorth: Lawnmower = Lawnmower(Position(0,0, North),"ADGD")
    val lmSouth: Lawnmower = Lawnmower(Position(0,0, South),"ADGD")
    val lmEast: Lawnmower = Lawnmower(Position(0,0, East),"ADGD")
    val lmWest: Lawnmower = Lawnmower(Position(0,0, West),"ADGD")
    assert(lmNorth.leftRotation == lmWest)
    assert(lmSouth.leftRotation == lmEast)
    assert(lmEast.leftRotation == lmNorth)
    assert(lmWest.leftRotation == lmSouth)
  }

  "Rotate a lawnmower to the right" should "return a rotated vehicle according to his orientation" in {
    val lmNorth: Lawnmower = Lawnmower(Position(0,0, North),"ADGD")
    val lmSouth: Lawnmower = Lawnmower(Position(0,0, South),"ADGD")
    val lmEast: Lawnmower = Lawnmower(Position(0,0, East),"ADGD")
    val lmWest: Lawnmower = Lawnmower(Position(0,0, West),"ADGD")
    assert(lmNorth.rightRotation == lmEast)
    assert(lmSouth.rightRotation == lmWest)
    assert(lmEast.rightRotation == lmSouth)
    assert(lmWest.rightRotation == lmNorth)
  }
}