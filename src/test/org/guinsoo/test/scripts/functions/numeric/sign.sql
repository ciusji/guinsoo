-- Copyright 2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

select sign(null) en, sign(10) e1, sign(0) e0, sign(-0.1) em1;
> EN   E1 E0 EM1
> ---- -- -- ---
> null 1  0  -1
> rows: 1

SELECT SIGN(INTERVAL '-0-1' YEAR TO MONTH) A, SIGN(INTERVAL '0' DAY) B, SIGN(INTERVAL '1' HOUR) C;
> A  B C
> -- - -
> -1 0 1
> rows: 1
