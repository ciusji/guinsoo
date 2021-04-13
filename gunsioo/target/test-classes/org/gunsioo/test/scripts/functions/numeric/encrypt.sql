-- Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

call encrypt('AES', X'00000000000000000000000000000000', stringtoutf8('Hello World Test'));
>> X'dbd42d55d4b923c4b03eba0396fac98e'

CALL ENCRYPT('XTEA', X'00', STRINGTOUTF8('Test'));
>> X'8bc9a4601b3062692a72a5941072425f'

call encrypt('XTEA', X'000102030405060708090a0b0c0d0e0f', X'4142434445464748');
>> X'dea0b0b40966b0669fbae58ab503765f'
