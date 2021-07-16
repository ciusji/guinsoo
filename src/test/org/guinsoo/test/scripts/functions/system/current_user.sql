-- Copyright 2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

select user() x_sa, current_user() x_sa2;
> X_SA X_SA2
> ---- -----
> SA   SA
> rows: 1

SELECT CURRENT_USER;
>> SA

SELECT SESSION_USER;
>> SA

SELECT SYSTEM_USER;
>> SA

SELECT CURRENT_ROLE;
>> PUBLIC

EXPLAIN SELECT CURRENT_USER, SESSION_USER, SYSTEM_USER, USER, CURRENT_ROLE;
>> SELECT CURRENT_USER, SESSION_USER, SYSTEM_USER, CURRENT_USER, CURRENT_ROLE
