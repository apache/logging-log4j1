#!/bin/sh
  
#--------------------------------------------
# No need to edit anything past here
#--------------------------------------------
if test -z "${JAVA_HOME}" ; then
    echo "ERROR: JAVA_HOME not found in your environment."
    echo "Please, set the JAVA_HOME variable in your environment to match the"
    echo "location of the Java Virtual Machine you want to use."
    exit
fi

# Set the sperator to ; on cygwin and to the usual : on real Unix.
if ["$OSTYPE" = "cygwin32" ] || [ "$OSTYPE" = "cygwin" ]
then
  SEP='\;'
else
  SEP=':'
fi

if test -f ${JAVA_HOME}/lib/tools.jar ; then
    CLASSPATH=${CLASSPATH}$SEP${JAVA_HOME}/lib/tools.jar
fi


for l in build/lib/*.jar 
do
  echo "Adding $l to CLASSPATH."
  CLASSPATH=${CLASSPATH}$SEP$l
done

# convert the unix path to windows
if [ "$OSTYPE" = "cygwin32" ] || [ "$OSTYPE" = "cygwin" ] ; then
   CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
fi

echo "Classpath is [$CLASSPATH]"
BUILDFILE=build.xml

${JAVA_HOME}/bin/java -classpath ${CLASSPATH} \
                       org.apache.tools.ant.Main \
                      -buildfile ${BUILDFILE} "$@"

#   -Djavax.xml.transform.TransformerFactory=com.icl.saxon.TransformerFactoryImpl\
