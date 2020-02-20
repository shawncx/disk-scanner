package com.msxichen.diskscanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.msxichen.diskscanner.model.ScanConfiguration;

public class DiskScannerTest {

//	@Test
//	public void testScan() {
//		DiskScanner scanner = new DiskScanner(5);
//		String baseDir = "D:\\SSIS\\Rel\\SQL17\\SQL17_SSIS";
////		String baseDir = "D:\\DsMainDev";
//		String[] excludedDirs = new String[] {
//				"D:\\SSIS\\Rel\\SQL17\\SQL17_SSIS\\localization",
//				"D:\\SSIS\\Rel\\SQL17\\SQL17_SSIS\\Tools",
//				"D:\\SSIS\\Rel\\SQL17\\SQL17_SSIS\\TestTools",
//				"D:\\SSIS\\Rel\\SQL17\\SQL17_SSIS\\optimization", 
//				"D:\\SSIS\\Rel\\SQL17\\SQL17_SSIS\\Test",
//				"D:\\SSIS\\Rel\\SQL17\\SQL17_SSIS\\Testtestsrc\\dts\\Test\\PackageRepository",
//				"D:\\SSIS\\Rel\\SQL17\\SQL17_SSIS\\Testtestsrc\\dts\\UnitTest",
//				"D:\\SSIS\\Rel\\SQL17\\SQL17_SSIS\\Testtestsrc\\crossgrouptools",
//				"D:\\SSIS\\Rel\\SQL17\\SQL17_SSIS\\Testtestsrc\\DP",
//				"D:\\SSIS\\Rel\\SQL17\\SQL17_SSIS\\Testtestsrc\\komodo\\yukon\\SRC\\Tools",
//				"D:\\SSIS\\Rel\\SQL17\\SQL17_SSIS\\Testtestsrc\\komodo\\yukon\\bin",
//				"D:\\SSIS\\Rel\\SQL17\\SQL17_SSIS\\Testtestsrc\\komodo\\yukon\\Common_input",
//				"D:\\SSIS\\Rel\\SQL17\\SQL17_SSIS\\Testtestsrc\\komodo\\yukon\\setup",
//				"D:\\SSIS\\Rel\\SQL17\\SQL17_SSIS\\_BuildCommon"};
////		excludedDirs = null;
//		scanner.scan(baseDir, excludedDirs);
//	}
	
	@Test
	public void testFullDir() throws IOException {
		ScanConfiguration config = readConfig("src/test/resources/testScanFullDir.json");
		DiskScanner scanner = new DiskScanner(config);
		scanner.scan();
	}
	
	private ScanConfiguration readConfig(String path) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
		StringBuilder configJson = new StringBuilder();
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			configJson.append(line).append("\r\n");
		}
		bufferedReader.close();
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(configJson.toString(), ScanConfiguration.class);
	}

}
