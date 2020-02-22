package com.msxichen.diskscanner;

import java.io.IOException;

import com.msxichen.diskscanner.core.DiskScanner;
import com.msxichen.diskscanner.core.model.ScanConfiguration;
import com.msxichen.diskscanner.io.ScanConfigurationReader;

public class Main {

	public static void main(String[] args) throws IOException {
		if (args == null || args.length == 0) {
			System.out.println("Input configuration path");
			System.exit(0);
		}

		ScanConfiguration config = new ScanConfigurationReader().read(args[0]);

		DiskScanner scanner = new DiskScanner(config);
		scanner.scan();
		System.out.println("Fin");
		System.exit(0);
	}

}
