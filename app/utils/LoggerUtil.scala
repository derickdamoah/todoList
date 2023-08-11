package utils

import org.slf4j.{Logger, LoggerFactory}
import play.api.LoggerLike

trait LoggerUtil extends LoggerLike {
  override val logger: Logger = LoggerFactory.getLogger("application")
}
