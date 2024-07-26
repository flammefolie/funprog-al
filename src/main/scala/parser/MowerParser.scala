package progfun

import config.{InvalidInstructionError, InvalidPositionFormatError}

import scala.io.Source
import scala.reflect.ClassTag
import scala.util.{Failure, Success, Try}

object MowerParser {
  def parseFile(filePath: String): Try[(Lawn, List[Mower])] = {
    Option(getClass.getResourceAsStream(filePath)) match {
      case Some(stream) =>
        Try {
          val lines =
            Source.fromInputStream(stream.nn, "UTF-8").getLines().toList
          parseLines(lines)
        }.flatten
      case None =>
        Failure[(Lawn, List[Mower])](
          new java.io.FileNotFoundException(s"Resource $filePath not found")
        )
    }
  }

  private def parseLines(lines: List[String]): Try[(Lawn, List[Mower])] = {
    lines match {
      case Nil =>
        Failure[(Lawn, List[Mower])](InvalidPositionFormatError("Empty file"))
      case lawnSizeLine :: tail =>
        val lawnTry = parseLawnSize(lawnSizeLine)
        val mowersTry = parseMowers(tail)
        for {
          lawn   <- lawnTry
          mowers <- mowersTry
        } yield (lawn, mowers)
    }
  }

  private def parseLawnSize(line: String): Try[Lawn] = {
    val parts = splitAndClean(line)
    if (parts.length != 2) {
      Failure[Lawn](InvalidPositionFormatError(line))
    } else {
      Try {
        val width = parts(0).toInt
        val height = parts(1).toInt
        Lawn(width, height)
      }.recoverWith { case _: NumberFormatException =>
        Failure[Lawn](InvalidPositionFormatError(line))
      }
    }
  }

  private def parseMowers(lines: List[String]): Try[List[Mower]] = {
    @scala.annotation.tailrec
    def parseMowersHelper(
        lines: List[String],
        acc: List[Try[Mower]]): Try[List[Mower]] = lines match {
      case Nil =>
        val combined = acc.foldLeft[Try[List[Mower]]](Success(Nil)) {
          (acc, tryMower) =>
            for {
              mowers <- acc
              mower  <- tryMower
            } yield mower :: mowers
        }
        combined
      case positionLine :: instructionLine :: tail =>
        val posTry = parsePosition(positionLine)
        val instrTry = parseInstructions(instructionLine)
        val mowerTry = for {
          pos   <- posTry
          instr <- instrTry
        } yield Mower(pos, instr)
        parseMowersHelper(tail, mowerTry :: acc)
      case _ =>
        Failure[List[progfun.Mower]](
          InvalidPositionFormatError("Invalid input format")
        )
    }

    parseMowersHelper(lines, Nil)
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

  private def parseInstructions(line: String): Try[List[Char]] = {
    val instructions = line.map {
      case 'G' => Success('G')
      case 'D' => Success('D')
      case 'A' => Success('A')
      case invalid =>
        Failure[String](InvalidInstructionError(invalid): Throwable)
    }

    val errors = instructions.collect { case Failure(e) => e }
    if (errors.nonEmpty) {
      Failure[List[Char]](
        errors.headOption.getOrElse(InvalidInstructionError(' ')): Throwable
      )
    } else {
      Success(instructions.collect { case Success(instr: Char) =>
        instr
      }.toList)
    }
  }
}
