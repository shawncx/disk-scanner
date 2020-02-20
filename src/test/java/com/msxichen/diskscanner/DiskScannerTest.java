package com.msxichen.diskscanner;

import org.junit.Test;

public class DiskScannerTest {

	@Test
	public void testScan() {
		DiskScanner scanner = new DiskScanner(5);
		String baseDir = "D:\\SSIS\\Rel\\SQL17\\SQL17_SSIS";
//		String baseDir = "D:\\DsMainDev";
		String[] excludedDirs = new String[] {
				"D:\\SSIS\\Rel\\SQL17\\SQL17_SSIS\\localization",
				"D:\\SSIS\\Rel\\SQL17\\SQL17_SSIS\\Tools",
				"D:\\SSIS\\Rel\\SQL17\\SQL17_SSIS\\TestTools",
				"D:\\SSIS\\Rel\\SQL17\\SQL17_SSIS\\optimization", 
				"D:\\SSIS\\Rel\\SQL17\\SQL17_SSIS\\Test",
				"D:\\SSIS\\Rel\\SQL17\\SQL17_SSIS\\Testtestsrc\\dts\\Test\\PackageRepository",
				"D:\\SSIS\\Rel\\SQL17\\SQL17_SSIS\\Testtestsrc\\dts\\UnitTest",
				"D:\\SSIS\\Rel\\SQL17\\SQL17_SSIS\\Testtestsrc\\crossgrouptools",
				"D:\\SSIS\\Rel\\SQL17\\SQL17_SSIS\\Testtestsrc\\DP",
				"D:\\SSIS\\Rel\\SQL17\\SQL17_SSIS\\Testtestsrc\\komodo\\yukon\\SRC\\Tools",
				"D:\\SSIS\\Rel\\SQL17\\SQL17_SSIS\\Testtestsrc\\komodo\\yukon\\bin",
				"D:\\SSIS\\Rel\\SQL17\\SQL17_SSIS\\Testtestsrc\\komodo\\yukon\\Common_input",
				"D:\\SSIS\\Rel\\SQL17\\SQL17_SSIS\\Testtestsrc\\komodo\\yukon\\setup",
				"D:\\SSIS\\Rel\\SQL17\\SQL17_SSIS\\_BuildCommon"};
//		excludedDirs = null;
		scanner.scan(baseDir, excludedDirs);
	}
	
	@Test
	public void testFullDir() {
		String baseDir = "C:\\Users\\xichen\\Desktop\\dbeaver";
		DiskScanner scanner = new DiskScanner(5);
		scanner.scan(baseDir, null);
	}

}
