-- Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

CALL XMLNODE('a', XMLATTR('href', 'https://h2database.com'));
>> <a href="https://h2database.com"/>

CALL XMLNODE('br');
>> <br/>

CALL XMLNODE('p', null, 'Hello World');
>> <p>Hello World</p>

SELECT XMLNODE('p', null, 'Hello' || chr(10) || 'World');
>> <p> Hello World </p>

SELECT XMLNODE('p', null, 'Hello' || chr(10) || 'World', false);
>> <p>Hello World</p>
