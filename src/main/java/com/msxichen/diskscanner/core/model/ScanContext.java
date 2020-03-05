package com.msxichen.diskscanner.core.model;

import java.nio.file.Path;
import java.util.Date;
import java.util.Set;

public class ScanContext {

	private int threadNum;
	private String baseDir;
	private String[] excludedPaths;
	private Set<OutputType> outputTypes;
	private Path fileOutputLoc;
	private Path summaryOutputPath;
	private Path dirInfoOutputPath;
	private Path fileInfoOutputPath;
	private OutputUnit fileOutputUnit;
	private OutputUnit dirOutputUnit;
	private long fileTopCount;

	private Date startTime;

	public ScanContext() {
		super();
	}

	public int getThreadNum() {
		return threadNum;
	}

	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
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

	public Set<OutputType> getOutputTypes() {
		return outputTypes;
	}

	public void setOutputTypes(Set<OutputType> outputTypes) {
		this.outputTypes = outputTypes;
	}

	public Path getFileOutputLoc() {
		return fileOutputLoc;
	}

	public void setFileOutputLoc(Path fileOutputLoc) {
		this.fileOutputLoc = fileOutputLoc;
	}

	public OutputUnit getFileOutputUnit() {
		return fileOutputUnit;
	}

	public void setFileOutputUnit(OutputUnit fileOutputUnit) {
		this.fileOutputUnit = fileOutputUnit;
	}

	public OutputUnit getDirOutputUnit() {
		return dirOutputUnit;
	}

	public void setDirOutputUnit(OutputUnit dirOutputUnit) {
		this.dirOutputUnit = dirOutputUnit;
	}

	public long getFileTopCount() {
		return fileTopCount;
	}

	public void setFileTopCount(long fileTopCount) {
		this.fileTopCount = fileTopCount;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Path getSummaryOutputPath() {
		return summaryOutputPath;
	}

	public void setSummaryOutputPath(Path summaryOutputPath) {
		this.summaryOutputPath = summaryOutputPath;
	}

	public Path getDirInfoOutputPath() {
		return dirInfoOutputPath;
	}

	public void setDirInfoOutputPath(Path dirInfoOutputPath) {
		this.dirInfoOutputPath = dirInfoOutputPath;
	}

	public Path getFileInfoOutputPath() {
		return fileInfoOutputPath;
	}

	public void setFileInfoOutputPath(Path fileInfoOutputPath) {
		this.fileInfoOutputPath = fileInfoOutputPath;
	}

}
