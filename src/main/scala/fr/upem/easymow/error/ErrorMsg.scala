package fr.upem.easymow.error

sealed trait ErrorMsg[A] {
  def errorMessage(a: A): String
}
/** A location on the field is occupied by an other vehicle */
case object AddOccupiedLocation extends ErrorMsg[(Int, Int)] {
  def errorMessage(coordinate: (Int, Int)): String = s"Location $coordinate is already occupied by an other vehicle: vehicles ignored"
}

/** A lawnmower's position is out of the field */
case object AddOutOfBound extends ErrorMsg[(Int, Int)] {
  def errorMessage(coordinate: (Int, Int)): String = s"Location $coordinate is out of bound: vehicles ignored"
}

/** The specified file doesn't exists or a problem occurred when the program try to open it  */
case object ReadFileFailed extends ErrorMsg[String] {
  def errorMessage(path: String): String = s"Read $path have failed"
}

/** A file may always have odd number of line : min -> field's size, lawnmower's position and  lawnmower's instruction */
case object FileNumberLineWrong extends ErrorMsg[String] {
  def errorMessage(nbline: String): String = s"Some instructions may be missing : $nbline instruction found"
}

/** The definition of the field contains a typo */
case object FieldSizeFormatIncorrect extends ErrorMsg[String] {
  def errorMessage(fieldSizeFormat: String): String = s"Field size definition $fieldSizeFormat is incorrect"
}

/** The definition of the lawnmower contains a typo */
case object VehiclePositionFormatIncorrect extends ErrorMsg[String] {
  def errorMessage(vehicleFormat: String): String = s"Initialization format is incorrect : $vehicleFormat"
}

/** The definition of the instructions contains a typo */
case object InstructionFormatIncorrect extends ErrorMsg[String] {
  def errorMessage(instrFormat: String): String = s"Instruction format is incorrect : $instrFormat"
}

/** May not appear in the program, if the cardinal is unknown */
case object UnknownCardinal extends ErrorMsg[String] {
  def errorMessage(card: String): String = s"Cardinal unknown : $card"
}

/** The instruction in the program is unknow, may not appear, program filter the file before create object */
case object UnknownInstruction extends ErrorMsg[String] {
  def errorMessage(instr: String): String = s"Instruction unknown : $instr"
}

/** When a lawnmower decide to move forward in direction of the wall, the instruction is ignored  */
case object HitAWall extends ErrorMsg[(Int, Int)] {
  def errorMessage(coordinate: (Int, Int)): String = s"Vehicle hit a wall at $coordinate: instruction ignored"
}

/** When a lawnmower decide to move forward in direction of an other lawnmower position, the instruction is ignored */
case object HitAVehicle extends ErrorMsg[(Int, Int)] {
  def errorMessage(coordinate: (Int, Int)): String = s"Vehicle hit another vehicle at $coordinate: instruction ignored"
}

/** When two vehicle are at the same position, they are ignored */
case object VehiclesSameLocation extends ErrorMsg[(Int, Int)] {
  def errorMessage(coordinate: (Int, Int)): String = s"Conflict between two vehicles location at $coordinate: vehicles ignored"
}


