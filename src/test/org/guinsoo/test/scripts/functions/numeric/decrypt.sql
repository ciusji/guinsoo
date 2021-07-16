-- Copyright 2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

call utf8tostring(decrypt('AES', X'00000000000000000000000000000000', X'dbd42d55d4b923c4b03eba0396fac98e'));
>> Hello World Test

call utf8tostring(decrypt('AES', hash('sha256', stringtoutf8('Hello'), 1000), encrypt('AES', hash('sha256', stringtoutf8('Hello'), 1000), stringtoutf8('Hello World Test'))));
>> Hello World Test
