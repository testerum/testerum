#!/usr/bin/env bash

exec "${WORKSPACE}/testerum/runner/bin/testerum-runner.sh" \
  --repository-directory "${WORKSPACE}/integration-tests/tests" \
  --setting testerum.selenium.driver.browserType=CHROME \
  --setting testerum.selenium.driver.headless=true \
  --setting testerum.selenium.takeScreenshotAfterEachStep=true \
  --report=CONSOLE_DEBUG \
  --managed-reports-directory /var/www/downloads.testerum.com/reports
