[English](./README.md) | [中文](./README-CN.md)

<br/> 

# ![logo](public/guinsoo-app.svg)

# Guinsoo

[![maven](https://img.shields.io/maven-central/v/io.github.ciusji/guinsoo)](https://search.maven.org/search?q=guinsoo)
[![size](https://img.shields.io/github/repo-size/ciusji/guinsoo)](https://github.com/ciusji/guinsoo)
[![version](https://img.shields.io/github/v/tag/ciusji/guinsoo)](https://github.com/ciusji/guinsoo)
[![cov](https://img.shields.io/codecov/c/github/ciusji/guinsoo)](https://github.com/ciusji/guinsoo)
[![downloads](https://img.shields.io/github/downloads/ciusji/guinsoo/total)](https://github.com/ciusji/guinsoo)

`Guinsoo, not only a database.`

Powered by [Guinsoo Lab](https://guinsoolab.github.io/glab/).

<br/>

## Feature

* Super-fast, open source, JDBC API
* In-memory, non-blocking store, designed for low-latency applications
* Embedded and server modes; disk-based or in-memory databases
* Transaction support, multi-version concurrency
* Encrypted databases

More information: https://ciusji.github.io/guinsoo/

<br>

## Overview

Working from the top down, the layers look like this:

* [GuinsooPad](https://guinsoolab.github.io/guinsoopad/).
* JDBC driver.
* Connection/session management.
* SQL Parser.
* Command execution and planning.
* Table/Index/Constraints.
* Transactions layer.
* B-tree/ART.
* Filesystem abstraction.
* More Plugins powered by [GLab](https://guinsoolab.github.io/glab/).

<br>

## Installation

### Apache Maven
```java
<dependency>
    <groupId>io.github.ciusji</groupId>
    <artifactId>guinsoo</artifactId>
    <version>0.2.1</version>
</dependency>
```

### Gradle Groovy DSL
```java
implementation 'io.github.ciusji:guinsoo:0.2.1'
```

### Gradle Kotlin DSL 
```java
implementation("io.github.ciusji:guinsoo:0.2.1")
```

### Scala SBT
```java
libraryDependencies += "io.github.ciusji" % "guinsoo" % "0.2.1"
```

### Apache Lvy
```java
<dependency org="io.github.ciusji" name="guinsoo" rev="0.2.1" />
```

### Groovy Grape
```java
@Grapes(
  @Grab(group='io.github.ciusji', module='guinsoo', version='0.2.1')
)
```

### Apache Buildr
```java
'io.github.ciusji:guinsoo:jar:0.2.1'
```

For more version information, please click [here](https://search.maven.org/artifact/io.github.ciusji/guinsoo). 

<br>

## Documentation

[Guinsoo Documentation](https://ciusji.github.io/guinsoo/).

<br>

## Support

[Issue tracker](https://github.com/ciusji/guinsoo/issues) for bug reports and feature requests.

<br>

## Appendix

* [GuinsooLab, a perfect calculation-container](https://guinsoolab.github.io/glab/)
* [Spotrix, explore insights for everyone](https://spotrix.github.io/spotrix-web/)



