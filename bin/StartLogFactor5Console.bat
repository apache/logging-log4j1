@echo off

if not "%LOG4J_HOME%" == "" goto start

SET LOG4J_HOME=..

:start

java -fullversion
set LOCALCLASSPATH=
for %%i in ("%LOG4J_HOME%\dist\lib\*.jar") do call "%LOG4J_HOME%\bin\lcp.bat" "%%i"

echo using classpath %LOCALCLASSPATH%

java -classpath %LOCALCLASSPATH% org.apache.log4j.lf5.StartLogFactor5

exit

:usage
echo usage: lf5 (target)

:eof
pause

