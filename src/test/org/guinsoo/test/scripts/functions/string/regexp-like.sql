-- Copyright 2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

call select 1 from dual where regexp_like('x', 'x', '\');
> exception INVALID_VALUE_2

CALL REGEXP_LIKE('A', '[a-z]', 'i');
>> TRUE

CALL REGEXP_LIKE('A', '[a-z]', 'c');
>> FALSE
