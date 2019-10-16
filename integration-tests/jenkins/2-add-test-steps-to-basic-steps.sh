#!/usr/bin/env bash

# Using the "Artifact Resolver" plugin, Jenkins has downloaded the latest test-steps jar file to Testerum package as "test-steps.jar".
# We need to copy this file to basic_steps Testerum directory
cp "${WORKSPACE}/test-steps.jar" "${WORKSPACE}/testerum/basic_steps/test-steps.jar"
