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
      val mowersResult = parseFile(config.inputFile)
      mowersResult match {
        case Success((lawn, mowers)) =>
          println(s"Lawn: $lawn")
          println(s"Mowers: $mowers")
          val mowerManager = MowerManager(lawn)
          val results = mowerManager.getMowerResults(mowers)
          results.foreach(println)
          val outputResult =
            OutputResult(Limit(lawn.width, lawn.height), results)

          val outputWriter = OutputWriter
          val jsonResult = outputWriter.toJson(outputResult)
          val csvResult = outputWriter.toCsv(outputResult)
          println("Récapitulatif des tondeuses :")
          println(jsonResult)
          println("Récapitulatif des tondeuses en CSV :")
          println(csvResult)
          outputWriter.writeToFile(jsonResult, config.outputJsonFile)
          outputWriter.writeCsvToFile(csvResult, config.outputCsvFile)
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
