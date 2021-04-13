-- Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

select left(null, 10) en, left('abc', null) en2, left('boat', 2) e_bo, left('', 1) ee, left('a', -1) ee2;
> EN   EN2  E_BO EE EE2
> ---- ---- ---- -- ---
> null null bo
> rows: 1
