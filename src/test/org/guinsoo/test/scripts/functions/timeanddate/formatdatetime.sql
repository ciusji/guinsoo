-- Copyright 2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

CALL FORMATDATETIME(PARSEDATETIME('2001-02-03 04:05:06 GMT', 'yyyy-MM-dd HH:mm:ss z', 'en', 'GMT'), 'EEE, d MMM yyyy HH:mm:ss z', 'en', 'GMT');
>> Sat, 3 Feb 2001 04:05:06 GMT

CALL FORMATDATETIME(TIMESTAMP '2001-02-03 04:05:06', 'yyyy-MM-dd HH:mm:ss');
>> 2001-02-03 04:05:06

CALL FORMATDATETIME(TIMESTAMP '2001-02-03 04:05:06', 'MM/dd/yyyy HH:mm:ss');
>> 02/03/2001 04:05:06

CALL FORMATDATETIME(TIMESTAMP '2001-02-03 04:05:06', 'd. MMMM yyyy', 'de');
>> 3. Februar 2001

CALL FORMATDATETIME(PARSEDATETIME('Sat, 3 Feb 2001 04:05:06 GMT', 'EEE, d MMM yyyy HH:mm:ss z', 'en', 'GMT'), 'yyyy-MM-dd HH:mm:ss', 'en', 'GMT');
>> 2001-02-03 04:05:06

SELECT FORMATDATETIME(TIMESTAMP WITH TIME ZONE '2010-05-06 07:08:09.123Z', 'yyyy-MM-dd HH:mm:ss.SSS z');
>> 2010-05-06 07:08:09.123 UTC

SELECT FORMATDATETIME(TIMESTAMP WITH TIME ZONE '2010-05-06 07:08:09.123+13:30', 'yyyy-MM-dd HH:mm:ss.SSS z');
>> 2010-05-06 07:08:09.123 GMT+13:30
