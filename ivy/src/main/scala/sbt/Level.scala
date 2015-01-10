/* sbt -- Simple Build Tool
 * Copyright 2008, 2009 Mark Harrah
 */
package sbt

import com.typesafe.scalalogging.Logger

/**
 * An enumeration defining the levels available for logging.  A level includes all of the levels
 * with id larger than its own id.  For example, Warn (id=3) includes Error (id=4).
 */
sealed trait Level {
  def id: Int
  def log(logger: Logger, msg: String): Unit
}

object Level {
  type Value = Level

  case object Debug extends Level {
    val id = 1
    def log(logger: Logger, msg: String): Unit =
      logger debug msg
  }
  case object Info extends Level {
    val id = 2
    def log(logger: Logger, msg: String): Unit =
      logger info msg
  }
  case object Warn extends Level {
    val id = 3
    def log(logger: Logger, msg: String): Unit =
      logger warn msg
  }
  case object Error extends Level {
    val id = 4
    def log(logger: Logger, msg: String): Unit =
      logger error msg
  }

  /**
   * Defines the label to use for success messages.
   * Because the label for levels is defined in this module, the success label is also defined here.
   */
  val SuccessLabel = "success"

  def union(a: Level, b: Level) = if (a.id < b.id) a else b
  def unionAll(vs: Seq[Level]) = vs reduceLeft union

  /** Returns the level with the given name wrapped in Some, or None if no level exists for that name. */
  def apply(s: String) = s match {
    case "debug" => Level.Debug
    case "info"  => Level.Info
    case "warn"  => Level.Warn
    case "error" => Level.Error
  }
  /** Same as apply, defined for use in pattern matching. */
  private[sbt] def unapply(s: String) = apply(s)
}