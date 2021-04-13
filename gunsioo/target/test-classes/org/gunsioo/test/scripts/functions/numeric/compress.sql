-- Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

CALL COMPRESS(X'000000000000000000000000');
>> X'010c010000c000010000'

CALL COMPRESS(X'000000000000000000000000', 'NO');
>> X'000c000000000000000000000000'

CALL COMPRESS(X'000000000000000000000000', 'LZF');
>> X'010c010000c000010000'

CALL COMPRESS(X'000000000000000000000000', 'DEFLATE');
>> X'020c789c6360400000000c0001'

CALL COMPRESS(X'000000000000000000000000', 'UNKNOWN');
> exception UNSUPPORTED_COMPRESSION_ALGORITHM_1

CALL COMPRESS(NULL);
>> null

CALL COMPRESS(X'00', NULL);
>> null
