# Guinsoo

![logo](./public/gunsioo.svg)

Guinsoo, not only a database.


## Feature:

* Super-fast, open source, JDBC API
* In-memory, non-blocking store, designed for low-latency applications
* Embedded and server modes; disk-based or in-memory databases
* Transaction support, multi-version concurrency
* Browser based Console application
* Encrypted databases
* Fulltext search

More information: https://github.com/ciusji/guinsoo


## Overview
Working from the top down, the layers look like this:

* JDBC driver.
* Connection/session management.
* SQL Parser.
* Command execution and planning.
* Table/Index/Constraints.
* Undo log, redo log, and transactions layer.
* B-tree engine and page-based storage allocation.
* Filesystem abstraction.


## Support

* [Issue tracker](https://github.com/ciusji/guinsoo/issues) for bug reports and feature requests