package acceptance.utilities

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}

import scala.util.Try

object Driver {

  System.setProperty("webdriver.chrome.driver", "/opt/homebrew/bin/chromedriver")

  val chromeOptions = new ChromeOptions()
  chromeOptions.addArguments("--remote-allow-origins=*")
  implicit lazy val instance: WebDriver = new ChromeDriver(chromeOptions)

  sys addShutdownHook {

    Try(instance.quit())

  }

}