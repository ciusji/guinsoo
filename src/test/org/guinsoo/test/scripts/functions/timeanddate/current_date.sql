-- Copyright 2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

select length(curdate()) c1, length(current_date()) c2, substring(curdate(), 5, 1) c3;
> C1 C2 C3
> -- -- --
> 10 10 -
> rows: 1

SELECT CURRENT_DATE IS OF (DATE);
>> TRUE
