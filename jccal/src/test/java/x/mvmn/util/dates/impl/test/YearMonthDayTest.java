package x.mvmn.util.dates.impl.test;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Test;

import x.mvmn.util.dates.impl.YearMonthDay;

public class YearMonthDayTest {

	private YearMonthDay ymd = new YearMonthDay();

	@Test
	public void test() {
		ymd = new YearMonthDay(1600, 0, 0);
		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.setMinimalDaysInFirstWeek(4);
		cal.set(ymd.getYearValue(), ymd.getMonthValue(), ymd.getDayValue() + 1, 12, 30, 0);
		int n0 = 0;
		int n1 = 1;
		Calendar calPrev = (Calendar) cal.clone();
		YearMonthDay ymdPrev = (YearMonthDay) ymd.clone();
		for (int i = 0; i < 28; i++) {
			int n = n0 + n1;
			n0 = n1;
			n1 = n;
			cal.add(Calendar.DAY_OF_YEAR, n);
			ymd.addDayValue(n);

			debug(n, cal, daysBetween(cal, calPrev), ymd, ymd.compareTo(ymdPrev));
			doCompare(cal, calPrev, ymd, ymdPrev);
			calPrev = (Calendar) cal.clone();
			ymdPrev = (YearMonthDay) ymd.clone();
		}

		n0 = 0;
		n1 = 1;
		for (int i = 0; i < 28; i++) {
			int n = n0 + n1;
			n0 = n1;
			n1 = n;
			cal.add(Calendar.DAY_OF_YEAR, -n);
			ymd.addDayValue(-n);

			debug(n, cal, daysBetween(cal, calPrev), ymd, ymd.compareTo(ymdPrev));
			doCompare(cal, calPrev, ymd, ymdPrev);
			calPrev = (Calendar) cal.clone();
			ymdPrev = (YearMonthDay) ymd.clone();

		}

		cal.set(2005, 0, 1);
		ymd.set(2005, 0, 0);
		debug(-1, cal, daysBetween(cal, calPrev), ymd, ymd.compareTo(ymdPrev));
		doCompare(cal, calPrev, ymd, ymdPrev);
		calPrev = (Calendar) cal.clone();
		ymdPrev = (YearMonthDay) ymd.clone();
		cal.add(Calendar.DAY_OF_YEAR, 1);
		ymd.addDayValue(1);
		debug(1, cal, daysBetween(cal, calPrev), ymd, ymd.compareTo(ymdPrev));
		doCompare(cal, calPrev, ymd, ymdPrev);
		calPrev = (Calendar) cal.clone();
		ymdPrev = (YearMonthDay) ymd.clone();

		cal.add(Calendar.DAY_OF_YEAR, 1);
		ymd.addDayValue(1);
		debug(1, cal, daysBetween(cal, calPrev), ymd, ymd.compareTo(ymdPrev));
		doCompare(cal, calPrev, ymd, ymdPrev);
		calPrev = (Calendar) cal.clone();
		ymdPrev = (YearMonthDay) ymd.clone();

		cal.set(1980, 0, 1);
		ymd.set(1980, 0, 0);
		n0 = 0;
		n1 = 1;
		for (int i = 0; i < 20; i++) {
			int n = n0 + n1;
			n0 = n1;
			n1 = n;
			cal.add(Calendar.MONTH, n);
			ymd.addMonthValue(n);

			debug(n, cal, daysBetween(cal, calPrev), ymd, ymd.compareTo(ymdPrev));
			doCompare(cal, calPrev, ymd, ymdPrev);
			calPrev = (Calendar) cal.clone();
			ymdPrev = (YearMonthDay) ymd.clone();
		}

		n0 = 0;
		n1 = 1;
		for (int i = 0; i < 20; i++) {
			int n = n0 + n1;
			n0 = n1;
			n1 = n;
			cal.add(Calendar.MONTH, -n);
			ymd.addMonthValue(-n);

			debug(n, cal, daysBetween(cal, calPrev), ymd, ymd.compareTo(ymdPrev));
			doCompare(cal, calPrev, ymd, ymdPrev);
			calPrev = (Calendar) cal.clone();
			ymdPrev = (YearMonthDay) ymd.clone();
		}

		cal.set(1980, 0, 1);
		ymd.set(1980, 0, 0);
		n0 = 0;
		n1 = 1;
		for (int i = 0; i < 20; i++) {
			int n = n0 + n1;
			n0 = n1;
			n1 = n;
			cal.add(Calendar.MONTH, n);
			ymd.getMonth().addMonthValue(n);

			debug(n, cal, daysBetween(cal, calPrev), ymd, ymd.compareTo(ymdPrev));
			doCompare(cal, calPrev, ymd, ymdPrev);
			calPrev = (Calendar) cal.clone();
			ymdPrev = (YearMonthDay) ymd.clone();
		}

		n0 = 0;
		n1 = 1;
		for (int i = 0; i < 20; i++) {
			int n = n0 + n1;
			n0 = n1;
			n1 = n;
			cal.add(Calendar.MONTH, -n);
			ymd.getMonth().addMonthValue(-n);

			debug(n, cal, daysBetween(cal, calPrev), ymd, ymd.compareTo(ymdPrev));
			doCompare(cal, calPrev, ymd, ymdPrev);
			calPrev = (Calendar) cal.clone();
			ymdPrev = (YearMonthDay) ymd.clone();
		}

		cal.setMinimalDaysInFirstWeek(4);
		cal.set(2008, 0, 30);
		ymd.set(2008, 0, 29);
		for (int i = 0; i < 365 * 6; i++) {
			cal.add(Calendar.DAY_OF_YEAR, 1);
			ymd.addDayValue(1);

			debug(1, cal, daysBetween(cal, calPrev), ymd, ymd.compareTo(ymdPrev));
			doCompare(cal, calPrev, ymd, ymdPrev);
			calPrev = (Calendar) cal.clone();
			ymdPrev = (YearMonthDay) ymd.clone();
		}
		for (int i = 0; i < 365 * 6; i++) {
			cal.add(Calendar.DAY_OF_YEAR, -1);
			ymd.addDayValue(-1);

			debug(1, cal, daysBetween(cal, calPrev), ymd, ymd.compareTo(ymdPrev));
			doCompare(cal, calPrev, ymd, ymdPrev);
			calPrev = (Calendar) cal.clone();
			ymdPrev = (YearMonthDay) ymd.clone();
		}
	}

