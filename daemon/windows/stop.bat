@echo off

set BASEDIR=%CD%
set SERVICE_NAME=PltoneDistWs
set SRV=%BASEDIR%\prunsrv.exe

echo stop %SERVICE_NAME%

%SRV% //SS//%SERVICE_NAME%

:end