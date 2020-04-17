package com.msxichen.diskscanner.core.model;

public class ScanResultExtensionItem {

	private String extension;
	private long sizeInByte;
	private int count;

	public ScanResultExtensionItem(String extension, long sizeInByte, int count) {
		super();
		this.extension = extension;
		this.sizeInByte = sizeInByte;
		this.count = count;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public long getSizeInByte() {
		return sizeInByte;
	}

	public void setSizeInByte(long sizeInByte) {
		this.sizeInByte = sizeInByte;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
