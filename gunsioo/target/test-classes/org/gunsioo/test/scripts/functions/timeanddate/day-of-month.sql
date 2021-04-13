-- Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

select dayofmonth(date '2005-09-12');
>> 12

create table test(ts timestamp with time zone);
> ok

insert into test(ts) values ('2010-05-11 00:00:00+10:00'), ('2010-05-11 00:00:00-10:00');
> update count: 2

select dayofmonth(ts) d from test;
> D
> --
> 11
> 11
> rows: 2

drop table test;
> ok
