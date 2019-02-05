#!/bin/sh

# resolve links - $0 may be a softlink
PRG="$0"

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

exec "$JAVACMD" \
    -Dfile.encoding=UTF8 \
    -Duser.timezone=GMT \
    -Dlogback.configurationFile="${BASEDIR}/conf/testerum-logback.xml" \
    -Dtesterum.web.httpPort="${TESTERUM_WEB_HTTPPORT}" \
    -Dtesterum.packageDirectory="${BASEDIR}" \
    -Xmx1024m \
    -classpath "${BASEDIR}/lib/*" \
    com.testerum.web_backend.TesterumWebMain \
    "$@"
