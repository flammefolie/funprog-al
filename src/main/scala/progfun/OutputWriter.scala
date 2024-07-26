package progfun

import better.files.File
import upickle.default.{write, ReadWriter}
import scala.annotation.tailrec

object OutputWriter {
  implicit val positionRW: ReadWriter[Position] = Position.rw
  implicit val mowerResultRW: ReadWriter[MowerResult] = MowerResult.rw
  implicit val initialMowerRW: ReadWriter[Mower] = Mower.rw
  implicit val activeMowerRW: ReadWriter[ActiveMower] = ActiveMower.rw
  implicit val outputResultRW: ReadWriter[OutputResult] = OutputResult.rw
  implicit val limitRW: ReadWriter[Limit] = Limit.rw

  def toJson(resultList: OutputResult): String = {
    write(resultList)
  }

  def writeToFile(jsonObject: String, path: String): Unit = {
    better.files.File(path).write(jsonObject)
    println("File written successfully")
  }

  def toCsv(resultList: OutputResult): String = {
    val header =
      "InitialX,InitialY,InitialDirection,FinalX,FinalY,FinalDirection,Instructions"

    @tailrec
    def rowsToCsv(mowers: List[MowerResult], acc: List[String]): List[String] =
      mowers match {
        case Nil => acc
        case mower :: tail =>
          val initial = mower.initialPosition
          val finalPos = mower.finalPosition
          val instructions = mower.instructions.mkString("")
          val row = List(
            initial.x.toString,
            initial.y.toString,
            initial.orientation,
            finalPos.x.toString,
            finalPos.y.toString,
            finalPos.orientation,
            instructions
          ).mkString(",")
          rowsToCsv(tail, row :: acc)
      }

    val rows = rowsToCsv(resultList.mowers, Nil).reverse
    (header :: rows).mkString("\n")
  }

  def writeCsvToFile(csvContent: String, path: String): Unit = {
    better.files.File(path).write(csvContent)
    println("CSV file written successfully")
  }
}
