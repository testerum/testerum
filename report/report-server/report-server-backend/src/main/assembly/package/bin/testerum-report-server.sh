#!/usr/bin/env bash

# resolve links - $0 may be a softlink
PRG="$0"

CONTEXT_PATH=/
POSITIONAL_ARGS=()
while [[ $# -gt 0 ]] ; do
    key="${1}"

    case $key in
        --context-path)
            CONTEXT_PATH="${2}"
            shift # past argument
            shift # past value
            ;;
        *) # unknown option
            POSITIONAL_ARGS+=("${1}")
            shift # past argument
            ;;
    esac
done
set -- "${POSITIONAL_ARGS[@]}" # restore positional arguments

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

PRGDIR=`dirname "$PRG"`
BASEDIR=`cd "$PRGDIR/.." >/dev/null; pwd`

JAVA_HOME="${BASEDIR}/jre"
if [ ! -d "$JAVA_HOME" ]; then
    JAVA_HOME="$(cd $(dirname "${BASEDIR}/../../PlugIns/jre.bundle/Contents/Home/jre") && pwd -P)/$(basename "${BASEDIR}/jre")"
fi

JAVACMD="${JAVA_HOME}/bin/java"

# workaround to Mac issue: the Mac extract utility doesn't properly restore the executable bit on this file
if [ -f "${JAVA_HOME}/lib/jspawnhelper" ]
then
    chmod +x "${JAVA_HOME}/lib/jspawnhelper"
fi

exec "$JAVACMD" \
    -Dfile.encoding=UTF8 \
    -Duser.timezone=GMT \
    -Dtesterum.packageDirectory="${BASEDIR}" \
    -jar "${BASEDIR}/lib/report-server-backend.jar" \
    --contextPath="${CONTEXT_PATH}" \
    "$@"
