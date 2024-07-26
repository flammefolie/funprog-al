package progfun

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

class MowerManager(protected val lawn: Lawn) {

  def getMowerResults(Mowers: List[Mower]): List[MowerResult] = {
    Mowers.map(Mower => getMowerResult(Mower))
  }

  private def getMowerResult(mower: Mower): MowerResult = {
    println(s"getMowerResult - Initial Position: ${mower.initialPosition}")
    MowerResult(
      mower.initialPosition,
      getFinalPosition(
        ActiveMower(mower.initialPosition, mower.instructions)
      ).position,
      mower.instructions
    )
  }

  private def getFinalPosition(mower: ActiveMower): ActiveMower = {
    @tailrec
    def loop(currentMower: ActiveMower): ActiveMower = {
      currentMower.instructionsLeft match {
        case Nil => currentMower
        case instruction :: instructionsLeft =>
          val updatedMower = handleInstruction(
            currentMower.copy(instructionsLeft = instructionsLeft),
            instruction
          ) match {
            case Success(newMower) => newMower
            case Failure(_)        => currentMower
          }
          loop(updatedMower)
      }
    }

    loop(mower)
  }

  private def handleInstruction(
      mower: ActiveMower,
      instruction: Char): Try[ActiveMower] = {
    instruction match
      case 'G' => Success(turnLeft(mower))
      case 'D' => Success(turnRight(mower))
      case 'A' => Success(advance(mower))
      case _ =>
        Failure[ActiveMower](
          new IllegalArgumentException(s"Unknown instruction: $instruction")
        )
  }

  private def turnLeft(mower: ActiveMower): ActiveMower = {
    val newPosition = mower.position.orientation match
      case 'N' => mower.position.copy(orientation = 'W')
      case 'W' => mower.position.copy(orientation = 'S')
      case 'S' => mower.position.copy(orientation = 'E')
      case 'E' => mower.position.copy(orientation = 'N')
      case _   => mower.position
    println(s"turnLeft - New Position: $newPosition")
    mower.copy(position = newPosition)
  }

  private def turnRight(mower: ActiveMower): ActiveMower = {
    val newPosition = mower.position.orientation match
      case 'N' => mower.position.copy(orientation = 'E')
      case 'E' => mower.position.copy(orientation = 'S')
      case 'S' => mower.position.copy(orientation = 'W')
      case 'W' => mower.position.copy(orientation = 'N')
      case _   => mower.position
    println(s"turnRight - New Position: $newPosition")
    mower.copy(position = newPosition)
  }

  private def advance(mower: ActiveMower): ActiveMower = {
    val newPosition = mower.position.orientation match
      case 'N' if mower.position.y < lawn.height =>
        mower.position.copy(y = mower.position.y + 1)
      case 'E' if mower.position.x < lawn.width =>
        mower.position.copy(x = mower.position.x + 1)
      case 'S' if mower.position.y > 0 =>
        mower.position.copy(y = mower.position.y - 1)
      case 'W' if mower.position.x > 0 =>
        mower.position.copy(x = mower.position.x - 1)
      case _ =>
        println(s"Bump - Mower stays at Position: ${mower.position}")
        mower.position
    println(s"advance - New Position: $newPosition")
    mower.copy(position = newPosition)
  }
}
