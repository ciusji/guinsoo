-- Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

@reconnect off

CREATE SCHEMA TEST_SCHEMA;
> ok

DROP SCHEMA TEST_SCHEMA RESTRICT;
> ok

CREATE SCHEMA TEST_SCHEMA;
> ok

CREATE TABLE TEST_SCHEMA.TEST();
> ok

DROP SCHEMA TEST_SCHEMA RESTRICT;
> exception CANNOT_DROP_2

DROP SCHEMA TEST_SCHEMA CASCADE;
> ok

CREATE SCHEMA TEST_SCHEMA;
> ok

CREATE VIEW TEST_SCHEMA.TEST AS SELECT 1;
> ok

DROP SCHEMA TEST_SCHEMA RESTRICT;
> exception CANNOT_DROP_2

DROP SCHEMA TEST_SCHEMA CASCADE;
> ok

CREATE TABLE PUBLIC.SRC();
> ok

CREATE SCHEMA TEST_SCHEMA;
> ok

CREATE SYNONYM TEST_SCHEMA.TEST FOR PUBLIC.SRC;
> ok

DROP SCHEMA TEST_SCHEMA RESTRICT;
> exception CANNOT_DROP_2

DROP SCHEMA TEST_SCHEMA CASCADE;
> ok

DROP TABLE PUBLIC.SRC;
> ok

CREATE SCHEMA TEST_SCHEMA;
> ok

CREATE SEQUENCE TEST_SCHEMA.TEST;
> ok

DROP SCHEMA TEST_SCHEMA RESTRICT;
> exception CANNOT_DROP_2

DROP SCHEMA TEST_SCHEMA CASCADE;
> ok

CREATE SCHEMA TEST_SCHEMA;
> ok

CREATE CONSTANT TEST_SCHEMA.TEST VALUE 1;
> ok

DROP SCHEMA TEST_SCHEMA RESTRICT;
> exception CANNOT_DROP_2

DROP SCHEMA TEST_SCHEMA CASCADE;
> ok

CREATE SCHEMA TEST_SCHEMA;
> ok

CREATE ALIAS TEST_SCHEMA.TEST FOR "java.lang.System.currentTimeMillis";
> ok

DROP SCHEMA TEST_SCHEMA RESTRICT;
> exception CANNOT_DROP_2

DROP SCHEMA TEST_SCHEMA CASCADE;
> ok

-- Test computed column dependency

CREATE TABLE A (A INT);
> ok

CREATE TABLE B (B INT AS SELECT A FROM A);
> ok

DROP ALL OBJECTS;
> ok

CREATE SCHEMA TEST_SCHEMA;
> ok

CREATE TABLE TEST_SCHEMA.A (A INT);
> ok

CREATE TABLE TEST_SCHEMA.B (B INT AS SELECT A FROM TEST_SCHEMA.A);
> ok

DROP SCHEMA TEST_SCHEMA CASCADE;
> ok

CREATE SCHEMA A;
> ok

CREATE TABLE A.A1(ID INT);
> ok

CREATE SCHEMA B;
> ok

CREATE TABLE B.B1(ID INT, X INT DEFAULT (SELECT MAX(ID) FROM A.A1));
> ok

DROP SCHEMA A CASCADE;
> exception CANNOT_DROP_2

DROP SCHEMA B CASCADE;
> ok

DROP SCHEMA A CASCADE;
> ok

CREATE SCHEMA A;
> ok

CREATE TABLE A.A1(ID INT, X INT);
> ok

CREATE TABLE A.A2(ID INT, X INT DEFAULT (SELECT MAX(ID) FROM A.A1));
> ok

ALTER TABLE A.A1 ALTER COLUMN X SET DEFAULT (SELECT MAX(ID) FROM A.A2);
> ok

DROP SCHEMA A CASCADE;
> ok
