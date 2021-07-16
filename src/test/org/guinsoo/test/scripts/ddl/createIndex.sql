-- Copyright 2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

CREATE TABLE TEST(G GEOMETRY);
> ok

CREATE UNIQUE SPATIAL INDEX IDX ON TEST(G);
> exception SYNTAX_ERROR_2

CREATE HASH SPATIAL INDEX IDX ON TEST(G);
> exception SYNTAX_ERROR_2

CREATE UNIQUE HASH SPATIAL INDEX IDX ON TEST(G);
> exception SYNTAX_ERROR_2

DROP TABLE TEST;
> ok
