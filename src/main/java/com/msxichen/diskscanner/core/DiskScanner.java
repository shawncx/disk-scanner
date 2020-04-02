package com.msxichen.diskscanner.core;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.msxichen.diskscanner.core.model.ScanContext;

public class DiskScanner extends AbsDiskScanner {

	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void scan(ScanContext context) {
		LOGGER.trace("Start scan");
		onInitialize(context);
		onLaunchScan(context);

		int emptyQueueCount = 0;
		while (true) {
			LOGGER.trace("Candidate queue size: " + candidates.size());
			if (candidates.size() == 0) {
				emptyQueueCount++;
			} else {
				emptyQueueCount = 0;
			}
			if (emptyQueueCount == EMPTY_QUEUE_WAIT_COUNT) {
				LOGGER.trace("Candidate queue keeps empty. Finish!");
				onFinish();
				break;
			}

			try {
				TimeUnit.MILLISECONDS.sleep(QUEUE_POLLING_INTERVAL_MILLISECOND);
			} catch (InterruptedException e) {
				LOGGER.error(e);
			}
		}
	}

}