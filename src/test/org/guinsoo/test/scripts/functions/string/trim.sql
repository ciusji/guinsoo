-- Copyright 2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

CREATE TABLE TEST(ID INT PRIMARY KEY, A VARCHAR, B VARCHAR, C VARCHAR) AS VALUES (1, '__A__', '    B    ', 'xAx');
> ok

SELECT TRIM(BOTH '_' FROM A), '|' || TRIM(LEADING FROM B) || '|', TRIM(TRAILING 'x' FROM C) FROM TEST;
> TRIM('_' FROM A) '|' || TRIM(LEADING FROM B) || '|' TRIM(TRAILING 'x' FROM C)
> ---------------- ---------------------------------- -------------------------
> A                |B |                               xA
> rows: 1

SELECT LENGTH(TRIM(B)), LENGTH(TRIM(FROM B)) FROM TEST;
> CHAR_LENGTH(TRIM(B)) CHAR_LENGTH(TRIM(B))
> -------------------- --------------------
> 1                    1
> rows: 1

SELECT TRIM(BOTH B) FROM TEST;
> exception SYNTAX_ERROR_2

DROP TABLE TEST;
> ok

select TRIM(' ' FROM '  abc   ') from dual;
> 'abc'
> -----
> abc
> rows: 1
