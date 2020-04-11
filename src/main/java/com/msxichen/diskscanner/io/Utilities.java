package com.msxichen.diskscanner.io;

import com.msxichen.diskscanner.core.model.OutputUnit;

public class Utilities {

	private static final String SIZE_FORMAT = "%.2f";

	public static String formatSize(OutputUnit unit, long sizeInByte) {
		double size = 0;
		if (OutputUnit.Auto == unit) {
			size = sizeInByte;
			if (size < 1024) {
				return formatSize(size) + OutputUnit.B;
			}
			size /= 1024d;
			if (size < 1024) {
				return formatSize(size) + OutputUnit.Kb;
			}
			size /= 1024d;
			if (size < 1024) {
				return formatSize(size) + OutputUnit.Mb;
			}
			size /= 1024d;
			return formatSize(size) + OutputUnit.Gb;
			
		} else if (OutputUnit.Gb == unit) {
			size = sizeInByte / 1024d / 1024d / 1024d;
		} else if (OutputUnit.Mb == unit) {
			size = sizeInByte / 1024d / 1024d;
		} else if (OutputUnit.Kb == unit) {
			size = sizeInByte / 1024d;
		} else {
			throw new IllegalArgumentException("Unknow unit: " + unit);
		}
		return formatSize(size) + unit;
	}

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
