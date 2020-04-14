package com.msxichen.diskscanner.core.model;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ExtensionItem {

	private String extension;
	private AtomicLong sizeInByte;
	private AtomicInteger count;

	public ExtensionItem(String extension) {
		super();
		this.extension = extension;
		this.sizeInByte = new AtomicLong();
		this.count = new AtomicInteger();
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}
	
	public long increaseSizeInByte(long delta) {
		return sizeInByte.addAndGet(delta);
	}

	public long getSizeInByteValue() {
		return sizeInByte.longValue();
	}
	
	public int increaseCount(int delta) {
		return count.addAndGet(delta);
	}
	
	public int getCountValue() {
		return count.intValue();
	}

}
