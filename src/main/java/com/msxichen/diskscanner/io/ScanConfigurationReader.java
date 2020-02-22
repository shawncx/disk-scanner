package com.msxichen.diskscanner.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msxichen.diskscanner.core.model.ScanConfiguration;

public class ScanConfigurationReader {
	
	public ScanConfiguration read(String path) throws IOException {
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))){
			StringBuilder configJson = new StringBuilder();
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				configJson.append(line).append("\r\n");
			}
			
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(configJson.toString(), ScanConfiguration.class);
		} 
	}

}
