
The tests in this directory (LOG4J_HOME/tests/integration/) test
log4j's integration with various Application Servers. Most of the
tests deal with the logging separation problem while others deal with
class loader issues. 

Unfortunately, we are currently unable to test web-applicatin
recycling with Cactus.


Prerequisites
=============

You need a recent version of Ant.

You need to place the following jar files in the ./lib/ directory.

  aspectjrt-1.1.1.jar
  cactus-1.6.1.jar
  cactus-ant-1.6.1.jar
  commons-httpclient-2.0.jar
  commons-logging-1.0.4.jar

They are shipped by Cactus.

You also need to place 'servletapi-2.3.jar' or equivalent in
./otherlib/ directory. This jar file also ships with Cactus.

Modify the 'build.properties.sample' file as appropriate for your
environment and copy it as 'build.properties'.

Make sure that you have a copy of Tomcat 5.0.x installed and
available. The 'tomcat5x.home' property should point to your Tomcat
5.0.x installation.

Running the tests
=================

There nothing more to it than invoking the 'ant test' command.
