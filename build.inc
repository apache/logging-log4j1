  
  <target name="Usage">
    <echo>
    ant build   - compile all project files, if a certain library is missing, 
	          then the compilation of its dependents is skipped.

    ant compile - compile files in local directory only

    ant javadoc - build project javadoc files

    ant jar     - build log4j-core and log4j jar files

    </echo>
  </target>


  <target name="swingCheck">
    <available classname="javax.swing.JTextPane" property="swing-present"/>
  </target>   
  <target name="swing" depends="swingCheck" if="swing-present">
    <echo message="Swing is present."/>
  </target>

  <target name="junitCheck">
    <available classname="junit.framework.TestCase" property="junit-present"/>
  </target>   
  <target name="junit" depends="junitCheck" if="junit-present">
    <echo message="JUnit is present."/>
  </target>

  <target name="xercesCheck">
    <available classname="org.apache.xerces.parsers.DOMParser" 
                          property="xerces-present"/>
    <available classname="org.apache.xerces.parsers.DOMParser" property="jaxp-present"/>
  </target>   
  <target name="xerces" depends="xercesCheck" if="xerces-present">
    <echo message="Xerces is present."/>
  </target>

  <target name="sunParserCheck">
    <available classname="com.sun.xml.parser.DocumentBuilderFactoryImpl" 
                          property="sunParser-present"/>
    <available classname="com.sun.xml.parser.DocumentBuilderFactoryImpl" property="jaxp-present"/>
  </target>   
  <target name="sunParser" depends="sunParserCheck" if="sunParser-present">
    <echo message="Sun JAXP parser is present."/>
  </target>


  <target name="jmsCheck">
    <available classname="javax.jms.Message" property="jms-present"/>
  </target>   
  <target name="jms" depends="jmsCheck" if="jms-present">
    <echo message="JMS is present."/>
  </target>

  <target name="jndiCheck">
    <available classname="javax.naming.Context" property="jndi-present"/>
  </target>   
  <target name="jndi" depends="jndiCheck" if="jndi-present">
    <echo message="JNDI is present."/>
  </target>

  <target name="javamailCheck">
    <available classname="javax.mail.Message" property="javamail-present"/>
  </target>   
  <target name="javamail" depends="javamailCheck" if="javamail-present">
    <echo message="JAVAMAIL is present."/>
  </target>


  <target name="init">
    <tstamp />
    <property name="srcdir" value="." />
    <property name="classes" value="classes" />
    <property name="stem" value="org/apache/log4j" />
    <property name="build.compiler" value="jikes"/>	
    <property name="version" value="1.0.4"/>  
  </target>



  <target name="compile" depends="init">
    <javac srcdir="." 
           destdir="${classes}"
           includes="&package;/*.java"
           depend="on"/>    
  </target>

  <target name="build" depends="init, build.core, build.swing, build.xml,
                                build.unitTests, build.javamail, build.jms"/>

  <target name="build.core" depends="init">
    <mkdir dir="${classes}" />
    <javac srcdir="${srcdir}"
	   destdir="${classes}"
	   includes="${stem}/**/*.java, ${stem}/xml/XMLLayout.java,"
	   classpath="${classpath}"
	   excludes="misc/*, **/UnitTest*.java,
	            **/doc-files/*,
                    ${stem}/xml/**,
		    ${stem}/net/SMTPAppender.java,
               	    ${stem}/net/JMS*.java,
               	    ${stem}/or/MessageRenderer.java,
		    **/pending/**" 
	    debug="on"/>			
    <rmic base="${classes}" 
	  classname="org.apache.log4j.examples.NumberCruncherServer"
	/>
  </target>

  <target name="build.swing" depends="init, swing" if="swing-present">   
    <mkdir dir="${classes}/icons"/>
    <copy todir="${classes}/icons">
      <fileset dir="icons"/>
    </copy>
    <javac srcdir="${srcdir}"
	   destdir="${classes}"
	   includes="${stem}/gui/*.java"
           excludes="**/JListView.java, **/JTableAppender.java"
           classpath="${classpath}"/>
  </target>
	
  <target name="build.unitTests" depends="init, junit" if="junit-present">   
    <javac srcdir="${srcdir}"
	   destdir="${classes}"
	   includes="**/UnitTest*.java"
	   classpath="${classpath}"/>
