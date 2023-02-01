NUME: testerUi
 shared resources cant be edited online
 change buttons on edit pages DELETE & EDIT  --- OK APPLY CANCEL    


### Settings

 * the code that scans settings.properties should be moved outside SettingsManager (including removing the field "private val settingsFile: Path = settingsDirectory.resolve("config.properties")

 * **V1** IMPROVEMENT: fix / or \ or \\, now we have all 3 in the properties file
   * for  testerum.packageDirectory setting replace \ with /
 * **V2** detect cycle dependencies a={{b}} b={{a}}

### Composed Steps
 * **V2** step tree filtering
 * **V2** don't close/cancel dialogs without warning if I edited at least one field; it should warn me about this and only lose my work if I click "OK". Example: HTTP request dialog
 * **V2** The description field should support MarkedDown 

### Basic Steps
 * **V1** BUG: StepsService class has a threadpool that is never killed on project shutdown. This should be initialized and closed from spring
 * **V1** Add description to steps and params
 
### Tests
 * **V1** IMP: create dir in tests tree: put focus on the input box in the popup that appears
 
##### Backgrond Steps
  * **V2** on the test tree we can setup Background steps to run
  * **V2** this background tests should run before each test in the subdirectory
  
##### Parametrized tests
  * **V5** like Scenario Outline in cucumber
  
##### BUG Tracket linking
 * **V2** Link Jira ticket to a test case
 * **V2** Link to a specific requirement in Jira ticket
 
##### Create defect in the Bug Tracker
 * **V5** add all the steps as description automatically
 
##### Tests Version
##### Change History 
  
### Version Control for Tests and steps
  * **V2** GitHub
  
### Users & Licenses
### Product update
### Obfuscate core code
  
### STEPS
##### Selenium steps
 * **V5** create recorder
 
##### Database steps

### Demo App
 * **V1** Find a demo APP

# Website
### Site
### Forum
### Download
### Documentation

# TODO
 * **V5** support multiple projects
 
# Manual tests
 * Each test should have an nice ID
### Manual test run
 * **V2** Link test to User story and requirement
 * Should we share on GIT the Runners?
 * **V2** We should limit the amount of Finalized Executions.
 * **V5** On Execution Detail if you click on the PIE the tree should be filtered and we can add a Pie animation, to show the selected pice bigger
 * **V2** Manual tests should have an ID as number. In this way we can have nice and stable links
 

### Tests Version
### Change History 
### Report bug to bug tracker
### Screen recorder (browser plugin)
### Take Screen shot and edit it (paint)