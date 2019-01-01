package fr.upem.easymow.vehicle

import cats.Show
import cats.syntax.show._
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
      case v if !field.isFreeZone(pos.x, pos.y) => Some(HitAVehicle.errorMessage((v.pos.x,v.pos.y)))
      case _ => None
    }
  }

  def executeInstructionRec(field: Field): (List[Either[String, Lawnmower]], Field) = {
    def executeInstructionRecAcc(field: Field, v: List[Either[String, Lawnmower]] , instr: List[Char]): (List[Either[String, Lawnmower]], Field) = {
      instr match {
        case x :: xs => x match {
          // move forward
          case 'A'  =>
            //if the last element of v is a left value then the vehicle can't move forward
            v.last match {
              case Right(lm) =>
                Lawnmower.canMoveForward(field, lm) match {
                  case None =>

                    val lastRight: Option[Either[String, Lawnmower]] = v.filter(x => x match {
                      case Left(_) => false
                      case Right(_) => true
                    }).lastOption

                    lastRight match {
                      case Some(lm2) =>  lm2 match {
                        case Right(l) => executeInstructionRecAcc (field, v :+ Right(Lawnmower.moveForward(l)), xs)
                      }
                      case None => executeInstructionRecAcc(field,v , xs)
                    }
                  case Some(error) => executeInstructionRecAcc(field, v :+ Left(error), xs)
                }
              case Left(error) => executeInstructionRecAcc(field, v :+ Left(error), xs)
            }

          //right rotation
          case 'D' =>
            val lastRight: Option[Either[String, Lawnmower]] = v.filter(x => x match {
              case Left(_) => false
              case Right(_) => true
            }).lastOption

            lastRight match {
              case Some(lm) =>  lm match {
                case Right(l) => executeInstructionRecAcc(field, v :+ Right(Lawnmower(Position (l.pos.x, l.pos.y, l.pos.orientation.rightRotation), instruction)), xs)
              }
              case None => executeInstructionRecAcc(field, v, xs)
            }

          //left rotation
          case 'G' =>
            val lastRight: Option[Either[String, Lawnmower]] = v.filter(x => x match {
              case Left(_) => false
              case Right(_) => true
            }).lastOption

            lastRight match {
              case Some(lm) =>  lm match {
                case Right(l) => executeInstructionRecAcc(field, v :+ Right(Lawnmower(Position (l.pos.x, l.pos.y, l.pos.orientation.leftRotation), instruction)), xs)
              }
              case None => executeInstructionRecAcc(field, v, xs)
            }
        }
        case Nil =>
          val lastRight: Option[Either[String, Lawnmower]] = v.filter(x => x match {
            case Left(_) => false
            case Right(_) => true
          }).lastOption

          lastRight match {
            case Some(lm) =>  lm match {
              case Right(l) =>  field.add(l) match {
                case Right(f) => (v, f)
              }
            }
            case None => (v, field)
          }


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
      case v if !field.isFreeZone(v.pos.x, v.pos.y) => Some(HitAVehicle.errorMessage((v.pos.x,v.pos.y)))
      case _ => None
    }
  }
}
