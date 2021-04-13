-- Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

select convert(null, varchar(255)) xn, convert(' 10', int) x10, convert(' 20 ', int) x20;
> XN   X10 X20
> ---- --- ---
> null 10  20
> rows: 1
