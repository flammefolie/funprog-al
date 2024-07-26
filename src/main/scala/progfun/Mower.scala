package progfun

sealed trait Instruction
case object TurnLeft extends Instruction
case object TurnRight extends Instruction
case object MoveForward extends Instruction

final case class Position(x: Int, y: Int, orientation: Char)

final case class Lawn(width: Int, height: Int)

final case class Mower(
    initialPosition: Position,
    instructions: List[Instruction]
)
