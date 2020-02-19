package com.msxichen.diskscanner;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			System.out.println("Input base dir");
			System.exit(0);
		}
		String baseDir = args[0];
		String[] excludedDirs = null;
		if (args.length > 1) {
			excludedDirs = new String[args.length - 1];
			IntStream.range(1, args.length).mapToObj((i) -> args[i].toLowerCase()).toArray(String[]::new);
		}
		DiskScanner scanner = new DiskScanner(5);
		scanner.scan(baseDir, excludedDirs);
		System.out.println("Fin");
		System.exit(0);
	}

}
