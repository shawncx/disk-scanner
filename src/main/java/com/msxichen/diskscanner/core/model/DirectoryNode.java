package com.msxichen.diskscanner.core.model;

import java.util.concurrent.ConcurrentHashMap;

public class DirectoryNode {

	private String absolutePath;
	private long sizeInByte;
	private boolean isDirectory;
	private String extension;
	private ConcurrentHashMap<String, DirectoryNode> children;

	public DirectoryNode(String absolutePath, boolean isDirectory, String extension) {
		this.absolutePath = absolutePath;
		this.isDirectory = isDirectory;
		this.extension = extension;
		this.children = new ConcurrentHashMap<String, DirectoryNode>();
	}

	public DirectoryNode(String absolutePath, long sizeInByte, boolean isDirectory, String extension) {
		this.absolutePath = absolutePath;
		this.sizeInByte = sizeInByte;
		this.isDirectory = isDirectory;
		this.extension = extension;
		this.children = new ConcurrentHashMap<String, DirectoryNode>();
	}

	public synchronized void increaseSizeInByte(long sizeInByte) {
		this.sizeInByte += sizeInByte;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public ConcurrentHashMap<String, DirectoryNode> getChildern() {
		return children;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public long getSizeInByte() {
		return sizeInByte;
	}

	public String getExtension() {
		return extension;
	}


}
