package fr.upem.easymow.vehicle

import fr.upem.easymow.error._
import fr.upem.easymow.playground._
import fr.upem.easymow.playground.Field
import fr.upem.easymow.movement.Rotation._

/**
  *  @constructor Create a new `Lawnmower` by specifying a
  *               [[fr.upem.easymow.playground.Position Position]]
  *               and `instruction`
  *  @param pos position of the lawnmower
  *  @param instruction list of instructions
  */
case class Lawnmower(pos: Position, instruction : String) {

  /** Move forward the local vehicle
    *
    *  @return the new vehicle
    */
  def moveForward(): Lawnmower = pos.orientation match {
    case North => Lawnmower(Position(pos.x, pos.y + 1, pos.orientation), instruction)
    case South => Lawnmower(Position(pos.x, pos.y - 1, pos.orientation), instruction)
    case East => Lawnmower(Position(pos.x + 1, pos.y, pos.orientation), instruction)
    case West => Lawnmower(Position(pos.x - 1, pos.y, pos.orientation), instruction)
  }

  /** See if the local vehicle can move forward
    *
    *  @param field the field where the vehicle want to move
    *  @return an error's message if the vehicle can't move
    *          None otherwise
    */
  def tryToMoveForward(field: Field): Option[String] = {
    moveForward() match {
      case v if !field.isInField(v.pos.x, v.pos.y) => Some(HitAWall.errorMessage((pos.x, pos.y)))
      case v if !field.isFreeZone(pos.x, pos.y) => Some(HitAVehicle.errorMessage((v.pos.x,v.pos.y)))
      case _ => None
    }
  }

  /** Execute instructions of a Lawnmower
    *
    *  @param field the field where the vehicle want to move
    *  @return a tuple of error's message, lawnmower and the final
    *          field after all process
    *          (List[ErrorMsg and Lawnmower], Field)
    */
  def executeInstructionRec(field: Field): (List[Either[String, Lawnmower]], Field) = {
    def executeInstructionRecAcc(field: Field, v: List[Either[String, Lawnmower]] , instr: List[Char]): (List[Either[String, Lawnmower]], Field) = {
      instr match {
        case x :: xs => x match {
          // move forward
          case 'A'  =>
            //if the last element of v is a left value then the vehicle can't move forward
            v.last match {
              case Right(lm) =>
                Lawnmower.tryToMoveForward(field, lm) match {
                  case None =>

                    val lastRight: Option[Either[String, Lawnmower]] = v.filter(x => x match {
                      case Left(_) => false
                      case Right(_) => true
                    }).lastOption

                    lastRight match {
                      case Some(lm2) =>  lm2 match {
                        case Right(l) => executeInstructionRecAcc (field, v :+ Right(Lawnmower.moveForward(l)), xs)
                        case Left(_) => executeInstructionRecAcc (field, v, xs)
                      }
                      case None => executeInstructionRecAcc(field,v , xs)
                    }
                  case Some(error) => executeInstructionRecAcc(field, v :+ Left(error), xs)
                }
              case Left(error) => executeInstructionRecAcc(field, v :+ Left(error), xs)
            }

          //right rotation
          case 'D' =>

            val lastRight = Lawnmower.getLastRight(v)
            lastRight match {
              case Some(lm) =>  lm match {
                case Right(l) => executeInstructionRecAcc(field, v :+ Right(l.rightRotation), xs)
                case Left(_) => executeInstructionRecAcc(field, v, xs)
              }
              case None => executeInstructionRecAcc(field, v, xs)
            }

          //left rotation
          case 'G' =>

            val lastRight = Lawnmower.getLastRight(v)
            lastRight match {
              case Some(lm) =>  lm match {
                case Right(l) => executeInstructionRecAcc(field, v :+ Right(l.leftRotation), xs)
                case Left(_) => executeInstructionRecAcc(field, v, xs)
              }
              case None => executeInstructionRecAcc(field, v, xs)
            }
        }
        case Nil =>

          val lastRight = Lawnmower.getLastRight(v)
          lastRight match {
            case Some(lm) =>  lm match {
              case Right(l) =>  field.add(l) match {
                case Right(f) => (v, f)
                case Left(_) => (v, field)
              }
              case Left(_) => (v, field)
            }
            case None => (v, field)
          }


      }
    }
    executeInstructionRecAcc(field, List(Right(Lawnmower(Position(pos.x, pos.y, pos.orientation), instruction))), instruction.toUpperCase.toList)
  }
}


/** Companion object of Lawnmower
  * static methods of move forward */
object Lawnmower {

  /** Get the last Right value if he exists
    *
    *  @param v list of trace of the execution
    *  @return the last right value if exists
    *          None otherwise
    */
  def getLastRight(v: List[Either[String, Lawnmower]]) : Option[Either[String, Lawnmower]] = {
    v.filter(x => x match {
      case Left(_) => false
      case Right(_) => true
    }).lastOption
  }
  /** Move forward a vehicle
    *
    *  @param lm the vehicle who wants to move
    *  @return the new vehicle
    */
  def moveForward(lm: Lawnmower): Lawnmower = lm.pos.orientation match {
    case North => Lawnmower(Position(lm.pos.x, lm.pos.y + 1, lm.pos.orientation), lm.instruction)
    case South => Lawnmower(Position(lm.pos.x, lm.pos.y - 1, lm.pos.orientation), lm.instruction)
    case East => Lawnmower(Position(lm.pos.x + 1, lm.pos.y, lm.pos.orientation), lm.instruction)
    case West => Lawnmower(Position(lm.pos.x - 1, lm.pos.y, lm.pos.orientation), lm.instruction)
  }

  /** See if the a vehicle can move forward
    *
    *  @param field the field where the vehicle want to move
    *  @param lm the vehicle who wants to move
    *  @return an error's message if the vehicle can't move
    *          None otherwise
    */
  def tryToMoveForward(field: Field, lm: Lawnmower): Option[String] = {
    moveForward(lm) match {
      case v if !field.isInField(v.pos.x, v.pos.y) => Some(HitAWall.errorMessage((lm.pos.x, lm.pos.y)))
      case v if !field.isFreeZone(v.pos.x, v.pos.y) => Some(HitAVehicle.errorMessage((v.pos.x,v.pos.y)))
      case _ => None
    }
  }
}
