package com.msxichen.diskscanner.core.model;

import java.util.ArrayList;
import java.util.List;

public class ScanResultFileInfo {

	private List<FileSnap> files = new ArrayList<FileSnap>();

	public ScanResultFileInfo() {
		super();
	}

	public List<FileSnap> getFiles() {
		return files;
	}

	public void setFiles(List<FileSnap> files) {
		this.files = files;
	}

}
