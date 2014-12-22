package x.mvmn.util.jccal.model;

import x.mvmn.util.dates.impl.YearMonth;
import x.mvmn.util.dates.impl.YearMonthDay;

public class MonthGrid {

	private final int[][] datesGrid = new int[6][];
	private final int year;
	private final int month;
	private final int[] weekNumbers = new int[6];
	private final int actualWeeksCount;

	public MonthGrid(int year, int month) {
		this.year = year;
		this.month = month;
		for (int i = 0; i < 6; i++) {
			datesGrid[i] = new int[7];
			for (int j = 0; j < 7; j++) {
				datesGrid[i][j] = -1;
			}
			weekNumbers[i] = -1;
		}

		YearMonth ym = new YearMonth(year, month);
		YearMonthDay ymd = new YearMonthDay(year, month, 0);
		int weekIdx = 0;
		int currentWeekNumber = ymd.getWeekNumber();
		weekNumbers[weekIdx] = currentWeekNumber;
		while (ymd.getMonth().isSame(ym)) {
			int newCurrentWeekNumber = ymd.getWeekNumber();
			if (newCurrentWeekNumber != currentWeekNumber) {
				weekIdx++;
				currentWeekNumber = newCurrentWeekNumber;
				weekNumbers[weekIdx] = currentWeekNumber;
			}
			datesGrid[weekIdx][ymd.getDayOfWeek()] = ymd.getDayValue();
			ymd.addDayValue(1);
		}
		actualWeeksCount = weekIdx;
	}

	public int getValueAt(int row, int dayOfWeek) {
		return datesGrid[row][dayOfWeek];
	}

	public int getYear() {
		return this.year;
	}

	public int getMonth() {
		return this.month;
	}

	public int getActualWeeksCount() {
		return this.actualWeeksCount;
	}

	public int getWeekNumber(int idx) {
		return this.weekNumbers[idx];
	}
}
