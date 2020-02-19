package com.msxichen.diskscanner;

import java.io.File;

public class FileSnap {

	private String absolutePath;
	private String absolutPathInLowerCase;
	private long sizeInByte;
	private boolean isDirectory;
	private File[] subFiles;

	public FileSnap(File file) {
		this.absolutePath = file.getAbsolutePath();
		this.absolutPathInLowerCase = this.absolutePath.toLowerCase();
		this.isDirectory = file.isDirectory();
		if (file.isDirectory()) {
			this.sizeInByte = file.length();
			this.subFiles = file.listFiles();
		} else {
			sizeInByte = file.length();
		}
	}

	public FileSnap(String absolutePath, long sizeInByte) {
		this.absolutePath = absolutePath;
		this.sizeInByte = sizeInByte;
	}

	public File[] listFiles() {
		return subFiles;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public File[] getSubFiles() {
		return subFiles;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public String getAbsolutPathInLowerCase() {
		return absolutPathInLowerCase;
	}

	public long getSizeInByte() {
		return sizeInByte;
	}
	
	public double getSizeKiloByte() {
		return sizeInByte / 1024d;
	}

	public double getSizeMegaByte() {
		return sizeInByte / 1024d / 1024d;
	}

	public double getSizeInGigaByte() {
		return sizeInByte / 1024d / 1024d / 1024d;
	}

}
