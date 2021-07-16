-- Copyright 2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

CALL XMLTEXT('test');
>> test

CALL XMLTEXT('<test>');
>> &lt;test&gt;

SELECT XMLTEXT('hello' || chr(10) || 'world');
>> hello world

CALL XMLTEXT('hello' || chr(10) || 'world', true);
>> hello&#xa;world
