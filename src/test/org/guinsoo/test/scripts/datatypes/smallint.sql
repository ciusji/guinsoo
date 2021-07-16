-- Copyright 2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

-- Division

SELECT CAST(1 AS SMALLINT) / CAST(0 AS SMALLINT);
> exception DIVISION_BY_ZERO_1

SELECT CAST(-32768 AS SMALLINT) / CAST(1 AS SMALLINT);
>> -32768

SELECT CAST(-32768 AS SMALLINT) / CAST(-1 AS SMALLINT);
> exception NUMERIC_VALUE_OUT_OF_RANGE_1

EXPLAIN VALUES CAST(1 AS SMALLINT);
>> VALUES (CAST(1 AS SMALLINT))

EXPLAIN VALUES CAST(1 AS YEAR);
> exception UNKNOWN_DATA_TYPE_1

SET MODE MySQL;
> ok

EXPLAIN VALUES CAST(1 AS YEAR);
>> VALUES (CAST(1 AS SMALLINT))

SET MODE Regular;
> ok
