-- Copyright 2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

select select decode(null, null, 'a');
>> a

select select decode(1, 1, 'a');
>> a

select select decode(1, 2, 'a');
>> null

select select decode(1, 1, 'a', 'else');
>> a

select select decode(1, 2, 'a', 'else');
>> else

select decode(4.0, 2.0, 2.0, 3.0, 3.0);
>> null

select decode('3', 2.0, 2.0, 3, 3.0);
>> 3.0

select decode(4.0, 2.0, 2.0, 3.0, 3.0, 4.0, 4.0, 9.0);
>> 4.0

select decode(1, 1, '1', 1, '11') from dual;
>> 1
