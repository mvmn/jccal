package x.mvmn.util.dates.impl;

import x.mvmn.util.dates.Monthable;
import x.mvmn.util.dates.Yearable;

public class YearMonth extends AbstractIntDateLevel<YearMonth> implements Yearable, Monthable, Cloneable {

	private Year year = new Year();

	@Override
	protected AbstractIntDateLevel<?> getSupervalue() {
		return year.getThis();
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

	public void set(int year, int month) {
		setYearValue(year);
		setValueInternal(month);
	}

	@Override
	public void setMonthValue(int month) {
		this.setValueInternal(month);
	}

	@Override
	public void addMonthValue(int delta) {
		this.addValueInternal(delta);
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
	public void setYearValue(int year) {
		this.year.setValueInternal(year);
	}

	@Override
	public void addYearValue(int delta) {
		this.year.addValueInternal(delta);
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
