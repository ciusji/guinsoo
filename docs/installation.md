# Guinsoo Database Enginee

<br/>

## Installation

Environment list:

- [Java](https://mvnrepository.com/) 
- [Python](https://pypi.org/project/pip/)
- [Node.js](https://www.npmjs.com/)
- [CLI](https://github.com/ciusji/guinsoo)
- [Rust](https://crates.io/)
- [C/C++](https://github.com/ciusji/guinsoo)

Platform list:

- Windows
- Linux 
- MacOS

<br/>


## Examples

For java pom:

```java
<dependency>
    <groupId>org.guinsoo</groupId>
    <artifactId>guindoo</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

A simple usage:
```java
String url = "jdbc:gunsioo:mem:;UNDO_LOG=0;CACHE_SIZE=4096;STORE=3";
Connection conn = ConnectionBuilder
	.getInstance()
    .setUrl(url)
    .build();
Statement stat = conn.createStatement();
stat.execute("select 4;");
stat.close();
conn.close()
```

More examples is building.
