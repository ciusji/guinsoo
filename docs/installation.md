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
    <artifactId>guindoo_driver</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

A simple usage:
```java
Class.forName("org.guinsoo.GuinsooDbDriver");
Connection conn = DriverManager.getConnection("jdbc:guinsoo:");
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery("SELECT 42");
```

More examples is building.
