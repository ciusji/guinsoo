-- Copyright 2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

select right(null, 10) en, right('abc', null) en2, right('boat-trip', 2) e_ip, right('', 1) ee, right('a', -1) ee2;
> EN   EN2  E_IP EE EE2
> ---- ---- ---- -- ---
> null null ip
> rows: 1
