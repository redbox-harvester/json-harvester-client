@echo off

if "%1" == "" goto usage
if "%2" == "" goto usage
if "%3" == "" goto usage

set DBNAME=%1
set USERNAME=%2
set PASSWORD=%3

java -Denvironment=production -Dlog4j.configuration=prod-log4j.xml -Djdbc.user=%USERNAME% -Djdbc.pw=%PASSWORD% -Djdbc.dbname=%DBNAME% -jar json-harvester-client.jar jdbc
goto end

:usage
echo Usage: %0 databasename username password

:end