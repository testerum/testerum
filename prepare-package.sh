#!/usr/bin/env bash

mvn package -T 2C -Dmaven.test.skip=true -am -pl steps/assertion-steps/,steps/http-steps/,steps/rdbms-steps/,steps/selenium-steps/,testerum-backend/testerum-runner/testerum-runner-cmdline

