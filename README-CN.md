[English](./README.md) | [中文](./README-CN.md)

<br/>

# ![logo](public/guinsoo-app.svg)

# Guinsoo

[![maven](https://img.shields.io/maven-central/v/io.github.ciusji/guinsoo)](https://github.com/ciusji/guinsoo)
[![size](https://img.shields.io/github/repo-size/ciusji/guinsoo)](https://github.com/ciusji/guinsoo)
[![version](https://img.shields.io/github/v/tag/ciusji/guinsoo)](https://github.com/ciusji/guinsoo)
[![cov](https://img.shields.io/codecov/c/github/ciusji/guinsoo)](https://github.com/ciusji/guinsoo)
[![downloads](https://img.shields.io/github/downloads/ciusji/guinsoo/total)](https://github.com/ciusji/guinsoo)

`Guinsoo，不只是一个数据库。`

由 [Guinsoo Lab](https://guinsoolab.github.io/glab/) 支持。

<br/>

## 特点

* 开源、免费
* 支持 JDBC，使用简单
* 多引擎支持，内置 In-memory 引擎
* 嵌入式开发，易于维护
* 支持事务，支持 MVCC
* 数据文件加密

更多信息可关注 https://ciusji.github.io/guinsoo/。

<br>

## 概览

Guinsoo 层次架构，自上而下依次为：

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

## 安装

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

更多版本信息，可点击 [here](https://search.maven.org/artifact/io.github.ciusji/guinsoo) 查看。 

<br>

## 文档

[Guinsoo 使用文档](https://ciusji.github.io/guinsoo/)。

<br>

## 支持

[Issue tracker](https://github.com/ciusji/guinsoo/issues)，如有 BUG，欢迎提交 Issue。

<br>

## 附录

* [GuinsooLab — 高性能嵌入式计算容器](https://guinsoolab.github.io/glab/)
* [Spotrix — 桌面版自主见解洞察](https://spotrix.github.io/spotrix-web/)