</target>

 <target name="build.xml" depends="init, xerces, sunParser" if="jaxp-present">
    <echo message="Really compiling org.log4j.xml" />
    <javac 
	   srcdir="${srcdir}"
	   destdir="${classes}"
	   includes="${stem}/xml/**/*.java"
           excludes="${stem}/xml/examples/doc-files/**.java,
	             ${stem}/xml/Transform.java"
	   classpath="${classpath}"/>
    <copy file="${stem}/xml/log4j.dtd"
	  tofile="${classes}/${stem}/xml/log4j.dtd" />
</target>

 <target name="build.javamail" depends="init, javamail" 
               if="javamail-present">   
    <echo message="Really compiling org.log4j.net.SMTPAppender" />
    <javac 
	   srcdir="${srcdir}"
	   destdir="${classes}"
	   includes="${stem}/net/SMTPAppender.java"
	   classpath="${classpath}"/>
  </target>

  <target name="build.jms" depends="init, jms, jndi" if="jms-present">   
    <javac srcdir="${srcdir}"
	   destdir="${classes}"
	   includes="${stem}/net/JMS*.java"
	   classpath="${classpath}"/>
  </target>



  <target name="clean" depends="init">
	<delete dir="${classes}/&package;" />
  </target>

  <target name="jar" depends="log4j-core.jar, log4j.jar"/>

  <!-- Create log4j-core.jar, exluding non-core files -->
  <target name="log4j-core.jar" depends="build">
    <delete file="log4j-core.jar"/>
    <jar jarfile="log4j-core.jar" basedir="${classes}"
	 includes="**/log4j/*.class, **/log4j/helpers/*.class,
                  **/log4j/spi/*.class, **/log4j/or/*.class"
	 excludes="**/StressCategory*, **/TTCC*, **/SimpleLayout.*, 
	          **/AsyncAppender.*, **/Dispatcher.*, **/BoundedFIFO.*,
	          **/Syslog*, **/RelativeTimeDateFormat.*,
		  **/gui/**/*.class, **/Loader.class,
		  **/UnitTest*.class, 
		  ${stem}/or/MessageRenderer.class,
		  ${stem}/spi/TriggeringEventEvaluator.class
		  org/apache/log4j/helpers/CyclicBuffer.class"
        manifest="manifest.mf"
    />
  </target>

  <!-- Create log4j.jar, excluding tests and other odds and
       ends. -->
  <target name="log4j.jar" depends="build">
    <delete file="log4j.jar"/>
    <jar jarfile="log4j.jar" basedir="${classes}"
	includes="**/log4j/*.class, ${stem}/xml/log4j.dtd, 
	          **/log4j/helpers/*.class, icons/**,
		  **/log4j/spi/*.class,  **/log4j/net/*.class,
		  **/log4j/varia/*.class, **/log4j/nt/*.class,
		  **/log4j/xml/*.class, 
		  **/log4j/or/*.class,
		  **/log4j/gui/TextPaneAppender.class"
	excludes="**/StressCategory*, **/UnitTest**"
        manifest="manifest.mf"
    />
  </target>

  <target name="javadoc" depends="init">
    <mkdir dir="javadoc" />

    <javadoc sourcepath="${srcdir}" 
           destdir="javadoc" 
	   packagenames="org.apache.log4j,
		         org.apache.log4j.spi,
		         org.apache.log4j.or,
                         org.apache.log4j.helpers,
	                 org.apache.log4j.performance,
		         org.apache.log4j.xml,
			 org.apache.log4j.examples,
		         org.apache.log4j.net,
			 org.apache.log4j.nt,
			 org.apache.log4j.varia,
		         org.apache.log4j.xml.examples"
	   protected="true"				       
	   version="true"
	   author="true"
	   use="true"		   
	   overview="doc/overview.html"
	   doctitle="log4j version ${version}&lt;br&gt;API Specification"
	   windowtitle="Log4j Version ${version}" 
           header="&lt;b&gt;Log4j ${version}&lt;/b&gt;"
	   bottom="Please &lt;a href=http://lists.sourceforge.net/mailman/listinfo/log4j-announce&gt;&lt;b&gt;notify me&lt;/b&gt;&lt;/a&gt; about new log4j releases. &lt;hr&gt;"> 
		   
           <link offline="true" 
                 href="http://java.sun.com/products/jdk/1.2/docs/api"
                 packageListLoc="packaging"/>
    </javadoc>		
  </target>



