Feature: Misc WebDriver manual tests

  Scenario Outline: Element selectors (non-links)
    Given the page at url 'http://localhost:4200/forms' is opened
    When I type 'some text' into the field '<selector>'
    Then the element '<selector>' should have the value 'some text'
    Examples:
      | selector                                         |
      | id=textInputId                                   |
      | name=textInputName                               |
      | css=.textInputType                               |
      | xpath=//input[@id='textInputId']                 |
      | js=return document.getElementById('textInputId') |


  Scenario Outline: Element selectors (links)
    Given the page at url 'http://localhost:4200/forms' is opened
    Then the text of element '<selector>' should be 'An example website'
    Examples:
      | selector                     |
      | linkText=An example website  |
      | linkTextContains=example web |


  Scenario Outline: Text match expressions
    Given the page at url 'http://localhost:4200/forms' is opened
    Then the text of element 'id=linkId' should be '<textMatchExpression>'
    Examples:
      | textMatchExpression             |
      | An example website              |
      | exact=An example website        |
      | contains=example                |
      | containsCaseSensitive=example   |
      | containsCaseInsensitive=eXaMPLe |
      | regex=.*ex[a-z]mple.*           |
      | regex=(?i).*eX[a-z]MPLe.*       |

  Scenario: assertElementTextShouldBe
    Given the page at url 'http://localhost:4200/forms' is opened
    Then the text of element 'id=linkId' should be 'exact=An example website'

  Scenario: assertElementTextShouldNotBe
    Given the page at url 'http://localhost:4200/forms' is opened
    Then the text of element 'id=linkId' should not be 'exact=An example websiteX'

  Scenario: assertCurrentPageTitleShouldBe
    Given the page at url 'http://localhost:4200/forms' is opened
    Then the title of the current page should be 'exact=TestAppUi'

  Scenario: assertCurrentPageTitleShouldNotBe
    Given the page at url 'http://localhost:4200/forms' is opened
    Then the title of the current page should not be 'exact=NotTestAppUi'

  Scenario: assertCurrentUrlShouldBe
    Given the page at url 'http://localhost:4200/forms' is opened
    Then the url of the current page should be 'exact=http://localhost:4200/forms'

  Scenario: assertCurrentUrlShouldNotBe
    Given the page at url 'http://localhost:4200/forms' is opened
    Then the url of the current page should not be 'exact=http://localhost:4200/formsNot'

  Scenario: assertElementPresent
    Given the page at url 'http://localhost:4200/forms' is opened
    Then the element 'id=presentElementId' should be present

  Scenario: assertElementNotPresent
    Given the page at url 'http://localhost:4200/forms' is opened
    Then the element 'id=absentElementId' should be absent

  Scenario: assertElementDisplayed
    Given the page at url 'http://localhost:4200/forms' is opened
    Then the element 'id=displayedElementId' should be displayed

  Scenario Outline: assertElementHidden
    Given the page at url 'http://localhost:4200/forms' is opened
    Then the element 'id=<elementId>' should be hidden
    Examples:
    |elementId            |
    |notDisplayedElementId|
    |invisibleElementId   |

  Scenario: assertElementEnabled
    Given the page at url 'http://localhost:4200/forms' is opened
    Then the element 'id=enabledElementId' should be enabled

  Scenario: assertElementDisabled
    Given the page at url 'http://localhost:4200/forms' is opened
    Then the element 'id=disabledElementId' should be disabled

  Scenario: assertElementAttributeShouldBe
    Given the page at url 'http://localhost:4200/forms' is opened
    Then the element 'id=withAttributeElementId' should have the attribute 'data-myattr' with a value of 'my value'

  Scenario: assertElementAttributeShouldNotBe
    Given the page at url 'http://localhost:4200/forms' is opened
    Then the element 'id=withAttributeElementId' should have the attribute 'data-myattr' with a value different from 'not my value'

  Scenario: assertElementCssClassPresent
    Given the page at url 'http://localhost:4200/forms' is opened
    Then the element 'id=withCssClassElementId' should have the CSS class 'class-2'

  Scenario: assertElementCssClassPresent
    Given the page at url 'http://localhost:4200/forms' is opened
    Then the element 'id=withCssClassElementId' should not have the CSS class 'missing-class-2'

  Scenario: assertElementCssClassAbsent
    Given the page at url 'http://localhost:4200/forms' is opened
    Then the element 'id=withCssClassElementId' should not have the CSS class 'missing-class-2'

  Scenario Outline: click
    Given the page at url 'http://localhost:4200/forms' is opened
    When I click the element '<elementLocator>'
    Then the text of element 'id=clickDestinationId' should be '<clickResult>'
    Examples:
      |elementLocator      |clickResult   |
      |id=clickableDivId   |div clicked   |
      |id=clickableButtonId|button clicked|

#  Scenario: sendKeys
#    Given the page at url 'http://localhost:4200/forms' is opened
#    When I type

