# Apache Log4j 1

Dear Log4j community,

While working on the December 2021 Apache Log4j 2 releases the Apache
Logging Services PMC received requests to reevaluate the 2015
End-of-Life (EOL) decision for Apache Log4j 1, which has seen its
latest release in 2012.

We have considered these requests and discussed various options.
Ultimately we came to the unanimous decision that the only sustainable
approach is to continue to focus on Log4j 2. The PMC hereby reconfirms
the 2015 EOL announcement of Log4j 1, meaning no resources will be
invested into the Log4j 1 codebase. We encourage users to update to
recent versions of Log4j 2. We welcome every effort to contribute to
the Log4j community. Please use the developer mailing lists to get in
touch: https://logging.apache.org/log4j/2.x/mail-lists.html

The Log4j 1 source code will continue to be publicly available but
Pull Requests will be closed as "Won't Fix". The Apache License allows
for code forks that respect Apache Software Foundation Trademarks.

Here are some of the reasons we believe this is the right choice for
the Log4j project:

## Log4j 2 supports migration from Log4j 1

We've made improvements to
https://logging.apache.org/log4j/2.x/manual/migration.html to better
explain the process. Many users are not aware that Log4j 2 now
supports Log4j 1 configuration files, since this feature is relatively
new. We believe most applications using Log4j 1 can now simply replace
the Log4j 1.x jar with Log4j 2 jars and be able to run. Users are
encouraged to contact us through the project mailing lists
(https://logging.apache.org/log4j/2.x/mail-lists.html) if there are
additional areas for improvement.

## Log4j 1 deadlock and multithreading design limitations

The decision to relaunch the Log4j project as Log4j 2 meant we had an
opportunity to correct long standing design deficiencies. One of these
fundamental design deficiencies has to do with how to handle
multithreading within the library. The following mailing list question
is but one example of known multithreading issues with Log4j 1:
https://lists.apache.org/thread/7yqrmzqgzpxmbcc7skl0vr8z33fk4hd4

## High-complexity Log4j 1 bugs

In addition to the items listed, many other issues can be found in
Bugzilla: https://bz.apache.org/bugzilla

| Issue | Description |
| ----- | ----------- |
| 50213 | Category callAppenders synchronization causes java.lang.Thread.State: BLOCKED |
| 46878 | Deadlock in 1.2.15 caused by AsyncAppender and ThrowableInformation classes |
| 41214 | Deadlock with RollingFileAppender |
| 44700 | Log4J locks rolled log files (files can’t be deleted) |
| 49481 | Log4j stops writing to file, and then causes server to lockup |
| 50323 | Vulnerability in NTEventLogAppender |
| 50463 | AsyncAppender causing deadlock when dispatcher thread dies |
| 50858 | Classloader leak when using Log4j in a webapp container such as Tomcat, WebLogic |
| 52141 | [STUCK] ExecuteThread...Blocked trying to get lock: org/apache/log4j/Logger@0xc501e0a8[fat lock] |
| 54009 | Thread is getting Blocked |
| 54325 | Concurrency issues in AppenderAttachableImpl |

## Complexities with Log4j 1 build system that could impact binary compatibility

Apart from the issues listed above, Log4j 1 suffers from a challenging
build system designed around long outdated versions of Java and
operating system specific Appenders that the current development team
cannot support. Taking shortcuts in proposed fixes means an updated
release would not support all the environments of the original 1.2.x
release. Patches to Log4j 1 would also have to be compatible with the
existing Log4j 2 migration path.

## Limited Log4j 1 community

The Apache Logging PMC and committer community has been focused on the
success of Log4j 2 for nearly a decade. There had been little to no
interest in Log4j 1 in the years leading up to the 2015 EOL
announcement. While there might be people interested in working
independently on Log4j 1, until the Logging Services community can
gauge the merit of those contributors, the PMC would have to review
and apply all patches, drive the release process, and provide future
support. We feel that effort is better spent improving non-legacy
code. We welcome community contributions in the migration components
for better tooling and support.

## Unfixed Vulnerabilities

Several security vulnerabilities have been discovered in Log4j 1.x
since it was declared end of life. The following table lists the
CVEs published about these issues.

| Severity | CVE | Summary |
|----------|-----|---------|
| High | [CVE-2019-17571](https://www.cve.org/CVERecord?id=CVE-2019-17571) | SocketServer is vulnerable to a remote code execution vulnerability when an attacker can craft malicious serialized log events and send them to a listening SocketServer instance. |
| Moderate | [CVE-2020-9488](https://www.cve.org/CVERecord?id=CVE-2020-9488) | SMTPAppender is vulnerable to a man-in-the-middle attack when using SMTPS due to lack of hostname verification in the TLS certificate. |
| High | [CVE-2021-4104](https://www.cve.org/CVERecord?id=CVE-2021-4104) | JMSAppender is vulnerable to a remote code execution vulnerability when an attacker controls either the configuration file or target LDAP server used for setting the TopicBindingName and TopicConnectionFactoryBindingName configurations. |
| High | [CVE-2022-23302](https://www.cve.org/CVERecord?id=CVE-2022-23302) | JMSSink is vulnerable to a remote code execution vulnerability when an attacker controls either the configuration file or target LDAP server used for setting the TopicConnectionFactoryBindingName configurations. |
| High | [CVE-2022-23305](https://www.cve.org/CVERecord?id=CVE-2022-23305) | JDBCAppender is vulnerable to a SQL injection vulnerability when an attacker can craft a malicious log message written to a JDBCAppender. |
| Critical | [CVE-2022-23307](https://www.cve.org/CVERecord?id=CVE-2022-23307) | Chainsaw versions bundled with Log4j prior to Chainsaw 2.1.0 are vulnerable to a remote code execution vulnerability when an attacker sends malicious serialized log events. See also [CVE-2020-9493](https://www.cve.org/CVERecord?id=CVE-2020-9493) for the CVE affecting the standalone version of Apache Chainsaw. |


Regards,<br />
Ron

The Apache Software Foundation<br />
V.P., Logging Services
