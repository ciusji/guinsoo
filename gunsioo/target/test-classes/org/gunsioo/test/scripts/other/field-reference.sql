-- Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

SELECT (R).A, (R).B FROM (VALUES CAST((1, 2) AS ROW(A INT, B INT))) T(R);
> (R).A (R).B
> ----- -----
> 1     2
> rows: 1

SELECT (R).C FROM (VALUES CAST((1, 2) AS ROW(A INT, B INT))) T(R);
> exception COLUMN_NOT_FOUND_1

SELECT (R).C1, (R).C2 FROM (VALUES ((1, 2))) T(R);
> (R).C1 (R).C2
> ------ ------
> 1      2
> rows: 1

SELECT (1, 2).C2;
>> 2

SELECT (1, 2).C0;
> exception COLUMN_NOT_FOUND_1

SELECT (1, 2).C;
> exception COLUMN_NOT_FOUND_1

SELECT (1, 2).CX;
> exception COLUMN_NOT_FOUND_1
