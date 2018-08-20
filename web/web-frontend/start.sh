#!/usr/bin/env bash

# ng serve --proxy-config proxy-conf.json --configuration hmr \
ng serve --proxy-config proxy-conf.json --live-reload false \
    | tee /dev/tty \
    | gawk '/Compiled successfully./ { system("notify-send ng\\ serve Compiled\\ successfully." timeTaken " -i info --expire-time 2000") }
           /ERROR in/ { system("notify-send ng\\ serve ERROR -i error --expire-time 999999999") }'

