package com.msxichen.diskscanner.model;

import java.util.HashMap;
import java.util.Map;

public class DirectoryNode {

	private String absolutePath;
	private long sizeInByte;
	private Map<String, DirectoryNode> children;

	public DirectoryNode(String absolutePath) {
		this.absolutePath = absolutePath;
		this.children = new HashMap<String, DirectoryNode>();
	}
	
	public DirectoryNode(String absolutePath, long sizeInByte) {
		this.absolutePath = absolutePath;
		this.sizeInByte = sizeInByte;
		this.children = new HashMap<String, DirectoryNode>();
	}
	
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Path: ").append(absolutePath).append("\r\n");
		sb.append("Size: ").append(sizeInByte / 1024d / 1024d).append("mb");
		return sb.toString();
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

	public Map<String, DirectoryNode> getChildern() {
		return children;
	}

}
