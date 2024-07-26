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
      mowers match {
        case Success((lawn, mowers)) =>
          println(lawn)
          println(mowers)
        case Failure(exception) =>
          println(s"Failed to parse mowers: ${exception.getMessage}")
      }
    case Failure(exception) =>
      println(s"Failed to load configuration: ${exception.getMessage}")
      System.exit(1)
  }
}

def loadConfig(): Try[ApplicationConfig] = {
  ConfigLoader.load()
}
