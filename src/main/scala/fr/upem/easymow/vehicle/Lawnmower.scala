package fr.upem.easymow.vehicle

import fr.upem.easymow.error.{AddOccupiedLocation, AddOutOfBound, UnknownCardinal, UnknownInstruction}
import fr.upem.easymow.playground._
import fr.upem.easymow.playground.Field
import fr.upem.easymow.movement.RotationTools._

case class Lawnmower(pos: Position, instruction : String) {
  def moveForward(): Either[String,Lawnmower] = pos.orientation match {
    case North => Right(Lawnmower(Position(pos.x, pos.y + 1, pos.orientation), instruction))
    case South => Right(Lawnmower(Position(pos.x, pos.y - 1, pos.orientation), instruction))
    case East => Right(Lawnmower(Position(pos.x + 1, pos.y, pos.orientation), instruction))
    case West => Right(Lawnmower(Position(pos.x - 1, pos.y, pos.orientation), instruction))
    case unknown => Left(UnknownCardinal.errorMessage(unknown.toString))
  }

  def canMoveForward(field: Field): Option[String] = pos match {
    case pos if !field.isInField(pos.x, pos.y) => Some(AddOutOfBound.errorMessage(pos.x, pos.y))
    case pos if !field.isFreeZone(pos.x, pos.y) => Some(AddOccupiedLocation.errorMessage(pos.x,pos.y))
    case _ => None
  }

  def executeInstruction(field: Field): Either[String, Lawnmower] = instruction.map(c => c.toUpper match{
    case 'A' => canMoveForward(field) match {
      case None => println(s"MOVE FORARD : $moveForward()");moveForward
      case Some(value) => Left(value)
    }
    case 'D' => Right(Lawnmower(Position(pos.x,pos.y, pos.orientation.rightRotation), instruction))
    case 'G' => Right(Lawnmower(Position(pos.x,pos.y, pos.orientation.leftRotation), instruction))
    case _ => Left(UnknownInstruction.errorMessage(c.toString))
  }).head




}
