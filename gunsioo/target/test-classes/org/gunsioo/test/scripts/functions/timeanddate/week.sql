-- Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
-- and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
-- Initial Developer: Gunsioo Group
--

-- ISO_WEEK

select iso_week('2006-12-31') w, iso_year('2007-12-31') y, iso_day_of_week('2007-12-31') w;
> W  Y    W
> -- ---- -
> 52 2008 1
> rows: 1
