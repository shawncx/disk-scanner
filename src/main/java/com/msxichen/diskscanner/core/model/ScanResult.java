package com.msxichen.diskscanner.core.model;

public class ScanResult {

	private ScanResultSummaryInfo summaryInfo;
	private ScanResultDirectoryInfo directoryInfo;
	private ScanResultFileInfo fileInfo;

	public ScanResultSummaryInfo getSummaryInfo() {
		return summaryInfo;
	}

	public void setSummaryInfo(ScanResultSummaryInfo summaryInfo) {
		this.summaryInfo = summaryInfo;
	}

	public ScanResultDirectoryInfo getDirectoryInfo() {
		return directoryInfo;
	}

	public void setDirectoryInfo(ScanResultDirectoryInfo directoryInfo) {
		this.directoryInfo = directoryInfo;
	}

	public ScanResultFileInfo getFileInfo() {
		return fileInfo;
	}

	public void setFileInfo(ScanResultFileInfo fileInfo) {
		this.fileInfo = fileInfo;
	}

}
