
@REM This little script compiles the files T113.java and T12.java with
@REM different classpaths.
@REM
@REM You have to set the classpath to point to rt.jar depending on your
@REM environment.

set CLASSPATH=.;%JAVA_HOME%\jre\lib\rt.jar;log4j-1.1.3.jar

echo [%CLASSPATH%]

javac T113.java

set CLASSPATH=.;%JAVA_HOME%\jre\lib\rt.jar;log4j-1.2alpha7.jar
javac -deprecation T12.java