package com.msxichen.diskscanner.core.model;

public class ScanConfiguration {

	private int threadNum;
	private String baseDir;
	private String[] excludedPaths;
	private String[] outputTypes;
	private String fileOutputLoc;
	private String fileSizeUnit;
	private String dirSizeUnit;
	private long fileTopCount;

	public ScanConfiguration(int threadNum, String baseDir, String[] excludedPaths, String[] outputTypes,
			String fileOutputLoc, String fileSizeUnit, String dirSizeUnit, long fileTopCount) {
		super();
		this.threadNum = threadNum;
		this.baseDir = baseDir;
		this.excludedPaths = excludedPaths;
		this.outputTypes = outputTypes;
		this.fileOutputLoc = fileOutputLoc;
		this.fileSizeUnit = fileSizeUnit;
		this.dirSizeUnit = dirSizeUnit;
		this.fileTopCount = fileTopCount;
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

	public String getFileOutputLoc() {
		return fileOutputLoc;
	}

	public long getFileTopCount() {
		return fileTopCount;
	}

	public String getFileSizeUnit() {
		return fileSizeUnit;
	}

	public String getDirSizeUnit() {
		return dirSizeUnit;
	}

}
