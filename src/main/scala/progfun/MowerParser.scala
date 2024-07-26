package progfun

import scala.io.Source

object MowerParser {
  def parseFile(filePath: String): List[Mower] = {
    val stream = getClass.getResourceAsStream("/input.txt")
    val lines = io.Source.fromInputStream(stream.nn, "UTF-8").getLines().toList
    parseLines(lines)
  }

  def parseLines(lines: List[String]): List[Mower] = {
    lines match {
      case Nil => Nil
      case lawnSize :: tail =>
        val (mowers, remainingLines) = parseMowers(tail)
        mowers
    }
  }

  def parseMowers(lines: List[String]): (List[Mower], List[String]) = {
    lines match {
      case Nil => (Nil, Nil)
      case positionLine :: instructionLine :: tail =>
        val position = parsePosition(positionLine)
        val instructions = parseInstructions(instructionLine)
        val mower = Mower(position, instructions)
        val (remainingMowers, remainingLines) = parseMowers(tail)
        (mower :: remainingMowers, remainingLines)
      case _ => (Nil, Nil)
    }
  }

  def parsePosition(line: String): Position = {
    val Array(x, y, orientation) = line.split(" "): @unchecked
    Position(
      x match {
        case s: String => s.toInt
        case _         => 'c'
      },
      y match {
        case s: String => s.toInt
        case _         => 'c'
      },
      orientation match {
        case s: String => s.head
        case _         => 'c'
      }
    )
  }

  def parseInstructions(line: String): List[Instruction] = {
    line.map {
      case 'G' => TurnLeft
      case 'D' => TurnRight
      case 'A' => MoveForward
      case _   => MoveForward

    }.toList
  }
}
