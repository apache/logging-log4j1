# Work In Progress

Not ready for use.

# End Of Life

On August 5, 2015 the Logging Services Project Management Committee announced that Log4j 1.x had reached end of life. For complete text of the announcement please see the [Apache Blog](https://blogs.apache.org/foundation/entry/apache_logging_services_project_announces). Users of Log4j 1 are recommended to upgrade to [Apache Log4j 2](https://logging.apache.org/log4j/2.x/index.html).

# Security release 1.2.18

On December 10, 2021 the Logging Services Project Management Committee announced the release of Log4j 2.15 to fix a critical security vulnerability, followed by Log4j 2.16 on December 13 with further fixes for this vulnerability, with details on the [Log4j Security Page](https://logging.apache.org/log4j/2.x/security.html). All log4j users should follow this security advice.

For remaining users of log4j 1.2 and older, the recommended upgrade path remains to migrate to [Apache Log4j 2](https://logging.apache.org/log4j/2.x/index.html). Log4j 1.2 does not suffer from the same security vulnerabilities in the same way, but users should still upgrade: Log4j 1.2 does have an older known vulnerability [CVE-2019-17571](https://www.cvedetails.com/cve/CVE-2019-17571/) and per the above end-of-life notice is UNMAINTAINED software since 2015. It is possible Log4j 1.2 has several unknown vulnerabilities.

For users that cannot upgrade to Log4j 2.x, a somewhat-secured version of Log4j 1.2 is being made as Log4j 1.2.18. This is a new release of otherwise UNMAINTAINED software. While 1.2.18 will fix a critical security vulnerability and has some improvements to the library that should help with security, it remains End Of Life and users should make plans to upgrade to 2.x.

## Changes in 1.2.18

See the [Changes Report](https://logging.apache.org/log4j/1.2/changes-report.html) for a detailed list of changes. This file is generated from [changes.xml](src/changes/changes.xml).
