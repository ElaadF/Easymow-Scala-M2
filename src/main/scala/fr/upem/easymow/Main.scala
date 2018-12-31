package fr.upem.easymow

import scala.util.Success
import scala.util.Failure
import fr.upem.easymow.file.IO
import fr.upem.easymow.vehicle.Lawnmower
import cats.implicits._

object Easymow extends App {


    //val logger: log4j.Logger = LogManager.getLogger(getClass().getName())

      //logger.info("toto")
      IO.read("src/main/resources/input") match {
        case Success(content) =>
          IO.analyzeFormat(content) match {
            case Left(e) => e.foreach(println);println("ERREURS ANALYSE")
            case Right(field) => {
              val fieldCompute: List[Either[String,Lawnmower]] = field.vehicles.map(v => {
                val fieldCopy = field.copy(vehicles = field.vehicles diff List(v))
                v.executeInstruction(fieldCopy)
              })
              fieldCompute.foreach(println)
              val (lefts, rights) = fieldCompute.separate
              (lefts, rights) match {
                case (errors,_) if errors.nonEmpty => lefts.foreach(println)
                case (_,results)  => results.foreach(println)
              }
            }

          }
        case Failure(ex) => println(s"Erreur lecture fichier : $ex")
      }
}
