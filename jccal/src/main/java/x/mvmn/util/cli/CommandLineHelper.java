package x.mvmn.util.cli;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CommandLineHelper {

	public static Map<String, String> getArgs(String[] args) {
		Map<String, StringBuilder> resultBuilder = new HashMap<String, StringBuilder>();

		if (args != null && args.length > 0) {
			String key = null;
			for (String arg : args) {
				if (arg.startsWith("-")) {
					key = arg.substring(1).toLowerCase();
					resultBuilder.put(key, new StringBuilder());
				} else {
					if (key != null) {
						StringBuilder value = resultBuilder.get(key);
						if (value.length() > 0) {
							value.append(" ");
						}
						value.append(arg);
					}
				}
			}
		}

		Map<String, String> result = new HashMap<String, String>(resultBuilder.size());
		for (Map.Entry<String, StringBuilder> resultItem : resultBuilder.entrySet()) {
			result.put(resultItem.getKey(), resultItem.getValue().toString());
		}

		return result;
	}

	public static int relativeInt(String val) {
		int result = 0;

		if (val != null && !val.isEmpty()) {
			try {
				boolean sub = val.startsWith("m");
				boolean add = val.startsWith("p");
				if (sub || add) {
					val = val.substring(1);
					if (!val.isEmpty()) {
						int delta = Integer.parseInt(val);
						if (sub) {
							result -= delta;
						} else {
							result += delta;
						}
					}
				}
			} catch (NumberFormatException e) {
			}
		}

		return result;
	}

	private static final Pattern PATTERN_DIGITS = Pattern.compile("^\\d+$");

	public static int asInt(String val, int defaultVal) {
		int result = defaultVal;

		if (val != null && !val.isEmpty() && PATTERN_DIGITS.matcher(val.trim()).matches()) {
			try {
				result = Integer.parseInt(val.trim());
			} catch (NumberFormatException e) {
			}
		}

		return result;
	}

	public static boolean asBoolean(String val, boolean defaultVal) {
		boolean result = defaultVal;

		if (val != null && !val.isEmpty()) {
			if (Boolean.TRUE.toString().equals(val.trim())) {
				result = true;
			} else if (Boolean.FALSE.toString().equals(val.trim())) {
				result = false;
			}
		}

		return result;
	}
}
