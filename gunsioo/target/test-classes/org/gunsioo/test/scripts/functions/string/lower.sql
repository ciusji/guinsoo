-- Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

select lower(null) en, lower('Hello') hello, lower('ABC') abc;
> EN   HELLO ABC
> ---- ----- ---
> null hello abc
> rows: 1

select lcase(null) en, lcase('Hello') hello, lcase('ABC') abc;
> EN   HELLO ABC
> ---- ----- ---
> null hello abc
> rows: 1
