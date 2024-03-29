-- Copyright 2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

SELECT ROUND(-1.2), ROUND(-1.5), ROUND(-1.6), ROUND(2), ROUND(1.5), ROUND(1.8), ROUND(1.1);
> -1 -2 -2 2 2 2 1
> -- -- -- - - - -
> -1 -2 -2 2 2 2 1
> rows: 1

select round(null, null) en, round(10.49, 0) e10, round(10.05, 1) e101;
> EN   E10 E101
> ---- --- ----
> null 10  10.1
> rows: 1

select round(null) en, round(0.6, null) en2, round(1.05) e1, round(-1.51) em2;
> EN   EN2  E1 EM2
> ---- ---- -- ---
> null null 1  -2
> rows: 1

CALL ROUND(998.5::DOUBLE);
>> 999.0

CALL ROUND(998.5::REAL);
>> 999.0

SELECT
    ROUND(4503599627370495.0::DOUBLE), ROUND(4503599627370495.5::DOUBLE),
    ROUND(4503599627370496.0::DOUBLE), ROUND(4503599627370497.0::DOUBLE);
> 4.503599627370495E15 4.503599627370496E15 4.503599627370496E15 4.503599627370497E15
> -------------------- -------------------- -------------------- --------------------
> 4.503599627370495E15 4.503599627370496E15 4.503599627370496E15 4.503599627370497E15
> rows: 1

SELECT
    ROUND(450359962737049.50::DOUBLE, 1), ROUND(450359962737049.55::DOUBLE, 1),
    ROUND(450359962737049.60::DOUBLE, 1), ROUND(450359962737049.70::DOUBLE, 1);
> 4.503599627370495E14 4.503599627370496E14 4.503599627370496E14 4.503599627370497E14
> -------------------- -------------------- -------------------- --------------------
> 4.503599627370495E14 4.503599627370496E14 4.503599627370496E14 4.503599627370497E14
> rows: 1

CALL ROUND(0.285, 2);
>> 0.29

CALL ROUND(0.285::DOUBLE, 2);
>> 0.29

CALL ROUND(0.285::REAL, 2);
>> 0.29

CALL ROUND(1.285, 2);
>> 1.29

CALL ROUND(1.285::DOUBLE, 2);
>> 1.29

CALL ROUND(1.285::REAL, 2);
>> 1.29

CALL ROUND(1, 1) IS OF (NUMERIC);
>> TRUE

CALL ROUND(1::DOUBLE, 1) IS OF (DOUBLE);
>> TRUE

CALL ROUND(1::REAL, 1) IS OF (REAL);
>> TRUE

SELECT ROUND(1, 10000000);
> exception INVALID_VALUE_2
