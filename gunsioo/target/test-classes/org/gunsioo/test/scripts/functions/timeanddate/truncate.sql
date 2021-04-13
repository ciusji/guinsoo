-- Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

select trunc('2015-05-29 15:00:00');
>> 2015-05-29 00:00:00

select trunc('2015-05-29');
>> 2015-05-29 00:00:00

select trunc(timestamp '2000-01-01 10:20:30.0');
>> 2000-01-01 00:00:00

select trunc(timestamp '2001-01-01 14:00:00.0');
>> 2001-01-01 00:00:00
