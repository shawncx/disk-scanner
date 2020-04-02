package com.msxichen.diskscanner;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.msxichen.diskscanner.core.DiskScanner;
import com.msxichen.diskscanner.core.model.ScanConfiguration;
import com.msxichen.diskscanner.core.model.ScanContext;
import com.msxichen.diskscanner.core.model.ScanResult;
import com.msxichen.diskscanner.io.ScanResultLocalWriter;
import com.msxichen.diskscanner.io.ScanConfigurationReader;

public class Main {

	private static final Logger LOGGER = LogManager.getLogger();

	public static void main(String[] args) throws IOException {
		if (args == null || args.length == 0) {
			LOGGER.error("Input configuration path");
			System.exit(0);
		}

		ScanConfigurationReader reader = new ScanConfigurationReader();
		ScanConfiguration config = reader.read(args[0]);

		ScanContext context = reader.buildScanContext(config);
		DiskScanner scanner = new DiskScanner();

		scanner.scan(context);
		
		ScanResult result = scanner.getScanResult();
		writeResult(context, result);
		
		LOGGER.trace("Fin");
		System.exit(0);
	}
	
	private static void writeResult(ScanContext context, ScanResult result) {
		try (ScanResultLocalWriter writer = new ScanResultLocalWriter(context)) {
			writer.writeResult(result);
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

}
