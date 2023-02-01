#!/usr/bin/env bash

# Using the "Artifact Resolver" plugin, Jenkins has downloaded the latest linux .tar.gz Testerum package to "testerum-linux-package.tar.gz".
# Here, we're extracting it to the "testerum" directory.
rm -rf "${WORKSPACE}/testerum"
mkdir "${WORKSPACE}/testerum"
tar xzf "${WORKSPACE}/testerum-linux-package.tar.gz" -C "${WORKSPACE}/testerum"

