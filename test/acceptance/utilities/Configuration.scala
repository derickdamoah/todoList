package acceptance.utilities

case class Settings(PAGE_TIMEOUT_SECS: Int)
object Configuration {
  lazy val settings: Settings = Settings(
    PAGE_TIMEOUT_SECS = 10
  )
}
