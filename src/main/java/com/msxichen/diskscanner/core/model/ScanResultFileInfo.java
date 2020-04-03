package com.msxichen.diskscanner.core.model;

import java.util.ArrayList;
import java.util.List;

public class ScanResultFileInfo {

	private List<ScanResultFile> files = new ArrayList<ScanResultFile>();

	public ScanResultFileInfo() {
		super();
	}

	public List<ScanResultFile> getFiles() {
		return files;
	}

	public void setFiles(List<ScanResultFile> files) {
		this.files = files;
	}

}
