package com.msxichen.diskscanner.io;

public class Utilities {

	private static final String SIZE_FORMAT = "%.2f";

	public static String formatSize(double size) {
		return String.format(SIZE_FORMAT, size);
	}

	public static String wildcardToRegex(String wildcard) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < wildcard.length(); i++) {
			char c = wildcard.charAt(i);
			if (c == '\\') {
				result.append("\\\\");
			} else if (c == '.') {
				result.append("\\.");
			} else if (c == '*') {
				result.append(".*");
			} else if (c == '?') {
				result.append('.');
			} else if (c == '~') {
				if (i + 1 < wildcard.length()) {
					char nextC = wildcard.charAt(i + 1);
					if (nextC == '*' || nextC == '?') {
						result.append("\\").append(nextC);
						i++;
					} else {
						result.append(c);
					}
				} else {
					result.append(c);
				}
			} else {
				result.append(c);
			}
		}
		return result.toString();
	}

}
