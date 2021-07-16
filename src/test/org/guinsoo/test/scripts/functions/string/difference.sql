-- Copyright 2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

select difference(null, null) en, difference('a', null) en1, difference(null, 'a') en2;
> EN   EN1  EN2
> ---- ---- ----
> null null null
> rows: 1

select difference('abc', 'abc') e0, difference('Thomas', 'Tom') e1;
> E0 E1
> -- --
> 4  3
> rows: 1
