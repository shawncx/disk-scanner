package com.msxichen.diskscanner.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msxichen.diskscanner.core.model.OutputType;
import com.msxichen.diskscanner.core.model.OutputUnit;
import com.msxichen.diskscanner.core.model.ScanConfiguration;
import com.msxichen.diskscanner.core.model.ScanContext;

public class ScanConfigurationReader {

	public static final String FILE_OUTPUT_NAME_SUMMARY = "diskScan-summary";
	public static final String FILE_OUTPUT_NAME_DIR_INFO = "diskScan-dirInfo";
	public static final String FILE_OUTPUT_NAME_FILE_INFO = "diskScan-fileInfo";

	private static final SimpleDateFormat FILE_OUTPUT_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

	private static final Logger LOGGER = LogManager.getLogger();

	public ScanConfiguration read(String path) throws IOException {
		LOGGER.trace("Start to read configuration");
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
			StringBuilder configJson = new StringBuilder();
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				configJson.append(line).append("\r\n");
			}

			ObjectMapper objectMapper = new ObjectMapper();
			ScanConfiguration config = objectMapper.readValue(configJson.toString(), ScanConfiguration.class);
			LOGGER.trace("Read configuration finished");
			return config;
		}
	}

	public ScanContext buildScanContext(ScanConfiguration config) throws IllegalArgumentException {
		LOGGER.trace("Start to build context from configuraiton");
		ScanContext context = new ScanContext();
		context.setStartTime(new Date());

		if (config.getThreadNum() <= 0) {
			throw new IllegalArgumentException("Thread number cannot less than 1");
		}
		context.setThreadNum(config.getThreadNum());

		if (config.getBaseDir() == null || config.getBaseDir().length() == 0) {
			throw new IllegalArgumentException("Base dir cannot be null or empty");
		}
		// For case like: C:. It cannot be recognized.
		String basePath = config.getBaseDir().endsWith(":") ? config.getBaseDir() + "\\" : config.getBaseDir();
		File baseDir = new File(basePath);
		if (!baseDir.exists()) {
			throw new IllegalArgumentException("Base dir does not exist");
		}
		context.setBaseDir(config.getBaseDir());

		context.setExcludedPaths(config.getExcludedPaths() == null ? new String[0] : config.getExcludedPaths());

		Set<OutputType> outputTypes = new HashSet<OutputType>();
		if (config.getOutputTypes() == null || config.getOutputTypes().length == 0) {
			throw new IllegalArgumentException("Output type cannot be null or empty");
		}
		for (String s : config.getOutputTypes()) {
			outputTypes.add(OutputType.valueOf(s));
		}
		context.setOutputTypes(outputTypes);

		if (outputTypes.contains(OutputType.File)) {
			if (config.getFileOutputLoc() == null || config.getFileOutputLoc().length() == 0) {
				throw new IllegalArgumentException(
						"File output loc cannot be null or empty if output type includes File");
			}
			File fileOutputLoc = new File(config.getFileOutputLoc());
			if (!fileOutputLoc.exists()) {
				throw new IllegalArgumentException("File output location does not exist");
			}
			context.setFileOutputLoc(Paths.get(config.getFileOutputLoc()));

			String summaryPath = FILE_OUTPUT_NAME_SUMMARY + "-"
					+ FILE_OUTPUT_TIME_FORMAT.format(context.getStartTime());
			context.setSummaryOutputPath(context.getFileOutputLoc().resolve(summaryPath));
			String dirInfoPath = FILE_OUTPUT_NAME_DIR_INFO + "-"
					+ FILE_OUTPUT_TIME_FORMAT.format(context.getStartTime());
			context.setDirInfoOutputPath(context.getFileOutputLoc().resolve(dirInfoPath));
			String fileInfoPath = FILE_OUTPUT_NAME_FILE_INFO + "-"
					+ FILE_OUTPUT_TIME_FORMAT.format(context.getStartTime());
			context.setFileInfoOutputPath(context.getFileOutputLoc().resolve(fileInfoPath));
		}

		if (config.getFileSizeUnit() == null || config.getFileSizeUnit().length() == 0) {
			throw new IllegalArgumentException("File size unit cannot be null or empty");
		}
		context.setFileOutputUnit(OutputUnit.valueOf(config.getFileSizeUnit()));

		if (config.getDirSizeUnit() == null || config.getDirSizeUnit().length() == 0) {
			throw new IllegalArgumentException("Dir size unit cannot be null or empty");
		}
		context.setDirOutputUnit(OutputUnit.valueOf(config.getDirSizeUnit()));

		LOGGER.trace("Build context finished");
		return context;
	}

}
