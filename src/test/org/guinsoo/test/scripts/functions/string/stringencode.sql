-- Copyright 2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

SELECT STRINGENCODE(STRINGDECODE('abcsond\344rzeich\344 ') || char(22222) || STRINGDECODE(' \366\344\374\326\304\334\351\350\340\361!'));
>> abcsond\u00e4rzeich\u00e4 \u56ce \u00f6\u00e4\u00fc\u00d6\u00c4\u00dc\u00e9\u00e8\u00e0\u00f1!

call STRINGENCODE(STRINGDECODE('abcsond\344rzeich\344 \u56ce \366\344\374\326\304\334\351\350\340\361!'));
>> abcsond\u00e4rzeich\u00e4 \u56ce \u00f6\u00e4\u00fc\u00d6\u00c4\u00dc\u00e9\u00e8\u00e0\u00f1!

CALL STRINGENCODE(STRINGDECODE('Lines 1\nLine 2'));
>> Lines 1\nLine 2
