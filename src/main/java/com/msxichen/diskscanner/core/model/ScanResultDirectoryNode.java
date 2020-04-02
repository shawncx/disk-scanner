package com.msxichen.diskscanner.core.model;

import java.util.ArrayList;
import java.util.List;

public class ScanResultDirectoryNode {

	private String absolutePath;
	private long sizeInByte;
	private List<ScanResultDirectoryNode> children = new ArrayList<ScanResultDirectoryNode>();

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	public long getSizeInByte() {
		return sizeInByte;
	}

	public void setSizeInByte(long sizeInByte) {
		this.sizeInByte = sizeInByte;
	}

	public List<ScanResultDirectoryNode> getChildren() {
		return children;
	}

	public void setChildren(List<ScanResultDirectoryNode> children) {
		this.children = children;
	}

}
