package com.msxichen.diskscanner.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.Instant;

import org.junit.Test;

import com.msxichen.diskscanner.core.model.ScanConfiguration;
import com.msxichen.diskscanner.core.model.ScanContext;
import com.msxichen.diskscanner.core.model.ScanProgress;
import com.msxichen.diskscanner.core.model.ScanResult;
import com.msxichen.diskscanner.io.ScanConfigurationReader;
import com.msxichen.diskscanner.io.ScanResultLocalWriter;

public class DiskScannerAsyncTest {

	@Test
	public void testScanExcludedDir() throws Exception {
		runScan("src/test/resources/testScanExcludedDir.json");
	}

	private void runScan(String configPath) throws Exception {
		ScanConfigurationReader reader = new ScanConfigurationReader();
		ScanConfiguration config = reader.read(configPath);

		ScanContext context = reader.buildScanContext(config);
		DiskScannerAsync scanner = new DiskScannerAsync();

		scanner.scan(context);

		ScanProgress progress = scanner.getProgress();
		assertFalse(progress.isDone());

		Instant st = Instant.now();
		while (Duration.between(st, Instant.now()).toSeconds() < 60) {
			progress = scanner.getProgress();
			if (progress.isDone()) {
				break;
			}
		}
		assertTrue(progress.isDone());

		ScanResult result = scanner.getScanResult();
		try (ScanResultLocalWriter writer = new ScanResultLocalWriter(context)) {
			writer.writeResult(result);
		}
	}
}
