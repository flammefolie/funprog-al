package config

sealed trait ConfigError extends Exception
final case class MissingConfigError(key: String) extends ConfigError {
  override def getMessage: String = s"Missing configuration for key: $key"
}
final case class InvalidConfigFormatError(line: String) extends ConfigError {
  override def getMessage: String = s"Invalid configuration format: $line"
}
final case class InvalidPositionFormatError(line: String) extends ConfigError {
  override def getMessage: String = s"Invalid position format: $line"
}
final case class InvalidInstructionError(instruction: Char)
    extends ConfigError {
  override def getMessage: String = s"Invalid instruction: $instruction"
}
case object InvalidLawnSizeError extends Exception("Invalid lawn size format")
