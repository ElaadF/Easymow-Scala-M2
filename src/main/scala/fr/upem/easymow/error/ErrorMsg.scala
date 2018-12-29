package fr.upem.easymow.error
sealed trait ErrorMsg[A] {
  def errorMessage(a: A): String
}

case object AddOccupiedLocation extends ErrorMsg[(Int, Int)] {
  def errorMessage(coordinate: (Int, Int)): String = s"Location $coordinate is already occupied by an other vehicle."
}

case object AddOutOfBound extends ErrorMsg[(Int, Int)] {
  def errorMessage(coordinate: (Int, Int)): String = s"Location $coordinate is out of bound."
}

case object ReadFileFailed extends ErrorMsg[String] {
  def errorMessage(path: String): String = s"Read $path have failed"
}

case object FileNumberLineWrong extends ErrorMsg[String] {
  def errorMessage(nbline: String): String = s"Some instructions may be missing : $nbline instruction found"
}

case object FieldSizeFormatIncorrect extends ErrorMsg[String] {
  def errorMessage(fieldSizeFormat: String): String = s"Field size definition $fieldSizeFormat is incorrect"
}

case object VehiclePositionFormatIncorrect extends ErrorMsg[String] {
  def errorMessage(vehicleFormat: String): String = s"Initialization format is incorrect : $vehicleFormat"
}

case object InstructionFormatIncorrect extends ErrorMsg[String] {
  def errorMessage(instrFormat: String): String = s"Instruction format is incorrect : $instrFormat"
}