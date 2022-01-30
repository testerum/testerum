@ECHO ON
set portid=9999
FOR /F "tokens=5" %%T IN ('netstat -a -n -o ^| findstr %portid% ') DO (
SET /A ProcessId=%%T) &GOTO SkipLine                                                   
:SkipLine                                                                              
echo ProcessId to kill = %ProcessId%
taskkill /f /pid %ProcessId%
PAUSE
