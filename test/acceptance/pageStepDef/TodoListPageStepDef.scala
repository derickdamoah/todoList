package acceptance.pageStepDef

import acceptance.pages.BaseLogic._
import io.cucumber.scala.{EN, ScalaDsl}
import org.openqa.selenium.By

class TodoListPageStepDef extends ScalaDsl with EN {
  Given("""I navigate to the home page""") {
    navigateToStartPage()
  }

  Then("""I will be on a page where the title is "(.*)"$"""){ (pageTitle: String) =>
    val actualTitle = webDriver.getTitle
    assert(actualTitle == pageTitle, s"expected: $pageTitle, actual: $actualTitle")
  }

  And("""I will see a button with the text "(.*)"$""") { (buttonTitle: String) =>
    val actualButtonTitle = getElement(By.cssSelector("""#addItemButton>a""")).getText
    assert(actualButtonTitle == buttonTitle, s"expected: $buttonTitle, actual: $actualButtonTitle")
  }

  Then("""I will click on the "Add Item" button with id "addItemButton"""") {
    clickOnElement(By.cssSelector("""#addItemButton"""))
  }

  Then("""I will fill the user form and submit""") {
    webDriver.findElement(By.cssSelector("""#title""")).clear()
    webDriver.findElement(By.cssSelector("""#title""")).sendKeys(s"test title 1")
    webDriver.findElement(By.cssSelector("""#description""")).clear()
    webDriver.findElement(By.cssSelector("""#description""")).sendKeys(s"test description 1")
    webDriver.findElement(By.cssSelector("""#add-item-button""")).click()
  }

  And("""I will see the new todo item with title "(.*)"$""") { (buttonTitle: String) =>
    webDriver.navigate().refresh()
    val newItem = getElement(By.cssSelector("""details>summary""")).getText
    assert(newItem == buttonTitle, s"expected: $buttonTitle, actual: $newItem")
  }

  And("""I will see the new todo item with description "(.*)"$""") { (buttonTitle: String) =>
    webDriver.findElement(By.cssSelector("""details>summary""")).click()
    val newItem = getElement(By.cssSelector("""details>p""")).getText
    assert(newItem == buttonTitle, s"expected: $buttonTitle, actual: $newItem")
  }

  Then("""I will click on the "Edit" button with id "editItemButton"""") {
    clickOnElement(By.cssSelector("""#editItemButton"""))
  }

  Then("""I will change the values in the user form and submit""") {
    webDriver.findElement(By.cssSelector("""#title""")).clear()
    webDriver.findElement(By.cssSelector("""#title""")).sendKeys(s"updated title 1")
    webDriver.findElement(By.cssSelector("""#description""")).clear()
    webDriver.findElement(By.cssSelector("""#description""")).sendKeys(s"updated description 1")
    webDriver.findElement(By.cssSelector("""#edit-item-button""")).click()
  }

  And("""I delete all todo items"""){
    webDriver.findElement(By.cssSelector(""".deleteButton>form>input[type='submit']""")).click()
    Thread.sleep(2000)
    webDriver.findElement(By.cssSelector(""".deleteButton>form>input[type='submit']""")).click()
  }

  And("""I should see no items in the homepage""") {
    val value = webDriver.findElement(By.cssSelector(""".addNewItem""")).getText
    assert(value == "", s"expected: , actual: $value")
  }

}