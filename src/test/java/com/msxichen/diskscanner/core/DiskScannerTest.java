package com.msxichen.diskscanner.core;

import org.junit.Ignore;
import org.junit.Test;

import com.msxichen.diskscanner.core.model.ScanConfiguration;
import com.msxichen.diskscanner.core.model.ScanContext;
import com.msxichen.diskscanner.core.model.ScanResult;
import com.msxichen.diskscanner.io.ScanConfigurationReader;
import com.msxichen.diskscanner.io.ScanResultLocalWriter;

@Ignore
public class DiskScannerTest {

	@Test
	public void testScanExcludedDir() throws Exception {
		runScan("src/test/resources/testScanExcludedDir.json");
	}

	@Test
	public void testFullDir() throws Exception {
		runScan("src/test/resources/testScanFullDir.json");
	}

	@Test
	public void testLimitFileCount() throws Exception {
		runScan("src/test/resources/testLimitFileCount.json");
	}

	@Test
	public void testLargeDir() throws Exception {
		runScan("src/test/resources/testLargeDir.json");
	}

	private void runScan(String configPath) throws Exception {
		ScanConfigurationReader reader = new ScanConfigurationReader();
		ScanConfiguration config = reader.read(configPath);

		ScanContext context = reader.buildScanContext(config);
		DiskScanner scanner = new DiskScanner();

		scanner.scan(context);

		ScanResult result = scanner.getScanResult();
		try (ScanResultLocalWriter writer = new ScanResultLocalWriter(context)) {
			writer.writeResult(result);
		}
	}

}
