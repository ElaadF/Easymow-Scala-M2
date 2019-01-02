package fr.upem.easymow

import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}
import fr.upem.easymow.error._

class ErrorMsgTest extends FlatSpec with Matchers with GeneratorDrivenPropertyChecks {

  "Add Occupied location error" should "be returned" in {
    forAll("x", "y") { (x: Int, y: Int) =>
      AddOccupiedLocation.errorMessage((x, y)) should be(s"Location ${(x, y)} is already occupied by an other vehicle: vehicles ignored")
    }
  }

  "Add out of bound location error" should "be returned" in {
    forAll("x", "y") { (x: Int, y: Int) =>
      AddOutOfBound.errorMessage((x, y)) should be(s"Location ${(x, y)} is out of bound: vehicles ignored")
    }
  }

  "Read file error" should "be returned" in {
    forAll("path") { path: String =>
      ReadFileFailed.errorMessage(path) should be(s"Read $path have failed")
    }
  }

  "Number of line in file error" should "be returned" in {
    forAll("nbline") { nbline: String =>
      FileNumberLineWrong.errorMessage(nbline) should be(s"Some instructions may be missing : $nbline instruction found")
    }
  }

  "Field size format error" should "be returned" in {
    forAll("fieldSizeFormat") { fieldSizeFormat: String =>
      FieldSizeFormatIncorrect.errorMessage(fieldSizeFormat) should be(s"Field size definition $fieldSizeFormat is incorrect")
    }
  }

  "Vehicule position format error" should "be returned" in {
    forAll("vehicleFormat") { vehicleFormat: String =>
      VehiclePositionFormatIncorrect.errorMessage(vehicleFormat) should be(s"Initialization format is incorrect : $vehicleFormat")
    }
  }

  "Instruction format error" should "be returned" in {
    forAll("vehicleFormat") { instrFormat: String =>
      InstructionFormatIncorrect.errorMessage(instrFormat) should be(s"Instruction format is incorrect : $instrFormat")
    }
  }

  "Unknown cardinal error" should "be returned" in {
    forAll("vehicleFormat") { card: String =>
      UnknownCardinal.errorMessage(card) should be(s"Cardinal unknown : $card")
    }
  }

  "Unknown instruction error" should "be returned" in {
    forAll("vehicleFormat") { instr: String =>
      UnknownInstruction.errorMessage(instr) should be(s"Instruction unknown : $instr")
    }
  }

  "Vehicle hit a wall error" should "be returned" in {
    forAll("x", "y") { (x: Int, y: Int) =>
      HitAWall.errorMessage((x,y)) should be(s"Vehicle hit a wall at ${(x,y)}: instruction ignored")
    }
  }

  "Vehicle hit a vehicle error" should "be returned" in {
    forAll("x", "y") { (x: Int, y: Int) =>
      HitAVehicle.errorMessage((x,y)) should be(s"Vehicle hit another vehicle at ${(x,y)}: instruction ignored")
    }
  }

  "Vehicles have same location error" should "be returned" in {
    forAll("x", "y") { (x: Int, y: Int) =>
      VehiclesSameLocation.errorMessage((x,y)) should be(s"Conflict between two vehicles location at ${(x,y)}: vehicles ignored")
    }
  }

  "a list of error" should "be returned" in {
    forAll("x", "y") { (x: Int, y: Int) =>
      VehiclesSameLocation.errorMessage((x,y)) should be(s"Conflict between two vehicles location at ${(x,y)}: vehicles ignored")
    }
  }
}
