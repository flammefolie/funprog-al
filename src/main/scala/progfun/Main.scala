package fr.esgi.al.funprog

import config.{ApplicationConfig, ConfigLoader}

import scala.util.{Failure, Success, Try}

@main
def Main(): Unit = {
  val configResult = loadConfig()
  configResult match {
    case Success(config) =>
      val inputFilePath = config.inputFile

    case Failure(exception) =>
      println(s"Failed to load configuration: ${exception.getMessage}")
      System.exit(1)
  }
}

def loadConfig(): Try[ApplicationConfig] = {
  ConfigLoader.load()
}