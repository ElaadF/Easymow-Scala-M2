package fr.upem.easymow

import fr.upem.easymow.error.{AddOutOfBound, VehiclesSameLocation}
import fr.upem.easymow.file.IO
import fr.upem.easymow.playground.Field
import fr.upem.easymow.vehicle.Lawnmower
import org.apache.logging.log4j
import org.apache.logging.log4j.LogManager

import scala.util.{Failure, Success}
object uti {
  def computeField(field: Field): (List[Either[String, Lawnmower]], Field) = {
    def computeFieldAcc(f: Field, l: List[Lawnmower], res: List[Either[String, Lawnmower]]): (List[Either[String, Lawnmower]], Field) = {
      l match {
        case x :: xs =>
          val fieldCopy = f.copy(vehicles = f.vehicles diff List(x))
          val resAndField = x.executeInstructionRec(fieldCopy)
          computeFieldAcc(resAndField._2, xs, res ::: resAndField._1)
        case Nil => (res, f)
      }
    }
    computeFieldAcc(field, field.vehicles, List())
  }
}
object Easymow extends App {
  val logger: log4j.Logger = LogManager.getLogger(getClass.getName)

  IO.read("src/main/resources/input") match {
    case Success(content) =>
      val resultFile = IO.analyzeFormat(content)
      resultFile match {
        case Left(e) => e.foreach(println);println("ERREURS ANALYSE")
        case Right(field) =>

          val wrongVehicle: List[Lawnmower] = field.isValidPlayground
          if(wrongVehicle.nonEmpty) {
            wrongVehicle.foreach {
              case l if !field.isInField(l.pos.x, l.pos.y) => logger.warn(AddOutOfBound.errorMessage((l.pos.x, l.pos.y)))
              case l if !field.isFreeZone(l.pos.x, l.pos.y) => logger.warn(VehiclesSameLocation.errorMessage((l.pos.x, l.pos.y)))
            }
          }
          val cleanField: Field = field.copy(vehicles = field.vehicles diff wrongVehicle)
          val fieldComputeAndField = uti.computeField(cleanField)
          val fieldComputeRes: List[Either[String, Lawnmower]] = fieldComputeAndField._1
          //println(fieldComputeAndField._2)
          fieldComputeRes.foreach {
            case Left(impossibleInstr) => logger.warn(impossibleInstr)
            case Right(vehicleFinalState) => logger.info(vehicleFinalState)
          }
      }
    case Failure(ex) => logger.error(s"Erreur lecture fichier : $ex")
  }
}

