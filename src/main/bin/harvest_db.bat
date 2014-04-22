@echo off

if "%1" == "" goto usage
if "%2" == "" goto usage

set USERNAME=%1
set PASSWORD=%2

java -Denvironment=production -Dlog4j.configuration=prod-log4j.xml -Djdbc.user=%USERNAME% -Djdbc.pw=%PASSWORD% -jar json-harvester-client.jar sample/config/config-jdbc.groovy
goto end

:usage
echo Usage: %0 username password

:end