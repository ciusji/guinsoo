-- Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

select power(null, null) en, power(2, 3) e8, power(16, 0.5) e4;
> EN   E8  E4
> ---- --- ---
> null 8.0 4.0
> rows: 1

SELECT POWER(10, 2) IS OF (DOUBLE);
>> TRUE
