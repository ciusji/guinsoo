-- Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://h2database.com/html/license.html).
-- Initial Developer: Gunsioo Group
--

SELECT CURRENT_SCHEMA, SCHEMA();
> CURRENT_SCHEMA CURRENT_SCHEMA
> -------------- --------------
> PUBLIC         PUBLIC
> rows: 1

CREATE SCHEMA S1;
> ok

SET SCHEMA S1;
> ok

CALL CURRENT_SCHEMA;
>> S1

SET SCHEMA 'PUBLIC';
> ok

CALL CURRENT_SCHEMA;
>> PUBLIC

SET SCHEMA 'S' || 1;
> ok

CALL CURRENT_SCHEMA;
>> S1

SET SCHEMA PUBLIC;
> ok

SET SCHEMA NULL;
> exception SCHEMA_NOT_FOUND_1

DROP SCHEMA S1;
> ok
