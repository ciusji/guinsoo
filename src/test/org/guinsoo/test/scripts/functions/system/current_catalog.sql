-- Copyright 2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

CALL CURRENT_CATALOG;
>> SCRIPT

CALL DATABASE();
>> SCRIPT

SET CATALOG SCRIPT;
> ok

SET CATALOG 'SCRIPT';
> ok

SET CATALOG 'SCR' || 'IPT';
> ok

SET CATALOG UNKNOWN_CATALOG;
> exception DATABASE_NOT_FOUND_1

SET CATALOG NULL;
> exception DATABASE_NOT_FOUND_1

CALL CURRENT_DATABASE();
> exception FUNCTION_NOT_FOUND_1

SET MODE PostgreSQL;
> ok

CALL CURRENT_DATABASE();
>> SCRIPT

SET MODE Regular;
> ok
