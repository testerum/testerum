#!/usr/bin/env bash

# first destroy the containers that are up from the previous run
(cd "${WORKSPACE}/integration-tests/app/docker/" && docker-compose down)

# then, start docker
(cd "${WORKSPACE}/integration-tests/app/docker/" && docker-compose up -d)
