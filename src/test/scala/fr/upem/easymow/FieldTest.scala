package fr.upem.easymow

import fr.upem.easymow.error.{AddOccupiedLocation, AddOutOfBound}
import fr.upem.easymow.movement.Rotation._
import fr.upem.easymow.playground.CardinalUtils._
import fr.upem.easymow.playground._
import fr.upem.easymow.vehicle.Lawnmower
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

class FieldTest extends FlatSpec with Matchers with GeneratorDrivenPropertyChecks{

  "a string representing a cardinal, character or word" should "be converted in Cardinal type" in {
    assert("n".toCardinal == North)
    assert("N".toCardinal == North)
    assert("North".toCardinal == North)
    assert("NORTH".toCardinal == North)

    assert("s".toCardinal == South)
    assert("S".toCardinal == South)
    assert("South".toCardinal == South)
    assert("SOUTH".toCardinal == South)

    assert("e".toCardinal == East)
    assert("E".toCardinal == East)
    assert("East".toCardinal == East)
    assert("EAST".toCardinal == East)

    assert("w".toCardinal == West)
    assert("W".toCardinal == West)
    assert("West".toCardinal == West)
    assert("WEST".toCardinal == West)
  }

  "Add a lawnmower to a field" should "be impossible if the location is occupied" in {
    val field: Field = Field(5,5,List(Lawnmower(Position(0,0, North),"ADGD")))
    val lm: Lawnmower = Lawnmower(Position(0,0, West),"AGDDGAAA")
    assert(
      field.add(lm) match {
        case Left(err) =>
          assert(err == AddOccupiedLocation)
          true
        case Right(_) => false
      }
    )
  }

  "Add a lawnmower to a field" should "be impossible if his position is out of bound" in {
    val field: Field = Field(5,5,List(Lawnmower(Position(0,0, North),"ADGD")))
    val lm: Lawnmower = Lawnmower(Position(8943,43, West),"AGDDGAAA")
    assert(
      field.add(lm) match {
        case Left(err) =>
          assert(err == AddOutOfBound)
          true
        case Right(_) => false
      }
    )
  }

  "Add a lawnmower to a field" should "be succeed if he is not of bound and the location is free" in {
    val field: Field = Field(5,5,List(Lawnmower(Position(0,0, North),"ADGD")))
    val lm: Lawnmower = Lawnmower(Position(1,1, West),"AGDDGAAA")
    assert(
      field.add(lm) match {
        case Left(_) => false
        case Right(f) =>
          assert(f == Field(5,5,List(Lawnmower(Position(0,0, North),"ADGD"), lm)))
          true
      }
    )
  }

  "Tree invalids vehicles" should "be returned according to the field" in {
    val lm1: Lawnmower = Lawnmower(Position(1,1, West),"AGDDGAAA")
    val lm2: Lawnmower = Lawnmower(Position(1,1, South),"ADDG")
    val lm3: Lawnmower = Lawnmower(Position(2,1, West),"A")
    val lm4: Lawnmower = Lawnmower(Position(89,89, West),"GGGGG")
    val lmExceptInvalid = List(lm1, lm2, lm4)
    val field: Field = Field(5,5,List(lm1, lm2, lm3, lm4))

    assert(
      field.getInvalidVehicles match {
        case l if l.isEmpty => false
        case l =>
          assert(lmExceptInvalid == l)
          true
      }
    )
  }

  "None invalid vehicle" should "be returned according to the field" in {
    val lm1: Lawnmower = Lawnmower(Position(3,1, West),"AGDDGAAA")
    val lm2: Lawnmower = Lawnmower(Position(4,1, South),"ADDG")
    val lm3: Lawnmower = Lawnmower(Position(2,1, West),"A")
    val lm4: Lawnmower = Lawnmower(Position(5,5, West),"GGGGG")
    val field: Field = Field(5,5,List(lm1, lm2, lm3, lm4))

    assert(
      field.getInvalidVehicles match {
        case l if l.isEmpty => true
        case _ => false
      }
    )
  }

  "(0,0)" should "be a free zone in the field" in {
    val lm1: Lawnmower = Lawnmower(Position(3,1, West),"AGDDGAAA")
    val lm2: Lawnmower = Lawnmower(Position(4,1, South),"ADDG")
    val lm3: Lawnmower = Lawnmower(Position(2,1, West),"A")
    val lm4: Lawnmower = Lawnmower(Position(5,5, West),"GGGGG")
    val field: Field = Field(5,5,List(lm1, lm2, lm3, lm4))
    assert(field.isFreeZone(0,0))
  }

