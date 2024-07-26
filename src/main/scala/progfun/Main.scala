package fr.esgi.al.funprog

import config.{ApplicationConfig, ConfigLoader}
import progfun.*
import progfun.MowerParser.parseFile

import scala.util.{Failure, Success, Try}

@main
def Main(): Unit = {
  val configResult = loadConfig()
  configResult match {
    case Success(config) =>
      val mowers = parseFile(config.inputFile)
      println(mowers)
    case Failure(exception) =>
      println(s"Failed to load configuration: ${exception.getMessage}")
      System.exit(1)
  }
}

def loadConfig(): Try[ApplicationConfig] = {
  ConfigLoader.load()
}
