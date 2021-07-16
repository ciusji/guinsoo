-- Copyright 2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

select replace(null, null) en, replace(null, null, null) en1;
> EN   EN1
> ---- ----
> null null
> rows: 1

select replace('abchihihi', 'i', 'o') abcehohoho, replace('that is tom', 'i') abcethstom;
> ABCEHOHOHO ABCETHSTOM
> ---------- ----------
> abchohoho  that s tom
> rows: 1

set mode oracle;
> ok

select replace('white space', ' ', '') x, replace('white space', ' ', null) y from dual;
> X          Y
> ---------- ----------
> whitespace whitespace
> rows: 1
