package com.msxichen.diskscanner.web.entity;

import com.msxichen.diskscanner.core.model.ScanResultDirectoryInfo;

public class GetDirectoryInfoResponse {

	private ScanResultDirectoryInfo info;

	public GetDirectoryInfoResponse() {
		super();
	}

	public GetDirectoryInfoResponse(ScanResultDirectoryInfo info) {
		super();
		this.info = info;
	}

	public ScanResultDirectoryInfo getInfo() {
		return info;
	}

	public void setInfo(ScanResultDirectoryInfo info) {
		this.info = info;
	}

}
