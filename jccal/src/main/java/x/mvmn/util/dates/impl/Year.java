package x.mvmn.util.dates.impl;

import x.mvmn.util.dates.DateComparable;
import x.mvmn.util.dates.Yearable;

public class Year extends AbstractIntDateLevel<Year> implements Yearable, Cloneable {

	@Override
	protected DateComparable<AbstractIntDateLevel<?>> getSupervalue() {
		return null;
	}

	@Override
	protected int getInternalValueLimit() {
		return -1;
	}

	public Year() {
		setValueInternal(1980);
	}

	public Year(int year) {
		setValueInternal(year);
	}

	public void setYearValue(int year) {
		this.setValueInternal(year);
	}

	public void addYearValue(int delta) {
		this.addValueInternal(delta);
	}

	public int getYearValue() {
		return this.getValueInternal();
	}

	public Year getYear() {
		return this;
	}

	public boolean isLeap() {
		return isLeap(getValueInternal());
	}

	public static boolean isLeap(int year) {
		boolean result = false;
		if (year % 400 == 0) {
			result = true;
		} else if (year % 4 == 0 && year % 100 != 0) {
			result = true;
		}
		return result;
	}

	public String toString() {
		return String.format("%04d", this.getYearValue());
	}

	public Year clone() {
		return new Year(this.getYearValue());
	}
}
