<div align="right">
    <img src="https://raw.githubusercontent.com/ciusji/guinsoo/master/public/guinsoolab-badge.png" width="60" alt="badge">
    <br />
</div>
<div align="center">
    <img src="https://raw.githubusercontent.com/ciusji/guinsoo/master/public/guinsoo.jpg" width=120 alt="logo" />
    <br />
    <small>Powered by <a href="https://guinsoolab.github.io/glab">GuinsooLab</a></small>
</div>

# [Guinsoo](https://ciusji.gitbook.io/guinsoo/)

[![maven](https://img.shields.io/maven-central/v/io.github.ciusji/guinsoo)](https://search.maven.org/search?q=guinsoo)
[![size](https://img.shields.io/github/repo-size/ciusji/guinsoo)](https://github.com/ciusji/guinsoo)
[![version](https://img.shields.io/github/v/tag/ciusji/guinsoo)](https://github.com/ciusji/guinsoo)
[![cov](https://img.shields.io/codecov/c/github/ciusji/guinsoo)](https://github.com/ciusji/guinsoo)
[![downloads](https://img.shields.io/github/downloads/ciusji/guinsoo/total)](https://github.com/ciusji/guinsoo)

`A metadata store database for GuinsooLab stack.`

For more information please visit [here](https://ciusji.gitbook.io/guinsoolab/).


## Features

* Super-fast, open source, JDBC API
* In-memory, non-blocking store, designed for low-latency applications
* Embedded and server modes; disk-based or in-memory databases
* Transaction support, multi-version concurrency
* Fulltext search
* Encrypted databases

More information, please refer to [here](https://ciusji.gitbook.io/guinsoo/).


## Overview

Working from the top down, the layers look like this:

* JDBC driver.
* Connection/session management.
* SQL Parser.
* Command execution and planning.
* Table/Index/Constraints.
* Transactions layer.
* B-tree/ART.
* Filesystem abstraction.


## Quickstart

Step 1: Add maven dependency (click [here](https://search.maven.org/artifact/io.github.ciusji/guinsoo) to find more versions ð)
```java
<dependency>
    <groupId>io.github.ciusji</groupId>
    <artifactId>guinsoo</artifactId>
    <version>0.2.2</version>
</dependency>
```

Step 2: Connect and execute SQL

```java
Class.forName("org.guinsoo.Driver");
Connection conn = DriverManager.getConnection("jdbc:guinsoo:mem:");
Statement stat = conn.createStatement();

stat.execute("YOUR SQL");

stat.close();
conn.close();
```

For more language, such as Python, Java, C++, Rust, Node or others, please click [here](https://ciusji.gitbook.io/guinsoo/guides/tutorial).


## Documentation

### Guides

- [Introduction](https://ciusji.gitbook.io/guinsoo/guides/introduction)
- [Quickstart](https://ciusji.gitbook.io/guinsoo/guides/quickstart)
- [Features](https://ciusji.gitbook.io/guinsoo/guides/features)
- [Installation](https://ciusji.gitbook.io/guinsoo/guides/installation)
- [Tutorial](https://ciusji.gitbook.io/guinsoo/guides/tutorial)
- [Security](https://ciusji.gitbook.io/guinsoo/guides/security)
- [Performance](https://ciusji.gitbook.io/guinsoo/guides/performance)
- [Advanced](https://ciusji.gitbook.io/guinsoo/guides/advanced)

### Reference

- [Commands](https://ciusji.gitbook.io/guinsoo/reference/commands)
- [Functions](https://ciusji.gitbook.io/guinsoo/reference/functions)
- [Aggregate](https://ciusji.gitbook.io/guinsoo/reference/aggregate)
- [Window](https://ciusji.gitbook.io/guinsoo/reference/window)
- [Data Types](https://ciusji.gitbook.io/guinsoo/reference/data-types)
- [SQL Grammar](https://ciusji.gitbook.io/guinsoo/reference/sql-grammar)
- [System Table](https://ciusji.gitbook.io/guinsoo/reference/system-table)

### Support

- [FAQ](https://ciusji.gitbook.io/guinsoo/support/faq)

### Appendix

- [License](https://ciusji.gitbook.io/guinsoo/appendix/license)
- [Links](https://ciusji.gitbook.io/guinsoo/appendix/links)
- [Architecture](https://ciusji.gitbook.io/guinsoo/appendix/architecture)


## Issues

[Issue tracker](https://github.com/ciusji/guinsoo/issues) for bug reports and feature requests.


## Others

- [GuinsooLab, a perfect calculation-container](https://guinsoolab.github.io/glab/)
- [Spotrix, explore insights for everyone](https://spotrix.github.io/spotrix-web/)

