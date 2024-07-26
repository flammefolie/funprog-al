package progfun

import upickle.default.{macroRW, ReadWriter}

sealed trait Instruction
case object TurnLeft extends Instruction
case object TurnRight extends Instruction
case object MoveForward extends Instruction

final case class Position(x: Int, y: Int, orientation: Char)

final case class Mower(
    initialPosition: Position,
    instructions: List[Char]
)

object Mower {
  implicit val rw: ReadWriter[Mower] = macroRW
}

final case class ActiveMower(position: Position, instructionsLeft: List[Char])
object ActiveMower {
  implicit val rw: ReadWriter[ActiveMower] = macroRW
}

final case class Lawn(width: Int, height: Int)
object Lawn {
  implicit val rw: ReadWriter[Lawn] = macroRW
}

final case class MowerResult(
    initialPosition: Position,
    finalPosition: Position,
    instructions: List[Char]
)
object MowerResult {
  implicit val rw: ReadWriter[MowerResult] = macroRW
}

object Position {
  implicit val rw: ReadWriter[Position] = macroRW
}

final case class Limit(x: Int, y: Int)
object Limit {
  implicit val rw: ReadWriter[Limit] = macroRW
}

final case class OutputResult(limit: Limit, mowers: List[MowerResult])
object OutputResult {
  implicit val rw: ReadWriter[OutputResult] = macroRW
}
