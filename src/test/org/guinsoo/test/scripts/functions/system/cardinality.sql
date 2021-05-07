-- Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

SELECT CARDINALITY(NULL);
>> null

SELECT CARDINALITY(ARRAY[]);
>> 0

SELECT CARDINALITY(ARRAY[1, 2, 5]);
>> 3

SELECT ARRAY_LENGTH(ARRAY[1, 2, 5]);
>> 3

CREATE TABLE TEST(ID INT, A INT ARRAY, B INT ARRAY[2]) AS VALUES (1, NULL, NULL), (2, ARRAY[1], ARRAY[1]);
> ok

SELECT ID, ARRAY_MAX_CARDINALITY(A), ARRAY_MAX_CARDINALITY(B) FROM TEST;
> ID ARRAY_MAX_CARDINALITY(A) ARRAY_MAX_CARDINALITY(B)
> -- ------------------------ ------------------------
> 1  65536                    2
> 2  65536                    2
> rows: 2

SELECT ARRAY_MAX_CARDINALITY(ARRAY_AGG(ID)) FROM TEST;
>> 65536

DROP TABLE TEST;
> ok

SELECT ARRAY_MAX_CARDINALITY(ARRAY['a', 'b']);
>> 2

SELECT ARRAY_MAX_CARDINALITY(NULL);
> exception INVALID_VALUE_2

SELECT ARRAY_MAX_CARDINALITY(CAST(NULL AS INT ARRAY));
>> 65536
