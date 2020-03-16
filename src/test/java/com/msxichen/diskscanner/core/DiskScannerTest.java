package com.msxichen.diskscanner.core;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import com.msxichen.diskscanner.core.model.ScanConfiguration;
import com.msxichen.diskscanner.core.model.ScanContext;
import com.msxichen.diskscanner.io.ScanConfigurationReader;

@Ignore
public class DiskScannerTest {
	
	@Test
	public void testScanExcludedDir() throws IOException {
		runScan("src/test/resources/testScanExcludedDir.json");
	}

	@Test
	public void testFullDir() throws IOException {
		runScan("src/test/resources/testScanFullDir.json");
	}

	@Test
	public void testLimitFileCount() throws IOException {
		runScan("src/test/resources/testLimitFileCount.json");
	}

	@Test
	public void testLargeDir() throws IOException {
		runScan("src/test/resources/testLargeDir.json");
	}

	private void runScan(String configPath) throws IOException {
		ScanConfigurationReader reader = new ScanConfigurationReader();
		ScanConfiguration config = reader.read(configPath);

		ScanContext context = reader.buildScanContext(config);
		DiskScanner scanner = new DiskScanner();

		scanner.scan(context);
	}

}
