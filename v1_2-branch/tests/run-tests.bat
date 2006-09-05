'  Batch file for running tests on JDK 1.1
'
SET CLASSPATH=\java\junit3.8.1\junit.jar;\java\crimson-1.1.3\crimson.jar;\java\jakarta-oro-2.0.8\jakarta-oro-2.0.8.jar;..\dist\classes;classes;..\..\classes;resources;%log4j.jar%
mkdir classes
cd src\java
javac -d ..\..\classes org\apache\log4j\util\SerializationTestHelper.java
javac -d ..\..\classes org\apache\log4j\spi\LoggingEventTest.java
javac -d ..\..\classes org\apache\log4j\LevelTest.java
javac -d ..\..\classes org\apache\log4j\FileAppenderTest.java
javac -d ..\..\classes org\apache\log4j\CoreTestSuite.java
javac -d ..\..\classes org\apache\log4j\util\UnexpectedFormatException.java
javac -d ..\..\classes org\apache\log4j\util\Filter.java
javac -d ..\..\classes org\apache\log4j\util\Compare.java
javac -d ..\..\classes org\apache\log4j\util\ControlFilter.java
javac -d ..\..\classes org\apache\log4j\util\Transformer.java
javac -d ..\..\classes org\apache\log4j\util\LineNumberFilter.java
javac -d ..\..\classes org\apache\log4j\util\AbsoluteDateAndTimeFilter.java
javac -d ..\..\classes org\apache\log4j\MinimumTestCase.java
javac -d ..\..\classes org\apache\log4j\VectorAppender.java
javac -d ..\..\classes org\apache\log4j\LoggerTestCase.java
javac -d ..\..\classes org\apache\log4j\util\ISO8601Filter.java
javac -d ..\..\classes org\apache\log4j\util\SunReflectFilter.java
javac -d ..\..\classes org\apache\log4j\util\JunitTestRunnerFilter.java
javac -d ..\..\classes org\apache\log4j\xml\DOMTestCase.java
javac -d ..\..\classes org\apache\log4j\xml\XLevel.java
javac -d ..\..\classes org\apache\log4j\xml\CustomLevelTestCase.java
javac -d ..\..\classes org\apache\log4j\customLogger\XLogger.java
javac -d ..\..\classes org\apache\log4j\customLogger\XLoggerTestCase.java
javac -d ..\..\classes org\apache\log4j\defaultInit\TestCase1.java
javac -d ..\..\classes org\apache\log4j\defaultInit\TestCase3.java
javac -d ..\..\classes org\apache\log4j\defaultInit\TestCase4.java
javac -d ..\..\classes org\apache\log4j\util\XMLTimestampFilter.java
javac -d ..\..\classes org\apache\log4j\util\XMLLineAttributeFilter.java
javac -d ..\..\classes org\apache\log4j\xml\XMLLayoutTestCase.java
javac -d ..\..\classes org\apache\log4j\AsyncAppenderTestCase.java
javac -d ..\..\classes org\apache\log4j\helpers\OptionConverterTestCase.java
javac -d ..\..\classes org\apache\log4j\helpers\BoundedFIFOTestCase.java
javac -d ..\..\classes org\apache\log4j\helpers\CyclicBufferTestCase.java
javac -d ..\..\classes org\apache\log4j\or\ORTestCase.java
javac -d ..\..\classes org\apache\log4j\varia\LevelMatchFilterTestCase.java
javac -d ..\..\classes org\apache\log4j\helpers\PatternParserTestCase.java
javac -d ..\..\classes org\apache\log4j\util\AbsoluteTimeFilter.java
javac -d ..\..\classes org\apache\log4j\util\RelativeTimeFilter.java
javac -d ..\..\classes org\apache\log4j\PatternLayoutTestCase.java
javac -d ..\..\classes org\apache\log4j\MyPatternParser.java
javac -d ..\..\classes org\apache\log4j\MyPatternLayout.java
javac -d ..\..\classes org\apache\log4j\DRFATestCase.java
cd ..\..
mkdir output
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
