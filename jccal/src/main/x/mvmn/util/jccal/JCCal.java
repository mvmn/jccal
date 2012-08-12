package x.mvmn.util.jccal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Map;

import x.mvmn.util.cli.CommandLineHelper;
import x.mvmn.util.dates.impl.YearMonth;
import x.mvmn.util.dates.impl.YearMonthDay;
import x.mvmn.util.jccal.model.MonthGrid;

public class JCCal {

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

		YearMonthDay ymdToday = new YearMonthDay(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH) - 1);
		// System.out.println("Months to fill: "+ymEnd.compareTo(ymStart));
		int yearViewRows = (int) Math.ceil(((double) ymEnd.compareTo(ymStart) + 1) / (double) yearViewColumns);
		if (yearViewRows < 1)
			yearViewRows = 1;
		MonthGrid[][] monthsData = new MonthGrid[yearViewRows][];
		for (int i = 0; i < yearViewRows; i++) {
			monthsData[i] = new MonthGrid[yearViewColumns];
		}

		// System.out.format("Grid : %02d x %02d\n",yearViewRows,yearViewColumns);
		int mGridRow = 0;
		int mGridCol = 0;
		while (!ymStart.isAfter(ymEnd)) {
			MonthGrid mGridVal = new MonthGrid(ymStart.getYearValue(), ymStart.getMonthValue());
			// System.out.format("Filling %02d:%02d with %s\n", mGridRow,
			// mGridCol, ymStart.toString());
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

		for (int row = 0; row < yearViewRows; row++) {
			for (int mRow = 0; mRow < (flipMonthView ? 7 : 6); mRow++) {
				for (int col = 0; col < yearViewColumns; col++) {
					for (int mCol = 0; mCol < (flipMonthView ? 6 : 7); mCol++) {
						if (monthsData[row][col] != null) {
							int dayVal = monthsData[row][col].getValueAt(flipMonthView ? mCol : mRow, flipMonthView ? mRow : mCol);
							if(dayVal>=0) {
								System.out.format("%02d|", dayVal+1);
							} else {
								System.out.print("  |");
							}
						} else {
							System.out.print("  |");
						}
					}
					System.out.print(" ");
				}
				System.out.println("");
			}
			System.out.println("");
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
