package com.msxichen.diskscanner.model;

public class ScanConfiguration {

	public static final String OUTPUT_TYPE_CONSOLE = "console";
	public static final String OUTPUT_TYPE_FILE = "file";

	private int threadNum;
	private String baseDir;
	private String[] excludedPaths;
	private String[] outputTypes;
	private String outputFile;

	public ScanConfiguration(int threadNum, String baseDir, String[] excludedPaths, String[] outputTypes,
			String outputFile) {
		super();
		this.threadNum = threadNum;
		this.baseDir = baseDir;
		this.excludedPaths = excludedPaths;
		this.outputTypes = outputTypes;
		this.outputFile = outputFile;
	}

	public ScanConfiguration() {
		super();
	}

	public int getThreadNum() {
		return threadNum;
	}

	public String getBaseDir() {
		return baseDir;
	}

	public String[] getExcludedPaths() {
		return excludedPaths;
	}

	public String[] getOutputTypes() {
		return outputTypes;
	}

	public String getOutputFile() {
		return outputFile;
	}

}
