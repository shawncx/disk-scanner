package com.msxichen.diskscanner;

import org.junit.Test;

public class DiskScannerTest {

	@Test
	public void testScan() {
		DiskScanner scanner = new DiskScanner(5);
		String baseDir = "C:\\Users\\xichen\\tool\\apache-maven-3.6.3\\lib";
		String[] excludedDirs = new String[] {
				"C:\\Users\\xichen\\eclipse-workspace\\diskscanner\\target\\classes" };
		excludedDirs = null;
		scanner.scan(baseDir, excludedDirs);
	}
	
	@Test
	public void testFullDir() {
		String baseDir = "C:\\Users\\xichen\\Desktop\\dbeaver";
		DiskScanner scanner = new DiskScanner(5);
		scanner.scan(baseDir, null);
	}

}
