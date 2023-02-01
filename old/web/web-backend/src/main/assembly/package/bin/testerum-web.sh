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

LAUNCHER_CONFIG_FILE=${HOME}/.testerum/conf/testerum-launcher.properties

if [ -f "${LAUNCHER_CONFIG_FILE}" ]
then
    TESTERUM_WEB_HTTPPORT=$(grep 'testerum.web.httpPort' ${LAUNCHER_CONFIG_FILE} | cut -d'=' -f2)
else
    TESTERUM_WEB_HTTPPORT=9999
fi

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
    -Dlogback.configurationFile="${BASEDIR}/conf/testerum-logback.xml" \
    -Dtesterum.web.httpPort="${TESTERUM_WEB_HTTPPORT}" \
    -Dtesterum.packageDirectory="${BASEDIR}" \
    -DcontextPath="${CONTEXT_PATH}" \
    -Xmx1024m \
    -classpath "${BASEDIR}/lib/*:${BASEDIR}/basic_steps/*" \
    com.testerum.web_backend.TesterumWebMain \
    "$@"
