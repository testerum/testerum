#!/usr/bin/env bash

mvn package -T 2C -Dmaven.test.skip=true -am -pl steps/test-steps/,steps/assertion-steps/,steps/debug-steps/,steps/http-steps/,steps/json-steps/,steps/rdbms-steps/,steps/selenium-steps/,steps/android-steps/,steps/util-steps/,steps/vars-steps/,testerum-backend/testerum-runner/testerum-runner-cmdline
