package x.mvmn.util.jccal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import x.mvmn.util.cli.CommandLineHelper;
import x.mvmn.util.dates.impl.YearMonth;
import x.mvmn.util.dates.impl.YearMonthDay;
import x.mvmn.util.jccal.model.MonthGrid;

public class JCCal {

	private static String[] WEEK_DAYS_NAMES = { "Mo", "Tu", "We", "Th", "Fr", "Sa", "Su" };
	private static String[] MONTHS_TITLES = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November",
			"December" };
	private static String EMPTY_CELL = "  ";

	public static void main(String[] args) {
		Map<String, String> cliParams = CommandLineHelper.getArgs(args);
		if (cliParams.get("help") != null) {
			printHelpMessage();
			return;
		}

		Calendar now = Calendar.getInstance();
		int startMonth = CommandLineHelper.asInt(cliParams.get("sm"), now.get(Calendar.MONTH) + 1) - 1;
		int endMonth = CommandLineHelper.asInt(cliParams.get("em"), now.get(Calendar.MONTH) + 1) - 1;
		int startYear = CommandLineHelper.asInt(cliParams.get("sy"), now.get(Calendar.YEAR));
		int endYear = CommandLineHelper.asInt(cliParams.get("ey"), now.get(Calendar.YEAR));

		YearMonth ymStart = new YearMonth(startYear, startMonth);
		YearMonth ymEnd = new YearMonth(endYear, endMonth);

		ymStart.addYearValue(CommandLineHelper.relativeInt(cliParams.get("sy")));
		ymEnd.addYearValue(CommandLineHelper.relativeInt(cliParams.get("ey")));

		ymStart.addMonthValue(CommandLineHelper.relativeInt(cliParams.get("sm")));
		ymEnd.addMonthValue(CommandLineHelper.relativeInt(cliParams.get("em")));

		if (ymEnd.isBefore(ymStart)) {
			return;
		}

		int yearViewColumns = CommandLineHelper.asInt(cliParams.get("cols"), 1);
		boolean flipYearView = cliParams.get("fy") != null;
		boolean flipMonthView = cliParams.get("fm") != null;
		String dayNumberFormat = "%-2d";
		if (cliParams.get("lzd") != null) {
			dayNumberFormat = "%02d";
		}
		String weekNumberFormat = "%-2d";
		if (cliParams.get("lzw") != null) {
			weekNumberFormat = "%02d";
		}

		String weekDaysNamesCapitalization = cliParams.get("wdn");
		if (weekDaysNamesCapitalization != null && weekDaysNamesCapitalization.trim().length() == 1) {
			weekDaysNamesCapitalization = weekDaysNamesCapitalization.trim().toLowerCase();
		} else {
			weekDaysNamesCapitalization = null;
		}

		String titleFormat = cliParams.get("tlny") != null ? "%2$s" : "%04d %s";

		String daysVerticalSeparator = "|";
		if (cliParams.get("dvs") != null) {
			daysVerticalSeparator = cliParams.get("dvs");
		}
		String monthsVerticalSeparator = "  ";
		if (cliParams.get("mvs") != null) {
			monthsVerticalSeparator = cliParams.get("mvs");
		}

		String daysHorizontalSeparator = null;
		if (cliParams.get("dhs") != null && !cliParams.get("dhs").isEmpty()) {
			daysHorizontalSeparator = cliParams.get("dhs");
		}
		String monthsHorizontalSeparator = null;
		if (cliParams.get("mhs") != null && !cliParams.get("mhs").isEmpty()) {
			monthsHorizontalSeparator = cliParams.get("mhs");
		}

		boolean hlOnly = cliParams.get("hlonly") != null;
		if (hlOnly || cliParams.get("nosep") != null) {
			daysVerticalSeparator = daysVerticalSeparator.replaceAll(".", " ");
			monthsVerticalSeparator = monthsVerticalSeparator.replaceAll(".", " ");
			if (daysHorizontalSeparator != null) {
				daysHorizontalSeparator = daysHorizontalSeparator.replaceAll(".", " ");
			}
			if (monthsHorizontalSeparator != null) {
				monthsHorizontalSeparator = monthsHorizontalSeparator.replaceAll("[^\n]", " ");
			}
		}

		YearMonthDay highlightDay = new YearMonthDay(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH) - 1);
		Set<YearMonthDay> highlightDays = new HashSet<YearMonthDay>();
		if (cliParams.get("hldate") != null && !cliParams.get("hldate").isEmpty()) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String[] hlDays = cliParams.get("hldate").trim().split("\\s*,\\s*");
			if (hlDays.length == 1) {
				try {
					Date date = simpleDateFormat.parse(hlDays[0]);
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);
					highlightDay.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH) - 1);
				} catch (Exception e) {
					highlightDay = null;
				}
			} else {
				highlightDay = null;
				Calendar cal = Calendar.getInstance();
				for (String val : hlDays) {
					try {
						cal.setTime(simpleDateFormat.parse(val));
						highlightDays.add(new YearMonthDay(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH) - 1));
					} catch (Exception e) {
					}
				}
			}
		}
		if (highlightDay != null) {
			int hlYearOffset = CommandLineHelper.relativeInt(cliParams.get("hly"));
			int hlMonthOffset = CommandLineHelper.relativeInt(cliParams.get("hlm"));
			int hlDayOffset = CommandLineHelper.relativeInt(cliParams.get("hld"));
			highlightDay.addYearValue(hlYearOffset);
			highlightDay.addMonthValue(hlMonthOffset);
			highlightDay.addDayValue(hlDayOffset);
		}

		String possiblePositions[] = { "f", "l", "n" };
		Arrays.sort(possiblePositions);
		String hAxisLinePosition = "f";
		String vAxisLinePosition = "f";
		String titleLinePosition = "f";
		{
			String positionHorizontalAxisParam = cliParams.get("phx");
			if (positionHorizontalAxisParam != null && positionHorizontalAxisParam.trim().length() == 1) {
				String val = positionHorizontalAxisParam.trim().toLowerCase();
				if (Arrays.binarySearch(possiblePositions, val) >= 0) {
					hAxisLinePosition = val;
				}
			}
			String positionVerticalAxisParam = cliParams.get("pvx");
			if (positionVerticalAxisParam != null && positionVerticalAxisParam.trim().length() == 1) {
				String val = positionVerticalAxisParam.trim().toLowerCase();
				if (Arrays.binarySearch(possiblePositions, val) >= 0) {
					vAxisLinePosition = val;
				}
			}
			String positionTitleLineParam = cliParams.get("ptl");
			if (positionTitleLineParam != null && positionTitleLineParam.trim().length() == 1) {
				String val = positionTitleLineParam.trim().toLowerCase();
				if (Arrays.binarySearch(possiblePositions, val) >= 0) {
					titleLinePosition = val;
				}
			}
		}

		// YearMonthDay ymdToday = new YearMonthDay(now.get(Calendar.YEAR),
		// now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH) - 1);
		// System.out.println("Months to fill: "+ymEnd.compareTo(ymStart));

		// Calculate values
		int yearViewRows = (int) Math.ceil(((double) ymEnd.compareTo(ymStart) + 1) / (double) yearViewColumns);
		if (yearViewRows < 1)
			yearViewRows = 1;
		MonthGrid[][] monthsData = new MonthGrid[yearViewRows][];
		for (int i = 0; i < yearViewRows; i++) {
			monthsData[i] = new MonthGrid[yearViewColumns];
		}

		// Render
		int mGridRow = 0;
		int mGridCol = 0;
		while (!ymStart.isAfter(ymEnd)) {
			MonthGrid mGridVal = new MonthGrid(ymStart.getYearValue(), ymStart.getMonthValue());
			monthsData[mGridRow][mGridCol] = mGridVal;
			if (!flipYearView) {
				mGridCol++;
				if (mGridCol >= yearViewColumns) {
					mGridCol = 0;
					mGridRow++;
				}
			} else {
				mGridRow++;
				if (mGridRow >= yearViewRows) {
					mGridRow = 0;
					mGridCol++;
				}
			}
			ymStart.addMonthValue(1);
		}

		int highlightDayOfWeek = highlightDay != null ? highlightDay.getDayOfWeek() : -1;
		int highlightWeekNumber = highlightDay != null ? highlightDay.getWeekNumber() : -1;

		int lastOutputLineLength = 0;
		int mRowsCount = (flipMonthView ? 7 : 6);
		int mColsCount = (flipMonthView ? 6 : 7);
		for (int row = 0; row < yearViewRows; row++) {
			StringBuilder titleLine = new StringBuilder();
			StringBuilder hAxisLine = new StringBuilder();
			for (int col = 0; col < yearViewColumns; col++) {
				String titleText = "";
				if (monthsData[row][col] != null) {
					boolean thisMonth = highlightDay != null && (highlightDay.getYearValue() == monthsData[row][col].getYear()
							&& highlightDay.getMonthValue() == monthsData[row][col].getMonth());
					if (!hlOnly || thisMonth) {
						titleText = String.format(titleFormat, monthsData[row][col].getYear(), MONTHS_TITLES[monthsData[row][col].getMonth()],
								monthsData[row][col].getMonth());
					}

					int remainingLength = (2 + daysVerticalSeparator.length()) * mColsCount - daysVerticalSeparator.length() + monthsVerticalSeparator.length()
							- titleText.length();
					titleLine.append(titleText);
					for (int i = 0; i < remainingLength; i++) {
						titleLine.append(" ");
					}

					if ("f".equalsIgnoreCase(vAxisLinePosition)) {
						hAxisLine.append(EMPTY_CELL).append(daysVerticalSeparator);
						titleLine.append("  ");
						addSpaces(titleLine, daysVerticalSeparator.length());
					}
					for (int mCol = 0; mCol < mColsCount; mCol++) {
						String hAxisValue = getAxisValue(monthsData[row][col], true, flipMonthView, 0, mCol, weekDaysNamesCapitalization, weekNumberFormat,
								hlOnly, thisMonth, highlightDayOfWeek, highlightWeekNumber);
						hAxisLine.append(hAxisValue);
						if (mCol < mColsCount - 1) {
							hAxisLine.append(daysVerticalSeparator);
						}
					}
					if ("l".equalsIgnoreCase(vAxisLinePosition)) {
						hAxisLine.append(daysVerticalSeparator).append(EMPTY_CELL);
						titleLine.append("  ");
						addSpaces(titleLine, daysVerticalSeparator.length());
					}
					if (col < yearViewColumns - 1) {
						hAxisLine.append(monthsVerticalSeparator);
					}
				}
			}
			if ("f".equalsIgnoreCase(titleLinePosition)) {
				System.out.println(titleLine.toString());
			}
			if ("f".equalsIgnoreCase(hAxisLinePosition)) {
				System.out.println(hAxisLine.toString());
			}

			for (int mRow = 0; mRow < mRowsCount; mRow++) {
				StringBuilder outputLine = new StringBuilder();
				for (int col = 0; col < yearViewColumns; col++) {
					boolean thisMonth = false;
					if (monthsData[row][col] != null) {
						thisMonth = highlightDay != null && (highlightDay.getYearValue() == monthsData[row][col].getYear()
								&& highlightDay.getMonthValue() == monthsData[row][col].getMonth());
					}

					String vAxisValue = getAxisValue(monthsData[row][col], false, flipMonthView, mRow, 0, weekDaysNamesCapitalization, weekNumberFormat, hlOnly,
							thisMonth, highlightDayOfWeek, highlightWeekNumber);
					if ("f".equalsIgnoreCase(vAxisLinePosition)) {
						if (monthsData[row][col] != null) {
							outputLine.append(vAxisValue);
							outputLine.append(daysVerticalSeparator);
						}
					}
					for (int mCol = 0; mCol < mColsCount; mCol++) {
						boolean filled = false;
						if (monthsData[row][col] != null) {
							int dayVal = monthsData[row][col].getValueAt(flipMonthView ? mCol : mRow, flipMonthView ? mRow : mCol);
							if (dayVal >= 0) {
								if (!hlOnly || (thisMonth && highlightDay != null && highlightDay.getDayValue() == dayVal)) {
									outputLine.append(String.format(dayNumberFormat, dayVal + 1));
								} else if (hlOnly
										&& highlightDays.contains(new YearMonthDay(monthsData[row][col].getYear(), monthsData[row][col].getMonth(), dayVal))) {
									outputLine.append(String.format(dayNumberFormat, dayVal + 1));
								} else {
									outputLine.append(EMPTY_CELL);
								}
								filled = true;
							}
							if (!filled) {
								outputLine.append(EMPTY_CELL);
								if (mCol < mColsCount - 1) {
									outputLine.append(daysVerticalSeparator);
								}
							} else {
								if (mCol < mColsCount - 1) {
									outputLine.append(daysVerticalSeparator);
								}
							}
						}
					}
					if ("l".equalsIgnoreCase(vAxisLinePosition)) {
						if (monthsData[row][col] != null) {
							outputLine.append(daysVerticalSeparator);
							outputLine.append(vAxisValue);
						}
					}
					if (col < yearViewColumns - 1) {
						outputLine.append(monthsVerticalSeparator);
					}
				}
				System.out.println(outputLine.toString());
				lastOutputLineLength = outputLine.length();
				renderHorizontalSeparator(daysHorizontalSeparator, lastOutputLineLength);
			}
			if ("l".equalsIgnoreCase(hAxisLinePosition)) {
				System.out.println(hAxisLine.toString());
			}
			if ("l".equalsIgnoreCase(titleLinePosition)) {
				System.out.println(titleLine.toString());
			}
			renderHorizontalSeparator(monthsHorizontalSeparator, lastOutputLineLength);
		}
	}

	private static void addSpaces(StringBuilder sb, int count) {
		if (sb != null) {
			for (int i = 0; i < count; i++) {
				sb.append(" ");
			}
		}
	}

	private static String getAxisValue(MonthGrid monthGrid, boolean horizontal, boolean flipMonthView, int mRow, int mCol, String weekDaysCapital,
			String weekNumberFormat, boolean hlOnly, boolean thisMonth, int hlWeekDay, int hlWeekNumber) {
		if (monthGrid == null)
			return EMPTY_CELL;
		String axisValue = EMPTY_CELL;
		if (flipMonthView ^ horizontal) {
			if (!hlOnly || (thisMonth && (hlWeekDay == (flipMonthView ? mRow : mCol)))) {
				axisValue = WEEK_DAYS_NAMES[flipMonthView ? mRow : mCol];
				if (weekDaysCapital != null) {
					if ("u".equalsIgnoreCase(weekDaysCapital)) {
						axisValue = axisValue.toUpperCase();
					} else if ("l".equalsIgnoreCase(weekDaysCapital)) {
						axisValue = axisValue.toLowerCase();
					}
				}
			}
		} else {
			int weekNumber = monthGrid.getWeekNumber(flipMonthView ? mCol : mRow);
			if (weekNumber > 0) {
				if (!hlOnly || (thisMonth && weekNumber == hlWeekNumber)) {
					axisValue = String.format(weekNumberFormat, weekNumber);
				}
			}
		}
		return axisValue;
	}

	private static void renderHorizontalSeparator(String separatorString, int lineLength) {
		if (separatorString != null && separatorString.length() > 0) {
			StringBuilder result = new StringBuilder(separatorString);
			String separatorWithoutNewlines = separatorString.replaceAll("\n", "");
			if (separatorWithoutNewlines.length() > 0) {
				while (result.length() < lineLength) {
					result.append(separatorWithoutNewlines);
				}
				System.out.println(result.substring(0, lineLength));
			} else {
				System.out.println("\n");
			}
			if (separatorString.matches("\n")) {
				Matcher matcher = Pattern.compile("\n").matcher(separatorString);
				while (matcher.find()) {
					System.out.print("\n");
				}
			}
		}
	}

	private static void printHelpMessage() {
		InputStream helpMessageInputStream = JCCal.class.getResourceAsStream("/x/mvmn/util/jccal/helpmsg.txt");
		InputStreamReader helpMessageReader;
		try {
			helpMessageReader = new InputStreamReader(helpMessageInputStream, "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(helpMessageReader);
			String line;
			do {
				line = bufferedReader.readLine();
				if (line != null) {
					System.out.println(line);
				}
			} while (line != null);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace(); // Should never happen for UTF-8
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
