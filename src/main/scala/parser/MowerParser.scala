package progfun

import config.{InvalidInstructionError, InvalidPositionFormatError}

import scala.io.Source
import scala.reflect.ClassTag
import scala.util.{Failure, Success, Try}

object MowerParser {
  def parseFile(filePath: String): Try[List[Mower]] = {
    Option(getClass.getResourceAsStream(filePath)) match {
      case Some(stream) =>
        Try {
          val lines =
            Source.fromInputStream(stream.nn, "UTF-8").getLines().toList
          parseLines(lines)
        }.flatten
      case None =>
        Failure[List[Mower]](
          new java.io.FileNotFoundException(s"Resource $filePath not found")
        )
    }
  }

  private def parseLines(lines: List[String]): Try[List[Mower]] = {
    lines match {
      case Nil => Success(Nil)
      case lawnSize :: tail =>
        val (mowersTry, remainingLines) = parseMowers(tail)
        mowersTry
    }
  }

  private def parseMowers(
      lines: List[String]): (Try[List[Mower]], List[String]) = {
    lines match {
      case Nil => (Success(Nil), Nil)
      case positionLine :: instructionLine :: tail =>
        val result = for {
          pos   <- parsePosition(positionLine)
          instr <- parseInstructions(instructionLine)
          mower = Mower(pos, instr)
          (remainingMowersTry, remainingLines) = parseMowers(tail)
          remainingMowers <- remainingMowersTry
        } yield (mower :: remainingMowers, remainingLines)

        result match {
          case Success((mowers, remainingLines)) =>
            (Success(mowers), remainingLines)
          case Failure(e) => (Failure[List[progfun.Mower]](e), tail)
        }
      case _ =>
        (
          Failure[List[progfun.Mower]](
            InvalidPositionFormatError("Invalid input format")
          ),
          Nil
        )
    }
  }

  private def parsePosition(line: String): Try[Position] = {
    val parts = splitAndClean(line)
    if (parts.length != 3) {
      Failure[Position](InvalidPositionFormatError(line))
    } else {
      Try {
        val x = parts(0).toInt
        val y = parts(1).toInt
        val orientation = parts(2).head
        Position(x, y, orientation)
      }.recoverWith { case _: NumberFormatException =>
        Failure[Position](InvalidPositionFormatError(line))
      }
    }
  }

  private def splitAndClean(line: String)(implicit
      ct: ClassTag[String]): Array[String] = {
    Option(line)
      .map(_.split(" "))
      .getOrElse(Array.empty[String])
      .nn
      .collect { case s: String =>
        s
      }
  }

  private def parseInstructions(line: String): Try[List[Instruction]] = {
    val instructions = line.map {
      case 'G' => Success(TurnLeft)
      case 'D' => Success(TurnRight)
      case 'A' => Success(MoveForward)
      case invalid =>
        Failure[String](InvalidInstructionError(invalid): Throwable)
    }

    val errors = instructions.collect { case Failure(e) => e }
    if (errors.nonEmpty) {
      Failure[List[Instruction]](
        errors.headOption.getOrElse(InvalidInstructionError(' ')): Throwable
      )
    } else {
      Success(instructions.collect { case Success(instr: Instruction) =>
        instr
      }.toList)
    }
  }
}
