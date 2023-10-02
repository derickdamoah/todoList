Feature: Feature to test the Todo List application

  Scenario: The user creates a new todo list item with no errors
    Given I navigate to the home page
    Then I will be on a page where the title is "Todo List"
    And I will see a button with the text "Add Item"
    Then I will click on the "Add Item" button with id "addItemButton"
    And I will be on a page where the title is "Add Item Page"
    Then I will fill the user form and submit
    And I will be on a page where the title is "Todo List"
    And I will see the new todo item with title "test title 1"
    And I will see the new todo item with description "test description 1"

  Scenario: The user edits a todo list item
    Given I navigate to the home page
    Then I will be on a page where the title is "Todo List"
    And I will see a button with the text "Add Item"
    Then I will click on the "Add Item" button with id "addItemButton"
    And I will be on a page where the title is "Add Item Page"
    Then I will fill the user form and submit
    And I will be on a page where the title is "Todo List"
    And I will see the new todo item with title "test title 1"
    And I will see the new todo item with description "test description 1"
    Then I will click on the "Edit" button with id "editItemButton"
    And I will be on a page where the title is "Edit Page"
    Then I will change the values in the user form and submit
    And I will be on a page where the title is "Todo List"
    And I will see the new todo item with title "updated title 1"
    And I will see the new todo item with description "updated description 1"

  Scenario: The user deletes all todo items
    Given I navigate to the home page
    And I delete all todo items
    Then I will be on a page where the title is "Todo List"
    And I should see no items in the homepage
