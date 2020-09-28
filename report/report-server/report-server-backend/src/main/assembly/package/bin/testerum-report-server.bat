@echo off

@REM switch to UTF-8 "code page"
chcp 65001 > nul

set BASEDIR=%~dp0..

set JAVA_HOME=%BASEDIR%\jre
set JAVACMD=%JAVA_HOME%\bin\java

set CMD_LINE_ARGS=%*

set OPTS=
set OPTS=%OPTS% -Dfile.encoding=UTF-8
set OPTS=%OPTS% -Duser.timezone=GMT
set OPTS=%OPTS% -Dtesterum.packageDirectory="%BASEDIR%"
set OPTS=%OPTS% -jar "%BASEDIR%\lib\report-server-backend.jar"
set OPTS=%OPTS% %CMD_LINE_ARGS%

set ERROR_CODE=0
"%JAVACMD%" %OPTS%
if %ERRORLEVEL% NEQ 0 goto error
goto end

:error
set ERROR_CODE=%ERRORLEVEL%

:end
exit /B %ERROR_CODE%
