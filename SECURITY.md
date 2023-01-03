# Security Policy

## Supported Versions

Since Log4j 1.2 reached end of life in 2015 (cf.
[announcement](http://blogs.apache.org/foundation/entry/apache_logging_services_project_announces)) **no** version of
Log4j 1.2 is currently supported. Users are encouraged to migrate to [Apache
Log4j2](https://logging.apache.org/log4j/2.x/manual/migration.html).

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

See also [Apache Log4j 1.2 Security Vulnerabilities](https://logging.apache.org/log4j/1.2/).

