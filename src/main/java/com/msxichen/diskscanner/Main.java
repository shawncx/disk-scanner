package com.msxichen.diskscanner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msxichen.diskscanner.model.ScanConfiguration;

public class Main {

	public static void main(String[] args) throws IOException {
		if (args == null || args.length == 0) {
			System.out.println("Input configuration path");
			System.exit(0);
		}

		BufferedReader bufferedReader = new BufferedReader(new FileReader(args[0]));
		StringBuilder configJson = new StringBuilder();
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			configJson.append(line).append("\r\n");
		}
		
		ObjectMapper objectMapper = new ObjectMapper();
		ScanConfiguration config = objectMapper.readValue(configJson.toString(), ScanConfiguration.class);

		DiskScanner scanner = new DiskScanner(config);
		scanner.scan();
		System.out.println("Fin");
		bufferedReader.close();
		System.exit(0);
	}

}
