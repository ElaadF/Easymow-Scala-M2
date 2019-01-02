package fr.upem.easymow

import fr.upem.easymow.playground._
import fr.upem.easymow.vehicle.Lawnmower
import fr.upem.easymow.movement.Rotation._
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

class LawnmowerTest extends FlatSpec with Matchers with GeneratorDrivenPropertyChecks {

  "Create Lawnmower" should "return a right Lawnmower object" in {
    forAll ("x", "y", "str") { (x: Int, y: Int, str: String) =>
      val lm1: Lawnmower =  Lawnmower(Position(x,y,West), str)
      assert(lm1.pos.x == x)
      assert(lm1.pos.y == y)
      assert(lm1.pos.orientation == West)
      assert(lm1.instruction == str)
    }
  }

  "Move forward a lawnmower" should "increase position according to the orientation" in {
    forAll ("x", "y", "str") { (x: Int, y: Int, str: String) =>
      val lm1: Lawnmower =  Lawnmower(Position(x,y,West), str)
      val lm2: Lawnmower =  Lawnmower(Position(x,y,East), str)
      val lm3: Lawnmower =  Lawnmower(Position(x,y,North), str)
      val lm4: Lawnmower =  Lawnmower(Position(x,y,South), str)

      // move towards the West
      assert(lm1.moveForward().pos.x == x - 1)
      assert(lm1.moveForward().pos.y == y)
      // move towards the East
      assert(lm2.moveForward().pos.x == x + 1)
      assert(lm2.moveForward().pos.y == y)
      // move towards the North
      assert(lm3.moveForward().pos.x == x)
      assert(lm3.moveForward().pos.y == y + 1)
      // move towards the South
      assert(lm4.moveForward().pos.x == x)
      assert(lm4.moveForward().pos.y == y - 1)
    }
  }

  "Try move forward against wall" should "failed" in {
    val lm: Lawnmower = Lawnmower(Position(5,5,East), "AGGDDG")
    val field: Field = Field(5,5,List())
    assert(
      lm.tryToMoveForward(field) match {
        case Some(err) =>
          assert(err == "Vehicle hit a wall at (5,5): instruction ignored")
          true
        case None => false
      }
    )
  }

  "Try move forward against a lawnmower" should "failed" in {
    val lm: Lawnmower = Lawnmower(Position(4,5,East), "AGGDDG")
    val field: Field = Field(5,5,List(lm))
    assert(
      lm.tryToMoveForward(field) match {
        case Some(err) =>
          assert(err == "Vehicle hit another vehicle at (5,5): instruction ignored")
          true
        case None => false
      }
    )
  }

  "Try move forward" should "succeed" in {
    val lm: Lawnmower = Lawnmower(Position(4,5,East), "AGGDDG")
    val field: Field = Field(5,5,List())
    assert(
      lm.tryToMoveForward(field) match {
        case Some(_) => false
        case None => true
      }
    )
  }

  "Execute instruction" should "return the final field and list of process and errors" in {
    val lm1: Lawnmower = Lawnmower(Position(0,5, West),"DAADAGA")
    val lm2: Lawnmower = Lawnmower(Position(2,5, West),"a")

    val field: Field = Field(5,5,List(lm2))
    val fieldExcepted: Field = Field(5,5,List(
      lm2,
      lm1.rightRotation.rightRotation.moveForward().leftRotation
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
    )

    val fieldComputeAndField: (List[Either[String, Lawnmower]], Field) = lm1.executeInstructionRec(field)
    assert(fieldComputeAndField._1.nonEmpty)
    assert(fieldComputeAndField._1 == processExcepted)
    assert(fieldExcepted == fieldComputeAndField._2)
  }

  "Getting the last right value in a list containing a right value" should "return a right value" in {
    val lm: Lawnmower = Lawnmower(Position(0,5, West),"DAADAGA")
    assert(
      Lawnmower.getLastRight(List(Left("err"), Right(lm), Left("berk"))) match {
        case None => false
        case Some(l) =>
          assert(l == Right(lm))
          true
      }
    )
  }

  "Getting the last right value in a list without right value" should "return None" in {
    assert(
      Lawnmower.getLastRight(List(Left("err"), Left("berk"))) match {
        case None => true
        case Some(_) => false
      }
    )
  }

  "Move forward static lawnmower" should "lawnmower with the position updated" in {
    forAll ("x", "y", "str") { (x: Int, y: Int, str: String) =>
      val lm1: Lawnmower = Lawnmower(Position(x, y, West), str)
      val lm2: Lawnmower = Lawnmower(Position(x, y, East), str)
      val lm3: Lawnmower = Lawnmower(Position(x, y, North), str)
      val lm4: Lawnmower = Lawnmower(Position(x, y, South), str)

      // move towards the West
      assert(Lawnmower.moveForward(lm1).pos.x == x - 1)
      assert(Lawnmower.moveForward(lm1).pos.y == y)
      // move towards the East
      assert(Lawnmower.moveForward(lm2).pos.x == x + 1)
      assert(Lawnmower.moveForward(lm2).pos.y == y)
      // move towards the North
      assert(Lawnmower.moveForward(lm3).pos.x == x)
      assert(Lawnmower.moveForward(lm3).pos.y == y + 1)
      // move towards the South
      assert(Lawnmower.moveForward(lm4).pos.x == x)
      assert(Lawnmower.moveForward(lm4).pos.y == y - 1)
    }
  }



  "Try move forward static against wall" should "failed" in {
    val lm: Lawnmower = Lawnmower(Position(5,5,East), "AGGDDG")
    val field: Field = Field(5,5,List())
    assert(
      Lawnmower.tryToMoveForward(field, lm) match {
        case Some(err) =>
          assert(err == "Vehicle hit a wall at (5,5): instruction ignored")
          true
        case None => false
      }
    )
  }

  "Try move forward static against a lawnmower" should "failed" in {
    val lm: Lawnmower = Lawnmower(Position(4,5,East), "AGGDDG")
    val field: Field = Field(5,5,List(Lawnmower(Position(5,5,East), "AG")))
    assert(
      Lawnmower.tryToMoveForward(field, lm) match {
        case Some(err) =>
          assert(err == "Vehicle hit another vehicle at (5,5): instruction ignored")
          true
        case None => false
      }
    )
  }

  "Try move forward static" should "succeed" in {
    val lm: Lawnmower = Lawnmower(Position(4,5,East), "AGGDDG")
    val field: Field = Field(5,5,List())
    assert(
      Lawnmower.tryToMoveForward(field, lm) match {
        case Some(_) => false
        case None => true
      }
    )
  }
}