  "(0,0)" should "be an occupied zone" in {
    val lm1: Lawnmower = Lawnmower(Position(0,0, West),"AGDDGAAA")
    val lm2: Lawnmower = Lawnmower(Position(4,1, South),"ADDG")
    val lm3: Lawnmower = Lawnmower(Position(2,1, West),"A")
    val lm4: Lawnmower = Lawnmower(Position(5,5, West),"GGGGG")
    val field: Field = Field(5,5,List(lm1, lm2, lm3, lm4))
    assert(!field.isFreeZone(0,0))
  }

  "(4,1)" should "in the field" in {
    val lm1: Lawnmower = Lawnmower(Position(3,1, West),"AGDDGAAA")
    val field: Field = Field(5,5,List(lm1))
    assert(field.isInField(4,1))
  }

  "(42,42)" should "be out of the field" in {
    val lm1: Lawnmower = Lawnmower(Position(0,0, West),"AGDDGAAA")
    val field: Field = Field(5,5,List(lm1))
    assert(!field.isInField(42,42))
  }

  "The lawnmower" should "be in a free zone in the specified field" in {
    val lm1: Lawnmower = Lawnmower(Position(3,1, West),"AGDDGAAA")
    val lm2: Lawnmower = Lawnmower(Position(4,1, South),"ADDG")
    val lm3: Lawnmower = Lawnmower(Position(2,1, West),"A")
    val lm4: Lawnmower = Lawnmower(Position(5,5, West),"GGGGG")
    val field: Field = Field(5,5,List(lm1, lm2, lm3))
    assert(Field.isFreeZone(field, lm4))
  }

  "The lawnmower" should "be in a occupied zone in the specified field" in {
    val lm1: Lawnmower = Lawnmower(Position(3,1, West),"AGDDGAAA")
    val lm2: Lawnmower = Lawnmower(Position(4,1, South),"ADDG")
    val lm3: Lawnmower = Lawnmower(Position(2,1, West),"A")
    val lm4: Lawnmower = Lawnmower(Position(2,1, West),"GGGGG")
    val field: Field = Field(5,5,List(lm1, lm2, lm3))
    assert(Field.isFreeZone(field, lm4))
  }

  "Computing field" should "return the final field and list of process and errors" in {
    val lm1: Lawnmower = Lawnmower(Position(0,5, West),"DAADAGA")
    val lm2: Lawnmower = Lawnmower(Position(2,5, West),"a")
    val field: Field = Field(5,5,List(lm1, lm2))
    val fieldExcepted: Field = Field(5,5,List(
      lm1.rightRotation.rightRotation.moveForward().leftRotation,
      lm2
    ))
    val processExcepted = List (
      Right(lm1),
      Right(lm1.rightRotation),
      Left("Vehicle hit a wall at (0,5): instruction ignored"),
      Left("Vehicle hit a wall at (0,5): instruction ignored"),
      Right(lm1.rightRotation.rightRotation),
      Right(lm1.rightRotation.rightRotation.moveForward()),
      Right(lm1.rightRotation.rightRotation.moveForward().leftRotation),
      Left("Vehicle hit a wall at (1,5): instruction ignored"),
      Right(lm2),
      Left("Vehicle hit another vehicle at (1,5): instruction ignored")
    )

    val fieldComputeAndField: (List[Either[String, Lawnmower]], Field) = field.computeField
    assert(fieldComputeAndField._1.nonEmpty)
    assert(fieldComputeAndField._1 == processExcepted)
    assert(fieldExcepted == fieldComputeAndField._2)
  }


//  def computeField: (List[Either[String, Lawnmower]], Field) = {
//    def computeFieldAcc(f: Field, l: List[Lawnmower], res: List[Either[String, Lawnmower]]): (List[Either[String, Lawnmower]], Field) = {
//      l match {
//        case x :: xs =>
//          val fieldCopy = f.copy(vehicles = f.vehicles diff List(x))
//          val resAndField = x.executeInstructionRec(fieldCopy)
//          computeFieldAcc(resAndField._2, xs, res ::: resAndField._1)
//        case Nil => (res, f)
//      }
//    }
//    computeFieldAcc(field, field.vehicles, List())
//  }
//}
//getInvalidVehicles: List[Lawnmower]
  //add(lm: Lawnmower): Either[ErrorMsg[(Int, Int)], Field]
}
