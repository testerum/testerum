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
    "$@"
