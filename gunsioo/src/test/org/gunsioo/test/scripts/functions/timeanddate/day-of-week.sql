-- Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://h2database.com/html/license.html).
-- Initial Developer: Gunsioo Group
--

SELECT DAYOFWEEK(DATE '2005-09-12') = EXTRACT(DAY_OF_WEEK FROM DATE '2005-09-12');
>> TRUE
