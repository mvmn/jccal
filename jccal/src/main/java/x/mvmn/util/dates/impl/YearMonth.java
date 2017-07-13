package x.mvmn.util.dates.impl;

import x.mvmn.util.dates.DateComparable;
import x.mvmn.util.dates.Monthable;
import x.mvmn.util.dates.Yearable;

public class YearMonth extends AbstractIntDateLevel<Year, YearMonth> implements Yearable, Monthable, Cloneable {

	private Year year = new Year();

	@Override
	public YearMonth getThis() {
		return this;
	}

	@Override
	protected DateComparable<Year> getSupervalue() {
		return year;
	}

	@Override
	protected int getInternalValueLimit() {
		return 12;
	}

	public YearMonth() {
		setValueInternal(0);
	}

	public YearMonth(int year, int month) {
		set(year, month);
	}

	public Year getYear() {
		return year;
	}

	public YearMonth set(int year, int month) {
		setYearValue(year);
		setValueInternal(month);
		return this;
	}

	@Override
	public YearMonth setMonthValue(int month) {
		this.setValueInternal(month);
		return this;
	}

	@Override
	public YearMonth addMonthValue(int delta) {
		this.addValueInternal(delta);
		return this;
	}

	@Override
	public int getMonthValue() {
		return getValueInternal();
	}

	@Override
	public YearMonth getMonth() {
		return this;
	}

	@Override
	public YearMonth setYearValue(int year) {
		this.year.setValueInternal(year);
		return this;
	}

	@Override
	public YearMonth addYearValue(int delta) {
		this.year.addValueInternal(delta);
		return this;
	}

	@Override
	public int getYearValue() {
		return year.getValueInternal();
	}

	private static final int[] DAYS_IN_MONTHS = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

	public static int getNumberOfDaysInMonth(int year, int month) {
		int result = DAYS_IN_MONTHS[month];
		if (month == 1 && Year.isLeap(year)) { // February
			result = 29;
		}
		return result;

	}

	public int getNumberOfDaysInCurrentMonth() {
		int monthNum = getValueInternal();
		int result = DAYS_IN_MONTHS[monthNum];
		if (monthNum == 1 && year.isLeap()) { // February
			result = 29;
		}
		return result;
	}

	public String toString() {
		return String.format("%04d-%02d", this.getYearValue(), this.getMonthValue() + 1);
	}

	public YearMonth clone() {
		return new YearMonth(this.getYearValue(), this.getMonthValue());
	}
}
