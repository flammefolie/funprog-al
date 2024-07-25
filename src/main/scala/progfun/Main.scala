package fr.esgi.al.funprog

import config.ConfigLoader

import scala.util.{Failure, Success}

@main
def Main(): Unit = {
  ConfigLoader.load() match {
    case Success(config) =>
      println(s"App Name: ${config.name}")
      println(s"Input File: ${config.inputFile}")
      println(s"Output JSON File: ${config.outputJsonFile}")
      println(s"Output CSV File: ${config.outputCsvFile}")
      println(s"Output YAML File: ${config.outputYamlFile}")

    case Failure(exception) =>
      println(s"Failed to load configuration: ${exception.getMessage}")
      System.exit(1)
  }
}
