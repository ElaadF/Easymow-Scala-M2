package fr.upem.easymow.vehicle

import fr.upem.easymow.error._
import fr.upem.easymow.playground._
import fr.upem.easymow.playground.Field
import fr.upem.easymow.movement.RotationTools._

case class Lawnmower(pos: Position, instruction : String) {
  def moveForward(): Lawnmower = pos.orientation match {
    case North => Lawnmower(Position(pos.x, pos.y + 1, pos.orientation), instruction)
    case South => Lawnmower(Position(pos.x, pos.y - 1, pos.orientation), instruction)
    case East => Lawnmower(Position(pos.x + 1, pos.y, pos.orientation), instruction)
    case West => Lawnmower(Position(pos.x - 1, pos.y, pos.orientation), instruction)
  }

  def canMoveForward(field: Field): Option[String] = {
    moveForward() match {
      case v if !field.isInField(v.pos.x, v.pos.y) => Some(HitAWall.errorMessage((pos.x, pos.y)))
      case v if !field.isFreeZone(pos.x, pos.y) => Some(HitAVehicle.errorMessage((v.pos.x,v.pos.x)))
      case _ => None
    }
  }

  def executeInstructionRec(field: Field): List[Either[String, Lawnmower]] = {
    def executeInstructionRecAcc(field: Field, v: List[Either[String, Lawnmower]] , instr: List[Char]): List[Either[String, Lawnmower]] = {
      instr match {
        case x :: xs => x match {
          case 'A'  => canMoveForward(field) match {
            case None =>  v.last match {
              case Left(_) => executeInstructionRecAcc(field,v , xs)
               case Right(lm) => executeInstructionRecAcc (field, v :+ Right(Lawnmower.moveForward(lm)), xs)
              }
            case Some(error) => executeInstructionRecAcc(field, v :+ Left(error), xs)
          }
          case 'D' => v.last match {
            case Left(_) => executeInstructionRecAcc(field,v , xs)
            case Right(lm) => executeInstructionRecAcc(field, v :+ Right(Lawnmower(Position (lm.pos.x, lm.pos.y, lm.pos.orientation.rightRotation), instruction)), xs)
          }
          case 'G' => v.last match {
            case Left(_) => executeInstructionRecAcc(field, v, xs)
            case Right(lm) => executeInstructionRecAcc(field, v :+ Right(Lawnmower(Position (lm.pos.x, lm.pos.y, lm.pos.orientation.leftRotation), instruction)), xs)
          }
        }
        case Nil => v
      }
    }
    executeInstructionRecAcc(field, List(Right(Lawnmower(Position(pos.x, pos.y, pos.orientation), instruction))), instruction.toUpperCase.toList)
  }
}

object Lawnmower {
  def moveForward(lm: Lawnmower): Lawnmower = lm.pos.orientation match {
    case North => Lawnmower(Position(lm.pos.x, lm.pos.y + 1, lm.pos.orientation), lm.instruction)
    case South => Lawnmower(Position(lm.pos.x, lm.pos.y - 1, lm.pos.orientation), lm.instruction)
    case East => Lawnmower(Position(lm.pos.x + 1, lm.pos.y, lm.pos.orientation), lm.instruction)
    case West => Lawnmower(Position(lm.pos.x - 1, lm.pos.y, lm.pos.orientation), lm.instruction)
  }

  def canMoveForward(field: Field, lm: Lawnmower): Option[String] = {
    moveForward(lm) match {
      case v if !field.isInField(v.pos.x, v.pos.y) => Some(HitAWall.errorMessage((lm.pos.x, lm.pos.y)))
      case v if !field.isFreeZone(v.pos.x, v.pos.y) => Some(HitAVehicle.errorMessage((v.pos.x,v.pos.x)))
      case _ => None
    }
  }
}
