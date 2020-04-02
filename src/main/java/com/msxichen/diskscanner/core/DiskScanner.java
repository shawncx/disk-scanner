package com.msxichen.diskscanner.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.msxichen.diskscanner.core.model.ScanContext;

public class DiskScanner extends AbsDiskScanner {
	
	private long endTime;

	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void scan(ScanContext context) {
		LOGGER.trace("Start scan");
		initialize(context);

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
				consumerPool.shutdownNow();
				
				endTime = System.currentTimeMillis();
				break;
			}

			try {
				Thread.sleep(QUEUE_POLLING_INTERVAL_MILLISECOND);
			} catch (InterruptedException e) {
				LOGGER.error(e);
			}
		}
	}
	
	@Override
	protected long getEndTime() {
		return endTime;
	}

}