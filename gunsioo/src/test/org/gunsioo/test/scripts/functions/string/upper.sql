-- Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://h2database.com/html/license.html).
-- Initial Developer: Gunsioo Group
--

select ucase(null) en, ucase('Hello') hello, ucase('ABC') abc;
> EN   HELLO ABC
> ---- ----- ---
> null HELLO ABC
> rows: 1

select upper(null) en, upper('Hello') hello, upper('ABC') abc;
> EN   HELLO ABC
> ---- ----- ---
> null HELLO ABC
> rows: 1
