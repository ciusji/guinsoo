-- Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

select mod(null, 1) vn, mod(1, null) vn1, mod(null, null) vn2, mod(10, 2) e1;
> VN   VN1  VN2  E1
> ---- ---- ---- --
> null null null 0
> rows: 1
