-- Copyright 2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

select array_cat(ARRAY[1, 2], ARRAY[3, 4]) = ARRAY[1, 2, 3, 4];
>> TRUE

select array_cat(ARRAY[1, 2], null) is null;
>> TRUE

select array_cat(null, ARRAY[1, 2]) is null;
>> TRUE

select array_append(ARRAY[1, 2], 3) = ARRAY[1, 2, 3];
>> TRUE

select array_append(ARRAY[1, 2], null) is null;
>> TRUE

select array_append(null, 3) is null;
>> TRUE
