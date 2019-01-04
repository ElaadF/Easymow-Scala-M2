package fr.upem.easymow.logger

import cats.Show
import cats.syntax.show._
import fr.upem.easymow.playground.{Field, Position}
import fr.upem.easymow.vehicle._
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.scala.Logging

/** Log on String, List[String] and Lawnmower
  * */
@scala.annotation.implicitNotFound("No way to log ${A}." +
  "An implicit Log[${A}] must be in scope")
trait Log[A] {
  def loggingError(a: A): Unit
  def loggingWarn(a: A): Unit
  def loggingInfo(a: A): Unit
  def loggingResult(a: A): Unit
}

/** Allow to logs on the console and in logs/record.log*/
object Log extends Logging {

  implicit val positionShow: Show[Position] =
    Show.show(p => s"""(${p.x}, ${p.y}, ${p.orientation})""")

  implicit val lawnmowerShow: Show[Lawnmower] =
    Show.show(lm => s"Position : ${lm.pos.show} Instructions : ${lm.instruction}")

  implicit val fieldShow: Show[Field] =
    Show.show(f => s"Field : length : ${f.length + 1}, width : ${f.width + 1}")

  /** Custom level log in console in stdout */
  val RESULT: Level = Level.forName("RESULT", 450)

  def apply[A](implicit e: Log[A]): Log[A] = e

  /** Log ERROR level in logs/record.log and stderr
    *
    *  @param a the implicit element to log
    */
  def loggingError[A: Log](a: A): Unit = Log[A].loggingError(a)

  /** Log WARN level in logs/record.log
    *
    *  @param a the implicit element to log
    */
  def loggingWarn[A: Log](a: A): Unit = Log[A].loggingWarn(a)

  /** Log INFO level in logs/record.log
    *
    *  @param a the implicit element to log
    */
  def loggingInfo[A: Log](a: A): Unit = Log[A].loggingInfo(a)

  /** Log RESULT custom level in logs/record.log and stdout
    *
    *  @param a the implicit element to log
    */
  def loggingResult[A: Log](a: A): Unit = Log[A].loggingResult(a)

  implicit class LoggingMessages[A: Log](a: A) {

    def loggingError(): Unit = Log[A].loggingError(a)
    def loggingWarn(): Unit = Log[A].loggingWarn(a)
    def loggingInfo(): Unit = Log[A].loggingInfo(a)
    def loggingResult(): Unit = Log[A].loggingResult(a)

  }

  /** Logging on List[String]*/
    implicit val LogListString: Log[List[String]] =
      new Log[List[String]] {

        def loggingError(messages: List[String]): Unit =
          messages.foreach(errmsg => logger.error(errmsg))

        def loggingWarn(messages: List[String]): Unit =
          messages.foreach(warnmsg => logger.warn(warnmsg))

        def loggingInfo(messages: List[String]): Unit =
          messages.foreach(infomsg => logger.info(infomsg))

        def loggingResult(messages: List[String]): Unit =
          messages.foreach(resutlmsg => logger(RESULT, resutlmsg))
      }

  /** Logging on String */
    implicit val LogString: Log[String] =
      new Log[String] {

        def loggingError(message: String): Unit = logger.error(message)

        def loggingWarn(message: String): Unit = logger.warn(message)

        def loggingInfo(message: String): Unit = logger.info(message)

        def loggingResult(message: String): Unit = logger(RESULT, message)
      }

  /** Logging on Lawnmower */
    implicit val LogLanwmower: Log[Lawnmower] =
      new Log[Lawnmower] {

        def loggingError(lm: Lawnmower): Unit = logger.error(lm.show)

        def loggingWarn(lm: Lawnmower): Unit = logger.warn(lm.show)

        def loggingInfo(lm: Lawnmower): Unit = logger.info(lm.show)

        def loggingResult(lm: Lawnmower): Unit = logger(RESULT, lm.show)

      }
}