package fr.upem.easymow


import cats.Show           //the type class
import cats.syntax.show._  //the interface syntax
import fr.upem.easymow.error.{AddOutOfBound, VehiclesSameLocation}
import fr.upem.easymow.file.IO
import fr.upem.easymow.playground.{Field, Position}
import fr.upem.easymow.vehicle.Lawnmower
import org.apache.logging.log4j
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Level
import scala.util.{Failure, Success}

object Easymow extends App {
  val logger: log4j.Logger = LogManager.getLogger(getClass.getName)
  val RESULT = Level.forName("RESULT", 450)


  implicit val positionShow: Show[Position] =
    Show.show(p => s"""(${p.x}, ${p.y}, ${p.orientation})""")

  implicit val lawnmowerShow: Show[Lawnmower] =
    Show.show(lm => s"Position : ${lm.pos.show} Instructions : ${lm.instruction}")




  IO.read(scala.io.StdIn.readLine("Select a file: ")) match {
    case Success(content) =>
      val resultFile = IO.analyzeFormat(content)
      resultFile match {
        case Left(e) => e.foreach(err => logger.error(err))
        case Right(field) =>

          val wrongVehicle: List[Lawnmower] = field.isValidPlayground
          if(wrongVehicle.nonEmpty) {
            wrongVehicle.foreach {
              case l if !field.isInField(l.pos.x, l.pos.y) => logger.warn(AddOutOfBound.errorMessage((l.pos.x, l.pos.y)))
              case l if !field.isFreeZone(l.pos.x, l.pos.y) => logger.warn(VehiclesSameLocation.errorMessage((l.pos.x, l.pos.y)))
            }
          }
          val cleanField: Field = field.copy(vehicles = field.vehicles diff wrongVehicle)
          val fieldComputeAndField = Field.computeField(cleanField)
          val fieldComputeRes: List[Either[String, Lawnmower]] = fieldComputeAndField._1
          val finalField = fieldComputeAndField._2

          fieldComputeRes.foreach {
            case Left(impossibleInstr) => logger.warn(impossibleInstr)
            case Right(vehicleFinalState) => logger.info(vehicleFinalState.show)
          }

          val displayVehicle = cleanField.vehicles zip finalField.vehicles
          displayVehicle.foreach{case(v1, v2) => logger.log(RESULT, s"${v1.pos.show} => ${v2.pos.show}") }
      }
    case Failure(ex) => logger.error(s"Read File failed : $ex")
  }
}

