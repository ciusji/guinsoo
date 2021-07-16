-- Copyright 2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

help abc;
> ID SECTION TOPIC SYNTAX TEXT
> -- ------- ----- ------ ----
> rows: 0

HELP ABCDE EF_GH;
> ID SECTION TOPIC SYNTAX TEXT
> -- ------- ----- ------ ----
> rows: 0

HELP HELP;
> ID SECTION          TOPIC SYNTAX                  TEXT
> -- ---------------- ----- ----------------------- ----------------------------------------------------
> 72 Commands (Other) HELP  HELP [ anything [...] ] Displays the help pages of SQL commands or keywords.
> rows: 1

HELP he lp;
> ID SECTION          TOPIC SYNTAX                  TEXT
> -- ---------------- ----- ----------------------- ----------------------------------------------------
> 72 Commands (Other) HELP  HELP [ anything [...] ] Displays the help pages of SQL commands or keywords.
> rows: 1
