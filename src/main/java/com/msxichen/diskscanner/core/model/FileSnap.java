package com.msxichen.diskscanner.core.model;

import java.io.File;

import com.msxichen.diskscanner.io.Utilities;

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
			this.subFiles = file.listFiles();
		} else {
			sizeInByte = file.length();
		}
	}

	public FileSnap(String absolutePath, long sizeInByte) {
		this.absolutePath = absolutePath;
		this.sizeInByte = sizeInByte;
	}
	
	public String getExtension() {
		if (isDirectory) {
			return null;
		}
		return Utilities.getExtension(absolutePath);
	}

	public File[] listFiles() {
		return subFiles;
	}

	public boolean isDirectory() {
		return isDirectory;
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

}
