package com.msxichen.diskscanner.web.controller;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.msxichen.diskscanner.core.DiskScannerAsync;
import com.msxichen.diskscanner.core.model.ScanResultSummaryInfo;

public enum ScannerManager {

	INSTANCE;

	private ConcurrentHashMap<String, DiskScannerAsync> map;

	private static final long CLEANER_EXEC_INTERVAL_IN_MIN = 30;
	private static final Logger LOGGER = LogManager.getLogger();

	private ScannerManager() {
		map = new ConcurrentHashMap<String, DiskScannerAsync>();
		new Thread(new Cleaner(map)).start();
	}

	public String addScanner(DiskScannerAsync scanner) {
		String uuid = UUID.randomUUID().toString();
		map.put(uuid, scanner);
		return uuid;
	}

	public DiskScannerAsync getScanner(String uuid) {
		return map.getOrDefault(uuid, null);
	}

	private static class Cleaner implements Runnable {

		private ConcurrentHashMap<String, DiskScannerAsync> map;

		public Cleaner(ConcurrentHashMap<String, DiskScannerAsync> map) {
			super();
			this.map = map;
		}

		@Override
		public void run() {
			while (true) {
				Set<String> keys = map.keySet();
				for (String key : keys) {
					DiskScannerAsync scanner = map.get(key);
					if (scanner == null || !scanner.getProgress().isDone()) {
						continue;
					}
					ScanResultSummaryInfo summary = scanner.getScanResult().getSummaryInfo();
					if (Duration.between(summary.getEndInstant(), Instant.now())
							.toMinutes() > CLEANER_EXEC_INTERVAL_IN_MIN) {
						LOGGER.info("Scanner " + key + " is done over 30 mins, remove from cache");
						map.remove(key);
					}
				}
				try {
					TimeUnit.MINUTES.sleep(CLEANER_EXEC_INTERVAL_IN_MIN);
				} catch (InterruptedException e) {
					LOGGER.error(e);
				}
			}
		}

	}

}
