-- Copyright 2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

@reconnect off

select rand(1) e, random() f;
> E                  F
> ------------------ -------------------
> 0.7308781907032909 0.41008081149220166
> rows: 1

select rand();
>> 0.20771484130971707
