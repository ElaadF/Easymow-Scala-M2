package fr.upem.easymow

import fr.upem.easymow.error.{AddOutOfBound, ReadFileFailed, VehiclesSameLocation}
import fr.upem.easymow.file.IO
import fr.upem.easymow.playground.Field
import fr.upem.easymow.vehicle.Lawnmower
import fr.upem.easymow.logger.Log._
import scala.util.{Failure, Success}
import cats.syntax.show._

/** Main application */
object Easymow extends App {

  /** Log invalids vehicle in logs/record.log */
  private def logInvalidsVehicles(vehiclesOutOfBound: List[Lawnmower], vehiclesSameLocations: List[Lawnmower]): Unit = {
    (vehiclesOutOfBound, vehiclesSameLocations) match {
      case (out, _) if out.nonEmpty =>
        out.map(lm => AddOutOfBound.errorMessage((lm.pos.x, lm.pos.y))).loggingWarn()
      case (_, sames) if sames.nonEmpty =>
        sames.map(lm => VehiclesSameLocation.errorMessage((lm.pos.x, lm.pos.y))).loggingWarn()
      case (_,_) =>
    }
  }

  IO.read(scala.io.StdIn.readLine("Select a file: ")) match {
    case Success(content) =>
      val resultLines = IO.analyzeFormat(content)
      resultLines match {
        case Left(e) => e.loggingError()
        case Right(field) =>
          field.show.loggingInfo()
          val wrongVehicle: List[Lawnmower] = field.getInvalidVehicles

          val vehiclesOutOfBound: List[Lawnmower] = wrongVehicle.filter(l => !field.isInField(l.pos.x, l.pos.y))
          val vehiclesSameLocations: List[Lawnmower] = wrongVehicle.filter(l => !field.isFreeZone(l.pos.x, l.pos.y))

          logInvalidsVehicles(vehiclesOutOfBound, vehiclesSameLocations)

          val cleanFieldInitial: Field = field.copy(vehicles = field.vehicles diff wrongVehicle)
          val fieldComputeAndField: (List[Either[String, Lawnmower]], Field) = cleanFieldInitial.computeField
          val fieldComputeRes: List[Either[String, Lawnmower]] = fieldComputeAndField._1
          val finalField: Field = fieldComputeAndField._2

          fieldComputeRes.foreach {
            case Left(impossibleInstr) => impossibleInstr.loggingWarn()
            case Right(vehicleFinalState) => vehicleFinalState.loggingInfo()
          }

          val displayVehicle = cleanFieldInitial.vehicles zip finalField.vehicles

          displayVehicle.foreach { case (v1, v2) => s"${v1.pos.show} => ${v2.pos.show}".loggingResult() }
      }
    case Failure(ex) => ReadFileFailed.errorMessage(ex.getMessage).loggingError()
  }
}


