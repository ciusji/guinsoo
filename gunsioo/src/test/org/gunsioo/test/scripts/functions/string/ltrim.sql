-- Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

select ltrim(null) en, '>' || ltrim('a') || '<' ea, '>' || ltrim(' a ') || '<' e_as;
> EN   EA  E_AS
> ---- --- ----
> null >a< >a <
> rows: 1
