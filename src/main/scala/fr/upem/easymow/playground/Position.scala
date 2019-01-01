package fr.upem.easymow.playground

/** Position of a [[fr.upem.easymow.vehicle.Lawnmower Lawnmower]]
  * descrive a location on a field and the orientation according
  * to [[fr.upem.easymow.playground.Field Cardinal]]
  *
  *  @param x abscissa
  *  @param y ordinate
  *  @param orientation orientation (North, East, South, West)
  */
case class Position(x: Int, y: Int, orientation: Cardinal)

