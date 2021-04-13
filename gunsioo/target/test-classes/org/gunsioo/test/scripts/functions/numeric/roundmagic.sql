-- Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

select roundmagic(null) en, roundmagic(cast(3.11 as double) - 3.1) e001, roundmagic(3.11-3.1-0.01) e000, roundmagic(2000000000000) e20x;
> EN   E001 E000 E20X
> ---- ---- ---- ------
> null 0.01 0.0  2.0E12
> rows: 1
