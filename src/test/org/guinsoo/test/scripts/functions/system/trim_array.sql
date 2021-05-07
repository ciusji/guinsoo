-- Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

SELECT TRIM_ARRAY(ARRAY[1, 2], -1);
> exception ARRAY_ELEMENT_ERROR_2

SELECT TRIM_ARRAY(ARRAY[1, 2], 0);
>> [1, 2]

SELECT TRIM_ARRAY(ARRAY[1, 2], 1);
>> [1]

SELECT TRIM_ARRAY(ARRAY[1, 2], 2);
>> []

SELECT TRIM_ARRAY(ARRAY[1, 2], 3);
> exception ARRAY_ELEMENT_ERROR_2

SELECT TRIM_ARRAY(NULL, 1);
>> null

SELECT TRIM_ARRAY(NULL, -1);
> exception ARRAY_ELEMENT_ERROR_2

SELECT TRIM_ARRAY(ARRAY[1], NULL);
>> null
