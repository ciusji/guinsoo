# Guinsoo Database Enginee

`Guinsoo's Scythe of Vyse, is just guinsoo (羊刀) of the database name.`

<br/>

## Why Guinsoo

There are many database management systems out there. But **Guinsoo** is only for one-size-fits all
database system.

#### Simple Operation

**Guinsoo** adopts h2database/SQLite ideas of simplicity and embedded operation.

**Guinsoo** has few dependencies, there is no DBMS server to install, update and maintain. **Guinsoo** 
does not run as a separate process, but completely embedded within a host process. 

#### Fast Analytical Queries

**Guinsoo** is designed to support analytical query workloads (aka OLAP). 

To efficiently support this workload, it is critical to reduce the amount of CPU cycles that are expended 
per individual value. The state of the art in data management ot achieve this are either [vectorized of 
just-in-time query execution engines](https://www.vldb.org/pvldb/vol11/p2209-kersten.pdf). **Guinsoo** contains 
a columnar-vectorized query execution engine, where the queries are still interpreted, but a large batch
of values are processed in one operation. This greatly reduces overhead present in traditional tuple-of-a-time 
processing model.

By the way, the OLTP feature is provided by [h2database](https://www.h2database.com/html/main.html).

#### Open Source License

**Guinsoo** is open source, which is released under the 
[MPL 2.0 & EPL 1.0](https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).

<br/>

## Documentation

- [home](https://github.com/ciusji/guinsoo/blob/master/docs/home.md)
- [installation](https://github.com/ciusji/guinsoo/blob/master/docs/installation.md)
- [quickstart](https://github.com/ciusji/guinsoo/blob/master/docs/quickstart.md)
- [feature](https://github.com/ciusji/guinsoo/blob/master/docs/features.md)
- [performance](https://github.com/ciusji/guinsoo/blob/master/docs/performance.md)
- [advanced](https://github.com/ciusji/guinsoo/blob/master/docs/advanced.md)
- [architecture](https://github.com/ciusji/guinsoo/blob/master/docs/architecture.md)

<br/>

## Contributing

This project and everyone can participate in it. 

For more, please start [here](https://github.com/ciusji/guinsoo).