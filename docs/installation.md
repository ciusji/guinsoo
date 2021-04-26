# Guinsoo Database Enginee

<br/>

## Installation

The **GuinsooDB** Java JDBC API can be installed from [mvn repository](https://mvnrepository.com/).

```java
<dependency>
    <groupId>org.guinsoo</groupId>
    <artifactId>guindoo</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

<br/>

### Basic API Usage

**GuinsooDB**'s JDBC API implements the main part of the standard Java Database Connectivity API. Below is usage parts.

To create a **GuinsooDB** connection, use the JDBC URL like so:
```java
String url = "jdbc:gunsioo:mem:;STORE=3";
Connection conn = ConnectionBuilder.getInstance()
    .setUrl(url)
    .build();
```

Database Engine:

- STORE=1: page_store
- STORE=2: mv_store
- STORE=3: quick_store

<br/>

### Querying

**GuinsooDB** supports the standard JDBC methods. First a statement object has to be created from the simple method of 
`ConnectionBuilder`. Below is a example. See also the 
[JDBC guides](https://docs.oracle.com/javase/8/docs/technotes/guides/jdbc/).

```java
// create a table
Statement stmt = conn.createStatement();
stmt.execute("create table student(id integer, name varchar)");
// insert items
stmt.execute("insert into student values (1, 'guinsoo')");
```

```java
// query items
ResultSet rs = stmt.executeQuery("select * from student");
// print
while (rs.next()) {
    System.out.println(rs.getInt(1));
    System.out.println(rs.getString(2));
}
```
<br/>

```markdown
Note

Do not use statements to insert a large number of data into **GuinsooDB**. See the function of `read_csv` may be the 
better options.
```
