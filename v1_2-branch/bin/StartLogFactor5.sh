#! /bin/sh

if [ -z "$LOG4J_HOME" ] ; then
    LOG4J_HOME=..
fi

JAVACMD=$JAVA_HOME/bin/java

# add in the dependency .jar files
DIRLIBS=${LOG4J_HOME}/dist/lib/*.jar
for i in ${DIRLIBS}
do

    if [ "$i" != "${DIRLIBS}" ] ; then
        LOCALCLASSPATH=$LOCALCLASSPATH:"$i"
    fi

done

$JAVACMD -classpath "$LOCALCLASSPATH" org.apache.log4j.lf5.StartLogFactor5