@echo off

REM --------------------------------------------
REM No need to edit anything past here
REM --------------------------------------------
set _BUILDFILE=%BUILDFILE%
set BUILDFILE=build.xml

:final

set _CLASSPATH=%CLASSPATH%

if "%JAVA_HOME%" == "" goto javahomeerror
if exist %JAVA_HOME%\lib\tools.jar set CLASSPATH=%CLASSPATH%;%JAVA_HOME%\lib\tools.jar

set CLASSPATH=%CLASSPATH%;build\lib\ant-1.4.jar;
set CLASSPATH=%CLASSPATH%;build\lib\crimson-1.1.jar;
set CLASSPATH=%CLASSPATH%;build\lib\jaxp-1.1.jar;
set CLASSPATH=%CLASSPATH%;build\lib\activation.jar;
set CLASSPATH=%CLASSPATH%;build\lib\mail.jar;
set CLASSPATH=%CLASSPATH%;build\lib\jms.jar;
set CLASSPATH=%CLASSPATH%;build\lib\jmxri.jar;
set CLASSPATH=%CLASSPATH%;build\lib\jmxtools.jar;

%JAVA_HOME%\bin\java.exe org.apache.tools.ant.Main -buildfile %BUILDFILE% %1 %2 %3 %4 %5 %6 %7 %8 %9

goto end

REM -----------ERROR-------------
:javahomeerror
echo "ERROR: JAVA_HOME not found in your environment."
echo "Please, set the JAVA_HOME variable in your environment to match the"
echo "location of the Java Virtual Machine you want to use."

:end
set BUILDFILE=%_BUILDFILE%
set _BUILDFILE=
set CLASSPATH=%_CLASSPATH%
set _CLASSPATH=
