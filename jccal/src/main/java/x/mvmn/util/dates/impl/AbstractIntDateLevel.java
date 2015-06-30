package x.mvmn.util.dates.impl;

import x.mvmn.util.dates.DateComparable;

public abstract class AbstractIntDateLevel<P extends AbstractIntDateLevel<?, P>, T extends AbstractIntDateLevel<P, T>> implements DateComparable<T> {

	protected abstract DateComparable<P> getSupervalue();

	protected abstract int getInternalValueLimit();

	private int internalValue;

	protected int getValueInternal() {
		return internalValue;
	}

	protected void setValueInternal(int newInternalValue) {
		this.internalValue = newInternalValue;
	}

	protected void addValueInternal(int delta) {
		this.internalValue += delta;
		peformRangeCorrection(getInternalValueLimit());
	}

	protected void peformRangeCorrection(int limit) {
		if (getSupervalue() != null && limit > 0) {
			if (internalValue < 0) {
				int superlevelCorrection = (internalValue / limit) - 1;
				if (internalValue % limit != 0) {
					this.internalValue = (internalValue % limit) + limit;
				} else {
					this.internalValue = 0;
					superlevelCorrection++;
				}
				getSupervalue().getThis().addValueInternal(superlevelCorrection);
			} else if (internalValue >= getInternalValueLimit()) {
				int superlevelCorrection = (internalValue / limit);
				this.internalValue = (internalValue % limit);
				getSupervalue().getThis().addValueInternal(superlevelCorrection);
			}
		}
	}

	@Override
	public boolean isAfter(DateComparable<T> comparison) {
		if (comparison == null)
			return false;
		boolean result = false;
		if (getSupervalue() != null) {
			if (getSupervalue().isAfter(comparison.getThis().getSupervalue())) {
				result = true;
			} else if (getSupervalue().isSame(comparison.getThis().getSupervalue()) && this.internalValue > comparison.getThis().getValueInternal()) {
				result = true;
			}
		} else {
			result = this.internalValue > comparison.getThis().getValueInternal();
		}
		return result;
	}

	@Override
	public boolean isBefore(DateComparable<T> comparison) {
		if (comparison == null)
			return false;
		boolean result = false;
		if (getSupervalue() != null) {
			if (getSupervalue().isBefore(comparison.getThis().getSupervalue())) {
				result = true;
			} else if (getSupervalue().isSame(comparison.getThis().getSupervalue()) && this.internalValue < comparison.getThis().getValueInternal()) {
				result = true;
			}
		} else {
			result = this.internalValue < comparison.getThis().getValueInternal();
		}
		return result;
	}

	@Override
	public boolean isSame(DateComparable<T> comparison) {
		if (comparison == null)
			return true;
		boolean result = false;
		if (getSupervalue() != null) {
			if (getSupervalue().isSame(comparison.getThis().getSupervalue()) && this.internalValue == comparison.getThis().getValueInternal()) {
				result = true;
			}
		} else {
			result = this.internalValue == comparison.getThis().getValueInternal();
		}
		return result;
	}

	public int compareTo(DateComparable<T> comparison) {
		int delta = 0;
		if (comparison == null) {
			delta = getValueInternal();
		} else {
			delta = getValueInternal() - comparison.getThis().getValueInternal();
		}
		if (this.getSupervalue() != null) {
			if (comparison == null) {
				delta += this.getSupervalue().getThis().getValueInternal() * this.getInternalValueLimit();
			} else {
				delta += this.getSupervalue().compareTo(comparison.getThis().getSupervalue()) * this.getInternalValueLimit();
			}
		}
		return delta;
	}

	public boolean equals(DateComparable<T> comparison) {
		if (comparison == null)
			return false;
		return this.isSame(comparison);
	}
}
