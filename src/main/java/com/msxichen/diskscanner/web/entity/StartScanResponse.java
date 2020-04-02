package com.msxichen.diskscanner.web.entity;

public class StartScanResponse {

	private boolean isSucceeded;
	private String uuid;
	private String message;

	public StartScanResponse() {
		super();
	}

	public StartScanResponse(boolean isSucceeded, String uuid, String message) {
		super();
		this.isSucceeded = isSucceeded;
		this.uuid = uuid;
		this.message = message;
	}

	public boolean isSucceeded() {
		return isSucceeded;
	}

	public void setSucceeded(boolean isSucceeded) {
		this.isSucceeded = isSucceeded;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