	private void doCompare(Calendar cal, Calendar calPrev, YearMonthDay ymd, YearMonthDay ymdPrev) {
		assertEquals("Comparing year", cal.get(Calendar.YEAR), ymd.getYearValue());
		assertEquals("Comparing month", cal.get(Calendar.MONTH), ymd.getMonthValue());
		assertEquals("Comparing day of month", cal.get(Calendar.DAY_OF_MONTH), ymd.getDayValue() + 1);
		assertEquals("Comparng day of week", (cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY + 7) % 7, ymd.getDayOfWeek());
		assertEquals("Comparing week of year", cal.get(Calendar.WEEK_OF_YEAR), ymd.getWeekNumber());
		assertEquals("Comparing delta calc", daysBetween(cal, calPrev), ymd.compareTo(ymdPrev));
		assertEquals("Comparing day of year", cal.get(Calendar.DAY_OF_YEAR), ymd.getDayOfYear() + 1);
	}

	private void debug(int n, Calendar cal, int calDelta, YearMonthDay ymd, int ymdDelta) {

		String dowName[] = { "MO", "TU", "WE", "TH", "FR", "SA", "SU" };

		System.out.format("Adding %10d: %04d-%02d-%02d (%s, week %02d, DoY %03d) [D%8d]   ==   %04d-%02d-%02d (%s, week %02d, DoY %03d) [D%8d]\n", n, cal.get(Calendar.YEAR),
				(cal.get(Calendar.MONTH) + 1), cal.get(Calendar.DAY_OF_MONTH), dowName[(cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY + 7) % 7], cal.get(Calendar.WEEK_OF_YEAR),
				cal.get(Calendar.DAY_OF_YEAR), calDelta, ymd.getYearValue(), ymd.getMonthValue() + 1, ymd.getDayValue() + 1, dowName[ymd.getDayOfWeek()], ymd.getWeekNumber(),
				ymd.getDayOfYear() + 1, ymdDelta);
	}

	public static int daysBetween(final Calendar endDate, final Calendar startDate) {

		// http://tripoverit.blogspot.com/2007/07/java-calculate-difference-between-two.html
		int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
		long endInstant = endDate.getTimeInMillis();
		int presumedDays = (int) ((endInstant - startDate.getTimeInMillis()) / MILLIS_IN_DAY);
		Calendar cursor = (Calendar) startDate.clone();
		cursor.add(Calendar.DAY_OF_YEAR, presumedDays);
		long instant = cursor.getTimeInMillis();
		if (instant == endInstant)
			return presumedDays;
		final int step = instant < endInstant ? 1 : -1;
		do {
			cursor.add(Calendar.DAY_OF_MONTH, step);
			presumedDays += step;
		} while (cursor.getTimeInMillis() != endInstant);
		return presumedDays;
	}

}
