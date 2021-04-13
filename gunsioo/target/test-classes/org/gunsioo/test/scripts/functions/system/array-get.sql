-- Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

CREATE TABLE TEST(A INTEGER ARRAY) AS VALUES ARRAY[NULL], ARRAY[1];
> ok

SELECT A, ARRAY_GET(A, 1), ARRAY_GET(A, 1) IS OF (INTEGER) FROM TEST;
> A      A[1] A[1] IS OF (INTEGER)
> ------ ---- --------------------
> [1]    1    TRUE
> [null] null null
> rows: 2

DROP TABLE TEST;
> ok
