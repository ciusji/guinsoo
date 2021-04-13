-- Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

CALL PARSEDATETIME('3. Februar 2001', 'd. MMMM yyyy', 'de');
>> 2001-02-03 00:00:00

CALL PARSEDATETIME('02/03/2001 04:05:06', 'MM/dd/yyyy HH:mm:ss');
>> 2001-02-03 04:05:06
