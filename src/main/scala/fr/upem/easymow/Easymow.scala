package fr.upem.easymow

import fr.upem.easymow.error.{AddOccupiedLocation, AddOutOfBound, VehiclesSameLocation}

import scala.util.Success
import scala.util.Failure
import fr.upem.easymow.file.IO
import fr.upem.easymow.playground.Field
import fr.upem.easymow.vehicle.Lawnmower
import org.apache.logging.log4j
import org.apache.logging.log4j.LogManager


object Easymow extends App {
  val logger: log4j.Logger = LogManager.getLogger(getClass.getName)

  IO.read("src/main/resources/input") match {
    case Success(content) =>
      val resultFile = IO.analyzeFormat(content)
      resultFile match {
        case Left(e) => e.foreach(println);println("ERREURS ANALYSE")
        case Right(field) =>

          val wrongVehicle: List[Lawnmower] = field.isValidPlayground
          println(wrongVehicle)
          if(wrongVehicle.nonEmpty) {
            wrongVehicle.foreach {
              case l if !field.isInField(l.pos.x, l.pos.y) => logger.warn(AddOutOfBound.errorMessage((l.pos.x, l.pos.y)))
              case l if !field.isFreeZone(l.pos.x, l.pos.y) => logger.warn(VehiclesSameLocation.errorMessage((l.pos.x, l.pos.y)))
            }
          }
          val cleanField: Field = field.copy(vehicles = field.vehicles diff wrongVehicle)
          println(cleanField)
          println(field)
          val fieldCompute: List[List[Either[String,Lawnmower]]] = cleanField.vehicles.map(v => {
            val fieldCopy = cleanField.copy(vehicles = cleanField.vehicles diff List(v))
            v.executeInstructionRec(fieldCopy)
          })

          fieldCompute.flatten.foreach {
            case Left(impossibleInstr) => logger.warn(impossibleInstr)
            case Right(vehicleFinalState) => logger.info(vehicleFinalState)
          }
      }
    case Failure(ex) => println(s"Erreur lecture fichier : $ex")
  }
}

