-- Copyright 2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

SELECT RAWTOHEX('A');
>> 0041

SELECT RAWTOHEX('Az');
>> 0041007a

SET MODE Oracle;
> ok

SELECT RAWTOHEX('A');
>> 41

SELECT RAWTOHEX('Az');
>> 417a

SET MODE Regular;
> ok

SELECT RAWTOHEX(X'12fe');
>> 12fe

SELECT RAWTOHEX('12345678-9abc-def0-0123-456789abcdef'::UUID);
>> 123456789abcdef00123456789abcdef
