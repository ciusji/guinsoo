-- Copyright 2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

CALL 1 /* comment */ ;;
>> 1

CALL 1 /* comment */ ;
>> 1

call /* remark * / * /* ** // end */*/ 1;
>> 1

call /*/*/ */*/ 1;
>> 1

call /*1/*1*/1*/1;
>> 1

--- remarks/comments/syntax ----------------------------------------------------------------------------------------------
CREATE TABLE TEST(
ID INT PRIMARY KEY, -- this is the primary key, type {integer}
NAME VARCHAR(255) -- this is a string
);
> ok

INSERT INTO TEST VALUES(
1 /* ID */,
'Hello' // NAME
);
> update count: 1

SELECT * FROM TEST;
> ID NAME
> -- -----
> 1  Hello
> rows: 1

DROP_ TABLE_ TEST_T;
> exception SYNTAX_ERROR_2

DROP TABLE TEST /*;
> exception SYNTAX_ERROR_1

call /* remark * / * /* ** // end */ 1;
> exception SYNTAX_ERROR_1

DROP TABLE TEST;
> ok
