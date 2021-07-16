-- Copyright 2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

select abs(-1) r1, abs(1) r1b;
> R1 R1B
> -- ---
> 1  1
> rows: 1

select abs(null) vn, abs(-1) r1, abs(1) r2, abs(0) r3, abs(-0.1) r4, abs(0.1) r5;
> VN   R1 R2 R3 R4  R5
> ---- -- -- -- --- ---
> null 1  1  0  0.1 0.1
> rows: 1

select * from table(id int=(1, 2), name varchar=('Hello', 'World')) x order by id;
> ID NAME
> -- -----
> 1  Hello
> 2  World
> rows (ordered): 2
