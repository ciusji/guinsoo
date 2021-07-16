-- Copyright 2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

select casewhen(null, '1', '2') xn, casewhen(1>0, 'n', 'y') xy, casewhen(0<1, 'a', 'b') xa;
> XN XY XA
> -- -- --
> 2  n  a
> rows: 1
