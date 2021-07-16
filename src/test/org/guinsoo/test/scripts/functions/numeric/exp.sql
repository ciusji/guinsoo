-- Copyright 2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

select exp(null) vn, left(exp(1), 4) v1, left(exp(1.1), 4) v2, left(exp(-1.1), 4) v3, left(exp(1.9), 4) v4, left(exp(-1.9), 4) v5;
> VN   V1   V2   V3   V4   V5
> ---- ---- ---- ---- ---- ----
> null 2.71 3.00 0.33 6.68 0.14
> rows: 1
