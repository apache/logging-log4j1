'  Batch file for running tests on JDK 1.1
'
SET CLASSPATH=\java\junit3.8.1\junit.jar;\java\crimson-1.1.3\crimson.jar;\java\jakarta-oro-2.0.8\jakarta-oro-2.0.8.jar;..\dist\classes;classes;resources
java junit.textui.TestRunner org.apache.log4j.CoreTestSuite
java junit.textui.TestRunner org.apache.log4j.MinimumTestCase
java junit.textui.TestRunner org.apache.log4j.LoggerTestCase
java junit.textui.TestRunner org.apache.log4j.xml.DOMTestCase
java junit.textui.TestRunner org.apache.log4j.xml.CustomLevelTestCase
java junit.textui.TestRunner org.apache.log4j.customLogger.XLoggerTestCase
del classes\log4j.xml
del classes\log4j.properties
java junit.textui.TestRunner org.apache.log4j.defaultInit.TestCase1
copy input\xml\defaultInit.xml classes\log4j.xml
java junit.textui.TestRunner org.apache.log4j.defaultInit.TestCase2
del classes\log4j.xml
copy input\xml\defaultInit.xml classes\log4j.xml
java -Dlog4j.defaultInitOverride=true junit.textui.TestRunner org.apache.log4j.defaultInit.TestCase1
del classes\log4j.xml
copy input\defaultInit3.properties classes\log4j.properties
java junit.textui.TestRunner org.apache.log4j.defaultInit.TestCase3
del classes\log4j.properties
copy input\xml\defaultInit.xml classes\log4j.xml
copy input\defaultInit3.properties classes\log4j.properties
java junit.textui.TestRunner org.apache.log4j.defaultInit.TestCase4
del classes\log4j.xml
del classes\log4j.properties
java junit.textui.TestRunner org.apache.log4j.xml.XMLLayoutTestCase
java junit.textui.TestRunner org.apache.log4j.AsyncAppenderTestCase
java junit.textui.TestRunner org.apache.log4j.helpers.OptionConverterTestCase
java junit.textui.TestRunner org.apache.log4j.helpers.BoundedFIFOTestCase
java junit.textui.TestRunner org.apache.log4j.helpers.CyclicBufferTestCase
java junit.textui.TestRunner org.apache.log4j.or.ORTestCase
java junit.textui.TestRunner org.apache.log4j.varia.LevelMatchFilterTestCase
java junit.textui.TestRunner org.apache.log4j.helpers.PatternParserTestCase
java junit.textui.TestRunner org.apache.log4j.PatternLayoutTestCase
java junit.textui.TestRunner org.apache.log4j.DRFATestCase
