package com.msxichen.diskscanner.web.entity;

import com.msxichen.diskscanner.core.model.ScanProgress;

public class GetScanProgressResponse {

	private ScanProgress progress;

	public GetScanProgressResponse() {
		super();
	}

	public GetScanProgressResponse(ScanProgress progress) {
		super();
		this.progress = progress;
	}

	public ScanProgress getProgress() {
		return progress;
	}

	public void setProgress(ScanProgress progress) {
		this.progress = progress;
	}

}
