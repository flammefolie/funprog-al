package config

import scala.io.{BufferedSource, Codec, Source}
import scala.util.{Failure, Success, Try}

final case class ApplicationConfig(
    name: String,
    inputFile: String,
    outputJsonFile: String,
    outputCsvFile: String,
    outputYamlFile: String
)

object ConfigLoader {
  def load(): Try[ApplicationConfig] = {
    val classLoader = getClass.getClassLoader.nn
    val resourceStream =
      Option(classLoader.getResourceAsStream("application.conf").nn)
        .toRight(new RuntimeException("Configuration file not found"))
        .toTry
        .flatMap { stream =>
          val source: BufferedSource =
            Source.fromInputStream(stream)(Codec.UTF8)
          Try(source.mkString)
            .map { content =>
              source.close()
              content
            }
            .recoverWith { case error: Throwable =>
              source.close()
              Failure[String](error)
            }
        }

    resourceStream.flatMap(parseConfig)
  }

  private def parseConfig(configContent: String): Try[ApplicationConfig] = {
    val configLines = configContent.split("\n").nn
    val configMap = configLines
      .map(_.nn.trim.nn)
      .filter(line => line.nonEmpty && !line.startsWith("#"))
      .flatMap { line =>
        line.split("=", 2).nn match {
          case Array(key, value) =>
            Some(key.nn.trim.nn -> value.nn.trim.nn.replace("\"", ""))
          case _: Array[String | Null] => None
        }
      }
      .toMap

    def getConfigValue(key: String): Try[String] = {
      configMap.get(key).map(_.nn) match {
        case Some(value) => Success(value)
        case None        => Failure[String](MissingConfigError(key))
      }
    }

    for {
      name           <- getConfigValue("name")
      inputFile      <- getConfigValue("input-file")
      outputJsonFile <- getConfigValue("output-json-file")
      outputCsvFile  <- getConfigValue("output-csv-file")
      outputYamlFile <- getConfigValue("output-yaml-file")
    } yield ApplicationConfig(
      name,
      inputFile,
      outputJsonFile,
      outputCsvFile,
      outputYamlFile
    )
  }
}
