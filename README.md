# Guinsoo

![logo](public/guinsoo-app.svg)

`Guinsoo, not only a database.`

Powered by [Guinsoo Lab](https://guinsoolab.github.io/glab/).

<br/>

## Feature:

* Super-fast, open source, JDBC API
* In-memory, non-blocking store, designed for low-latency applications
* Embedded and server modes; disk-based or in-memory databases
* Transaction support, multi-version concurrency
* Browser based Console application
* Encrypted databases
* Fulltext search

More information: https://ciusji.github.io/guinsoo/

<br>

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

<br>

## Support

* [Issue tracker](https://github.com/ciusji/guinsoo/issues) for bug reports and feature requests