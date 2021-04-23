#!/usr/bin/env bash

unbuffer \
    npx ng serve \
    --proxy-config proxy-conf.json \
    --live-reload false \
    --source-map false \
    --progress true \
    | sed 's/\x1b\[33m/\x1b\[38;2;180;90;0m/g'

