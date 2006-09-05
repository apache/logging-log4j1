
# This little Unix script compiles the files T113.java and T12.java
# with different classpaths.

# You have to set the classpath to point to rt.jar depending on your
# environment.

CLASSPATH=".:$JAVA_HOME/jre/lib/rt.jar:log4j-1.1.3.jar"
javac -deprecation T113.java

CLASSPATH=".:$JAVA_HOME/jre/lib/rt.jar:log4j-1.2alpha7.jar"
javac -deprecation T12.java