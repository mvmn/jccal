package x.mvmn.util.dates.impl;

import x.mvmn.util.dates.DateComparable;
import x.mvmn.util.dates.Dayable;
import x.mvmn.util.dates.Monthable;
import x.mvmn.util.dates.Yearable;

public class YearMonthDay extends AbstractIntDateLevel<YearMonthDay> implements Yearable, Monthable, Dayable, Cloneable {

	private class DayCorrectingYearMonth extends YearMonth {

		public DayCorrectingYearMonth() {
			super(1980, 0);
			setValueInternal(0);
		}

		public void setMonthValue(int month) {
			setValueInternal(month);
			YearMonthDay.this.correctAfterMonthSet();
		}

		public void addMonthValue(int delta) {
			addValueInternal(delta);
			YearMonthDay.this.correctAfterMonthAdd();
		}
	};

	private YearMonth yearMonth = new DayCorrectingYearMonth();

	protected void correctAfterMonthAdd() {
		if (getInternalValueLimit() < getValueInternal()) {
			int delta = getValueInternal() - getInternalValueLimit();
			setValueInternal(getInternalValueLimit());
			addDayValue(delta);
		}
	}

	protected void correctAfterMonthSet() {
		if (getInternalValueLimit() < getValueInternal()) {
			// No transitioning to next month:
			// if we have 31-st of Dec and set month = Feb
			// we want to end up 28/29-th of Feb, not 2/3 of Mar
			// cause we just set month to Feb, and getting back Mar
			// is unexpected.
			setValueInternal(getInternalValueLimit());
		}
	}

	@Override
	protected AbstractIntDateLevel<?> getSupervalue() {
		return yearMonth;
	}

	@Override
	protected int getInternalValueLimit() {
		return yearMonth.getNumberOfDaysInCurrentMonth();
	}

	@Override
	protected void addValueInternal(int delta) {
		delta = getValueInternal() + delta;
		if (delta >= 0) {
			while (delta > (yearMonth.getNumberOfDaysInCurrentMonth() - 1)) {
				delta -= yearMonth.getNumberOfDaysInCurrentMonth();
				yearMonth.addValueInternal(1);
			}
			setValueInternal(delta);
		} else if (delta < 0) {
			while (delta < 0) {
				yearMonth.addValueInternal(-1);
				delta += yearMonth.getNumberOfDaysInCurrentMonth();
			}
			setValueInternal(delta);
		}
	}

	public YearMonthDay() {
		setValueInternal(0);
	}

	public YearMonthDay(int year, int month, int day) {
		set(year, month, day);
	}

	public void set(int year, int month, int day) {
		setYearValue(year);
		setMonthValue(month);
		setDayValue(day);
	}

	@Override
	public void setDayValue(int day) {
		setValueInternal(day);
	}

	@Override
	public void addDayValue(int delta) {
		addValueInternal(delta);
	}

	@Override
	public int getDayValue() {
		return getValueInternal();
	}

	@Override
	public YearMonthDay getDay() {
		return this;
	}

	@Override
	public void setMonthValue(int month) {
		yearMonth.setMonthValue(month);
	}

	@Override
	public void addMonthValue(int delta) {
		yearMonth.addMonthValue(delta);
	}

	@Override
	public int getMonthValue() {
		return yearMonth.getMonthValue();
	}

	@Override
	public YearMonth getMonth() {
		return yearMonth.getMonth();
	}

	@Override
	public void setYearValue(int year) {
		yearMonth.setYearValue(year);
	}

	@Override
	public void addYearValue(int delta) {
		yearMonth.addYearValue(delta);
	}

	@Override
	public int getYearValue() {
		return yearMonth.getYearValue();
	}

	@Override
	public Year getYear() {
		return yearMonth.getYear();
	}

	private static final int[] DAYS_IN_MONTHS = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

	/**
	 * @return zero-based day of year (0 to 364 or 365 for leap years)
	 */
	public int getDayOfYear() {
		int dayOfYear = 0;
		for (int i = 0; i < getMonthValue(); i++) {
			dayOfYear += DAYS_IN_MONTHS[i];
		}
		if (getMonthValue() > 1 && getYear().isLeap()) {
			dayOfYear++;
		}

		dayOfYear += getDayValue();

		return dayOfYear;
	}

	public int getDayOfWeek() {
		int m = getMonthValue() + 1;
		int y = getYearValue();
		int q = getDayValue() + 1;
		if (m < 3) {
			m += 12;
			y -= 1;
		}

		int k = y % 100;
		int j = y / 100;

		int day = ((q + (((m + 1) * 26) / 10) + k + (k / 4) + (j / 4)) + (5 * j)) % 7;
		return (day + 5) % 7;
	}

	public int getWeekNumber() {
		int dayOfYear = getDayOfYear();
		// Day of year number for week's frist day
		int mondaysDayOfYear = (dayOfYear + 1) - getDayOfWeek();
		
		int thisWeeksDaysInThisYear = 365 + (getYear().isLeap() ? 1 : 0) - mondaysDayOfYear;
		if (thisWeeksDaysInThisYear < 3) {
			// This is already week 1 of next year
			return 1;
		}
		if (mondaysDayOfYear < -2) {
			int prevYearLastWeekNum = 52;
			if (mondaysDayOfYear == -3 || (mondaysDayOfYear == -4 && Year.isLeap(getYearValue() - 1))) {
				// all years ending with Thursday,
				// and leap years ending with Friday
				prevYearLastWeekNum = 53;
			}
			return prevYearLastWeekNum;
		}
		if (mondaysDayOfYear <= 0)
			return 1;
		// Number of full weeks passed this year
		int fullWeeks = mondaysDayOfYear / 7;
		// Reminder that passed before fist mon
		int reminderWeek = mondaysDayOfYear % 7;
		// Reminder counts for a week if it has Thursday (ISO Week num)
		return 1 + fullWeeks + (reminderWeek > 4 ? 1 : 0);
	}

	public int compareTo(DateComparable<AbstractIntDateLevel<YearMonthDay>> comparison) {
		int delta = 0;
		if (comparison == null) {
			delta = getValueInternal();
		} else {
			delta = getValueInternal() - comparison.getThis().getValueInternal();
		}
		YearMonthDay ymdComparison = (YearMonthDay) comparison.getThis();

		// We already have days in delta, but now we need to count them for
		// months and years
		YearMonthDay iter = new YearMonthDay(ymdComparison.getYearValue(), ymdComparison.getMonthValue(), 0);
		YearMonthDay ref = new YearMonthDay(this.getYearValue(), this.getMonthValue(), 0);
		if (!iter.isSame(ref)) {
			boolean after = iter.isAfter(ref);

			while (!iter.isSame(ref)) {
				if (after) {
					iter.addMonthValue(-1);
					delta -= iter.getMonth().getNumberOfDaysInCurrentMonth();
				} else {
					delta += iter.getMonth().getNumberOfDaysInCurrentMonth();
					iter.addMonthValue(1);
				}
			}
		}

		return delta;
	}

	public YearMonthDay clone() {
		return new YearMonthDay(getYearValue(), getMonthValue(), getDayValue());
	}

	public String toString() {
		return String.format("%04d-%02d-%02d", this.getYearValue(), this.getMonthValue() + 1, this.getDayValue() + 1);
	}

}
