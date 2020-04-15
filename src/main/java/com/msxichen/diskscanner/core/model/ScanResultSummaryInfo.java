package com.msxichen.diskscanner.core.model;

import java.util.ArrayList;
import java.util.List;

public class ScanResultSummaryInfo {

	private long timeCostInSecond;
	private long fileCount;
	private long dirCount;
	private long sizeInByte;
	private String size;
	private String baseDir;
	private String[] excludedPaths;

	private List<ScanResultExtensionItem> extensionItems = new ArrayList<>();
	private List<ScanResultFile> topFiles = new ArrayList<>();

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

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public List<ScanResultExtensionItem> getExtensionItems() {
		return extensionItems;
	}

	public void setExtensionItems(List<ScanResultExtensionItem> extensionItems) {
		this.extensionItems = extensionItems;
	}

	public List<ScanResultFile> getTopFiles() {
		return topFiles;
	}

	public void setTopFiles(List<ScanResultFile> topFiles) {
		this.topFiles = topFiles;
	}

}
