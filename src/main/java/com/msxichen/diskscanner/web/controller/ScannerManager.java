package com.msxichen.diskscanner.web.controller;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.msxichen.diskscanner.core.DiskScannerAsync;

public enum ScannerManager {

	INSTANCE;

	private ConcurrentHashMap<String, DiskScannerAsync> map;

	private ScannerManager() {
		map = new ConcurrentHashMap<String, DiskScannerAsync>();
	}

	public String addScanner(DiskScannerAsync scanner) {
		String uuid = UUID.randomUUID().toString();
		map.put(uuid, scanner);
		return uuid;
	}

	public DiskScannerAsync getScanner(String uuid) {
		return map.getOrDefault(uuid, null);
	}

}
