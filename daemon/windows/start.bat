@echo off

set BASEDIR=%CD%
set SERVICE_NAME=PltoneDistWs
set SRV=%BASEDIR%\prunsrv.exe

echo start %SERVICE_NAME% 

%SRV% //ES//%SERVICE_NAME%

:end