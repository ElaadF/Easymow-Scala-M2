package fr.upem.easymow.vehicle

import fr.upem.easymow.playground._

case class Lawnmower(pos: Position, instruction : String) {
  def moveForward(): Either[String,Lawnmower] =
      pos.orientation match {
        case North => Right(Lawnmower(Position(pos.x, pos.y + 1, pos.orientation), instruction))
        case South => Right(Lawnmower(Position(pos.x, pos.y - 1, pos.orientation), instruction))
        case East => Right(Lawnmower(Position(pos.x + 1, pos.y, pos.orientation), instruction))
        case West => Right(Lawnmower(Position(pos.x - 1, pos.y, pos.orientation), instruction))
      }
}
