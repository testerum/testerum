@echo off

REM switch to UTF-8 "code page"
chcp 65001 > nul

set BASEDIR=%~dp0..

set LAUNCHER_CONFIG_FILE=%HOMEPATH%\.testerum\conf\testerum-launcher.properties

if exist "%LAUNCHER_CONFIG_FILE%" (
    for /f "usebackq" %%i in (`findstr /r "^testerum.web.httpPort" "%LAUNCHER_CONFIG_FILE%"`) do (for /f "tokens=2,2 delims==" %%j in ("%%i") do set TESTERUM_WEB_HTTPPORT=%%j)
) else (
    set TESTERUM_WEB_HTTPPORT=9999
)

set CONTEXT_PATH=/
:loop
if not "%1"=="" (
    if "%1"=="--context-path" (
        set CONTEXT_PATH=%2
        shift
    )
    shift
    goto :loop
)

set JAVA_HOME=%BASEDIR%\jre
set JAVACMD=%JAVA_HOME%\bin\java

set CMD_LINE_ARGS=%*

set OPTS=
set OPTS=%OPTS% -Dfile.encoding=UTF-8
set OPTS=%OPTS% -Duser.timezone=GMT
set OPTS=%OPTS% -Dlogback.configurationFile="%BASEDIR%\conf\testerum-logback.xml"
set OPTS=%OPTS% -Dtesterum.web.httpPort=%TESTERUM_WEB_HTTPPORT%
set OPTS=%OPTS% -Dtesterum.packageDirectory="%BASEDIR%"
set OPTS=%OPTS% -DcontextPath="%CONTEXT_PATH%"
set OPTS=%OPTS% -Xmx1024m
set OPTS=%OPTS% -classpath "%BASEDIR%\lib\*;%BASEDIR%\basic_steps\*"
set OPTS=%OPTS% com.testerum.web_backend.TesterumWebMain
set OPTS=%OPTS% %CMD_LINE_ARGS%

set ERROR_CODE=0
"%JAVACMD%" %OPTS%
if %ERRORLEVEL% NEQ 0 goto error
goto end

:error
set ERROR_CODE=%ERRORLEVEL%

:end
exit /B %ERROR_CODE%
