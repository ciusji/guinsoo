# Guinsoo Database Enginee

![logo](../public/architecture.svg)


## Overview 

Working from the top down, the layers look like this:

* JDBC driver.
* Connection/session management.
* SQL Parser.
* Command execution and planning.
* Table/Index/Constraints.
* Undo log, redo log, and transactions layer.
* B-tree/ART-tree engine and page-based storage allocation.
* Filesystem abstraction.
