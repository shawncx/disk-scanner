package com.msxichen.diskscanner.core.model;

import java.time.Instant;

public class ScanResultSummaryInfo {

	private long timeCostInSecond;
	private long fileCount;
	private long dirCount;
	private long sizeInByte;
	private String baseDir;
	private String[] excludedPaths;
	private Instant endInstant;

	public ScanResultSummaryInfo() {
		super();
	}

	public long getTimeCostInSecond() {
		return timeCostInSecond;
	}

	public void setTimeCostInSecond(long timeCostInSecond) {
		this.timeCostInSecond = timeCostInSecond;
	}

	public long getFileCount() {
		return fileCount;
	}

	public void setFileCount(long fileCount) {
		this.fileCount = fileCount;
	}

	public long getDirCount() {
		return dirCount;
	}

	public void setDirCount(long dirCount) {
		this.dirCount = dirCount;
	}

	public long getSizeInByte() {
		return sizeInByte;
	}

	public void setSizeInByte(long sizeInByte) {
		this.sizeInByte = sizeInByte;
	}

	public String getBaseDir() {
		return baseDir;
	}

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}

	public String[] getExcludedPaths() {
		return excludedPaths;
	}

	public void setExcludedPaths(String[] excludedPaths) {
		this.excludedPaths = excludedPaths;
	}

	public Instant getEndInstant() {
		return endInstant;
	}

	public void setEndInstant(Instant endInstant) {
		this.endInstant = endInstant;
	}

}
