-- Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

select ascii(null) en, ascii('') en, ascii('Abc') e65;
> EN   EN   E65
> ---- ---- ---
> null null 65
> rows: 1
