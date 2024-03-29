-- Copyright 2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

select rtrim(null) en, '>' || rtrim('a') || '<' ea, '>' || rtrim(' a ') || '<' es;
> EN   EA  ES
> ---- --- ----
> null >a< > a<
> rows: 1

select rtrim() from dual;
> exception SYNTAX_ERROR_2
