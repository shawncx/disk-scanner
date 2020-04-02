package com.msxichen.diskscanner.web.entity;

import com.msxichen.diskscanner.core.model.ScanResultFileInfo;

public class GetFileInfoResponse {

	private ScanResultFileInfo info;

	public GetFileInfoResponse() {
		super();
	}

	public GetFileInfoResponse(ScanResultFileInfo info) {
		super();
		this.info = info;
	}

	public ScanResultFileInfo getInfo() {
		return info;
	}

	public void setInfo(ScanResultFileInfo info) {
		this.info = info;
	}

}
