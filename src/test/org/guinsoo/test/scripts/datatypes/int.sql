-- Copyright 2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

-- Division

SELECT CAST(1 AS INT) / CAST(0 AS INT);
> exception DIVISION_BY_ZERO_1

SELECT CAST(-2147483648 AS INT) / CAST(1 AS INT);
>> -2147483648

SELECT CAST(-2147483648 AS INT) / CAST(-1 AS INT);
> exception NUMERIC_VALUE_OUT_OF_RANGE_1

EXPLAIN VALUES 1;
>> VALUES (1)
