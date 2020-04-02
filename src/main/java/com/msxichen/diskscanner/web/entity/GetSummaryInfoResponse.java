package com.msxichen.diskscanner.web.entity;

import com.msxichen.diskscanner.core.model.ScanResultSummaryInfo;

public class GetSummaryInfoResponse {

	private ScanResultSummaryInfo info;

	public GetSummaryInfoResponse() {
		super();
	}

	public GetSummaryInfoResponse(ScanResultSummaryInfo info) {
		super();
		this.info = info;
	}

	public ScanResultSummaryInfo getInfo() {
		return info;
	}

	public void setInfo(ScanResultSummaryInfo info) {
		this.info = info;
	}

}
