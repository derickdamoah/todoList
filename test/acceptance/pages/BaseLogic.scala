package acceptance.pages
import org.openqa.selenium._
import acceptance.utilities._
import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}
import java.time.Duration
object BaseLogic {

  implicit lazy val webDriver: WebDriver = Driver.instance

  def navigateToStartPage(): Unit = {
    new WebDriverWait(webDriver, Duration.ofSeconds(Configuration.settings.PAGE_TIMEOUT_SECS))
    webDriver.get("http://localhost:9000/")
  }

  def getElement(by: By): WebElement = {
    new WebDriverWait(webDriver, Duration.ofSeconds(Configuration.settings.PAGE_TIMEOUT_SECS))
      .until(ExpectedConditions.elementToBeClickable(by))
  }

  def clickOnElement(by: By): Unit = {
    new WebDriverWait(webDriver, Duration.ofSeconds(Configuration.settings.PAGE_TIMEOUT_SECS))
      .until(ExpectedConditions.elementToBeClickable(by))
      .click()
  }


}
