-- Copyright 2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

-- with filter condition

create table test(v int);
> ok

insert into test values (10), (20), (30), (40), (50), (60), (70), (80), (90), (100), (110), (120);
> update count: 12

select avg(v), avg(v) filter (where v >= 40) from test where v <= 100;
> AVG(V) AVG(V) FILTER (WHERE V >= 40)
> ------ -----------------------------
> 55     70
> rows: 1

create index test_idx on test(v);
> ok

select avg(v), avg(v) filter (where v >= 40) from test where v <= 100;
> AVG(V) AVG(V) FILTER (WHERE V >= 40)
> ------ -----------------------------
> 55     70
> rows: 1

drop table test;
> ok
