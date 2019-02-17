#!/usr/bin/env bash

set -e

if [[ "$#" -ne 1 ]]; then
    echo "SYNTAX: $0 <REPO_ROOT_DIR>"
    echo ""
    echo "where REPO_ROOT_DIR is the directory where the tests and composed steps are stored"
    exit 1
fi

REPO_ROOT_DIR="$1"

if [[ ! -d "${REPO_ROOT_DIR}" ]]; then
  echo "The directory [${REPO_ROOT_DIR}] does not exist or is not a directory"
  exit 2
fi

REPLACEMENTS="s/Given the HTTP Mock Server <<([^>]*)>> with the Mock Request <<([^>]*)>>/Given the HTTP mock server <<\1>> with the mock request <<\2>>/g
s/When I execute <<([^>]*)>> HTTP Request/When I execute the HTTP request <<\1>>/g
s/Then I expect <<([^>]*)>> HTTP Response/Then the HTTP response should be <<\1>>/g
s/When writing <<([^>]*)>> in <<([^>]*)>> database/When I execute the SQL <<\1>> on the database <<\2>>/g
s/Then verify database <<([^>]*)>> state is like in <<([^>]*)>> file/Then the state of the database <<\1>> should be <<\2>>/g
s/When I submit the form containing the field identified by the element locator <<([^>]*)>>/When I submit the form containing the field <<\1>>/g
s/Given the page at url <<([^>]*)>> is opened/Given the page at url <<\1>> is open/g"

while IFS= read -r line; do
    echo "Executing replacement [${line}]"
    echo ""

    find "$REPO_ROOT_DIR" \
        \( -iname '*.test' -or -iname '*.step' \) \
        -exec echo "processing ["{}"]" \; -exec sed -i -E "${line}" {} \;

    echo "------------------------------------------------------------------------------------------------------------"
    echo ""
done <<< ${REPLACEMENTS}
