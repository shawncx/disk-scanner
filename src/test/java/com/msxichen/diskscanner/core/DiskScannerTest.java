package com.msxichen.diskscanner.core;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.msxichen.diskscanner.core.model.ScanConfiguration;
import com.msxichen.diskscanner.io.ScanConfigurationReader;

public class DiskScannerTest {

	@Test
	public void testScanExcludedDir() throws IOException {
		ScanConfigurationReader reader = new ScanConfigurationReader();
		ScanConfiguration config = reader.read("src/test/resources/testScanExcludedDir.json");
		DiskScanner scanner = new DiskScanner(config);
		scanner.scan();
	}

	@Test
	public void testFullDir() throws IOException {
		ScanConfigurationReader reader = new ScanConfigurationReader();
		ScanConfiguration config = reader.read("src/test/resources/testScanFullDir.json");
		DiskScanner scanner = new DiskScanner(config);
		scanner.scan();
	}
	
	@Test
	public void testLimitFileCount() throws IOException {
		ScanConfigurationReader reader = new ScanConfigurationReader();
		ScanConfiguration config = reader.read("src/test/resources/testLimitFileCount.json");
		DiskScanner scanner = new DiskScanner(config);
		scanner.scan();
	}
	
	@Test
	public void testLargeDir() throws IOException {
		ScanConfigurationReader reader = new ScanConfigurationReader();
		ScanConfiguration config = reader.read("src/test/resources/testLargeDir.json");
		DiskScanner scanner = new DiskScanner(config);
		scanner.scan();
	}

}
