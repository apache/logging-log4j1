
PKG_DIR :=org/apache/log4j
DEPTH   :=../../../../..
JSOURCES:=DailyRollingFileAppender.java\
	RollingFileAppender.java\
	PatternLayout.java\
	AsyncAppender.java\
	NDC.java\
	AppenderSkeleton.java\
	CategoryKey.java\
	ProvisionNode.java\
	StressCategory.java \
	FileAppender.java \
	SimpleLayout.java\
	TTCCLayout.java\
	Category.java \
	Hierarchy.java\
        Layout.java \
	Appender.java \
	Priority.java\
	WriterAppender.java\
	ConsoleAppender.java\
        MDC.java\
        Level.java\
        Logger.java\
	PropertyConfigurator.java\
	BasicConfigurator.java\
        HTMLLayout.java\
        LogManager.java\




ifdef $(ISJDK1)
 JSOURCES:=$(JSOURCES) RollingFileAppenderBeanInfo.java
endif


SUBDIRS :=helpers spi config or xml net nt varia test performance examples 

# include master-rule file
include $(DEPTH)/make/make.inc






