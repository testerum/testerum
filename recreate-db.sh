#!/usr/bin/env bash

CURRENT_DIR="${PWD}"
SCRIPT_DIR="$(dirname "$0")"
PROJECT_ROOT_DIR="${SCRIPT_DIR}"

cd "${PROJECT_ROOT_DIR}"

mvn clean -Pinit-database -e -am -pl "db/db-migrations/db-init"

cd "$CURRENT_DIR"
