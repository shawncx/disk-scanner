package com.msxichen.diskscanner.io;

public class Utilies {
	
	private static final String SIZE_FORMAT = "%.2f";
	
	public static String formatSize(double size) {
		return String.format(SIZE_FORMAT, size);
	}

}
