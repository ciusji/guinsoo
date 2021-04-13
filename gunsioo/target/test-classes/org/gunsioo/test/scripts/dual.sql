-- Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

SELECT * FROM DUAL;
>
>
>
> rows: 1

CREATE TABLE DUAL(A INT);
> ok

INSERT INTO DUAL VALUES (2);
> update count: 1

SELECT A FROM DUAL;
>> 2

SELECT * FROM SYS.DUAL;
>
>
>
> rows: 1

DROP TABLE DUAL;
> ok

SET MODE DB2;
> ok

SELECT * FROM SYSDUMMY1;
>
>
>
> rows: 1

CREATE TABLE SYSDUMMY1(A INT);
> ok

INSERT INTO SYSDUMMY1 VALUES (2);
> update count: 1

SELECT A FROM SYSDUMMY1;
>> 2

SELECT * FROM SYSIBM.SYSDUMMY1;
>
>
>
> rows: 1

DROP TABLE SYSDUMMY1;
> ok

SET MODE Regular;
> ok
