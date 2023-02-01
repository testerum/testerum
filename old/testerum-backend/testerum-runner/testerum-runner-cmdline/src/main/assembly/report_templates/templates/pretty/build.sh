#!/bin/bash

script_dir=$(dirname $0)
cd "${script_dir}"
rm -rf dist
npm run build-prod
rm -rf ../../../../../../../../../../package/runner/report_templates/pretty/report-template/*
cp -r dist/runner-report-app/* ../../../../../../../../../../package/runner/report_templates/pretty/report-template
cd -
