-- Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

select concat(null, null) en, concat(null, 'a') ea, concat('b', null) eb, concat('ab', 'c') abc;
> EN EA EB ABC
> -- -- -- ---
>    a  b  abc
> rows: 1

SELECT CONCAT('a', 'b', 'c', 'd');
>> abcd
