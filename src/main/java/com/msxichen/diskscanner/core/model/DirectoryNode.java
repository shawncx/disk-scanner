package com.msxichen.diskscanner.core.model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DirectoryNode {

	private String absolutePath;
	private long sizeInByte;
	private ConcurrentHashMap<String, DirectoryNode> children;

	public DirectoryNode(String absolutePath) {
		this.absolutePath = absolutePath;
		this.children = new ConcurrentHashMap<String, DirectoryNode>();
	}
	
	public DirectoryNode(String absolutePath, long sizeInByte) {
		this.absolutePath = absolutePath;
		this.sizeInByte = sizeInByte;
		this.children = new ConcurrentHashMap<String, DirectoryNode>();
	}

	public synchronized void increaseSizeInByte(long sizeInByte) {
		this.sizeInByte += sizeInByte;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public long getSizeInByte() {
		return sizeInByte;
	}

	public ConcurrentHashMap<String, DirectoryNode> getChildern() {
		return children;
	}

}
