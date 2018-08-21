@echo off
set BASEDIR=%CD%
set SERVICE_NAME=PltoneDistWs
set SRV=%BASEDIR%\prunsrv.exe

%SRV% //DS//%SERVICE_NAME%

:end