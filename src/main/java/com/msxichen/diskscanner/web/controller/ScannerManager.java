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

public enum ScannerManager {

	INSTANCE;

	private ConcurrentHashMap<String, Pair<Instant, DiskScannerAsync>> map;

	private static final long CLEANER_EXEC_INTERVAL_IN_MIN = 5;
	private static final Logger LOGGER = LogManager.getLogger();

	private ScannerManager() {
		map = new ConcurrentHashMap<String, Pair<Instant, DiskScannerAsync>>();
		new Thread(new Cleaner(map)).start();
	}

	public String addScanner(DiskScannerAsync scanner) {
		String uuid = UUID.randomUUID().toString();
		map.put(uuid, new Pair<Instant, DiskScannerAsync>(Instant.now(), scanner));
		return uuid;
	}

	public DiskScannerAsync getScanner(String uuid) {
		return map.containsKey(uuid) ? map.get(uuid).t2: null;
	}

	private static class Cleaner implements Runnable {

		private ConcurrentHashMap<String, Pair<Instant, DiskScannerAsync>> map;

		public Cleaner(ConcurrentHashMap<String, Pair<Instant, DiskScannerAsync>> map) {
			super();
			this.map = map;
		}

		@Override
		public void run() {
			while (true) {
				Set<String> keys = map.keySet();
				for (String key : keys) {
					Pair<Instant, DiskScannerAsync> pair = map.get(key);
					if (pair == null || pair.t2 == null || !pair.t2.getProgress().isDone()) {
						continue;
					}
					if (Duration.between(pair.t1, Instant.now())
							.toMinutes() > CLEANER_EXEC_INTERVAL_IN_MIN) {
						LOGGER.info("Scanner " + key + " is done over 5 mins, remove from cache");
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
	
	private static class Pair<T1, T2> {
		public T1 t1;
		public T2 t2;
		
		public Pair(T1 t1, T2 t2) {
			this.t1 = t1;
			this.t2 = t2;
		}
	}

}
