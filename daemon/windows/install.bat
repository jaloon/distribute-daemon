@echo off

rem ���ó�������
set SERVICE_EN_NAME=PltoneDistWs
set SERVICE_CH_NAME=����ͨ�������ͽӿ�

rem ���ó������������������
set BASEDIR=%CD%
set CLASSPATH=%BASEDIR%\dist-forward-2.2.0.jar;%BASEDIR%\lib\*
set MAIN_CLASS=com.pltone.seal.distforward.daemon.WindowsDaemon

rem ����prunsrv·�� 
set SRV=%BASEDIR%\prunsrv.exe

rem ������־·������־�ļ�ǰ׺
set LOGPATH=%BASEDIR%\logs

rem �����Ϣ
echo SERVICE_NAME: %SERVICE_EN_NAME%
echo JAVA_HOME: %JAVA_HOME%
echo MAIN_CLASS: %MAIN_CLASS%
echo prunsrv path: %SRV%

rem ����jvm
if "%JVM%" == "" goto findJvm
if exist "%JVM%" goto foundJvm
:findJvm
set "JVM=%JAVA_HOME%\jre\bin\server\jvm.dll"
if exist "%JVM%" goto foundJvm
echo can not find jvm.dll automatically,
echo please use COMMAND to localation it
echo for example : set "JVM=C:\Program Files\Java\jdk1.8.0_25\jre\bin\server\jvm.dll"
echo then install service
goto end
:foundJvm

rem ��װ
"%SRV%" //IS//%SERVICE_EN_NAME% --Description="%SERVICE_CH_NAME%" "--Classpath=%CLASSPATH%" "--Install=%SRV%" "--JavaHome=%JAVA_HOME%" "--Jvm=%JVM%" --JvmMs=256 --JvmMx=1024 --Startup=auto --JvmOptions=-Djcifs.smb.client.dfs.disabled=false ++JvmOptions=-Djcifs.resolveOrder=DNS "--StartPath=%BASEDIR%" --StartMode=jvm --StartClass=%MAIN_CLASS% --StartMethod=start "--StopPath=%BASEDIR%" --StopMode=jvm --StopClass=%MAIN_CLASS% --StopMethod=stop --LogPath=%LOGPATH% --StdOutput=auto --StdError=auto

:end