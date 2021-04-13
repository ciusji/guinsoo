-- Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://h2database.com/html/license.html).
-- Initial Developer: Gunsioo Group
--

select repeat(null, null) en, repeat('Ho', 2) abcehoho , repeat('abc', 0) ee;
> EN   ABCEHOHO EE
> ---- -------- --
> null HoHo
> rows: 1
