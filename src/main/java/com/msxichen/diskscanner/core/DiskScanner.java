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

		int emptyWorkingPathSetCount = 0;
		while (true) {
			LOGGER.trace("Candidate queue size: " + candidates.size());
			LOGGER.trace("Working path set size: " + workingPaths.size());
			
			if (workingPaths.size() == 0) {
				emptyWorkingPathSetCount++;
			} else {
				emptyWorkingPathSetCount = 0;
			}
			if (emptyWorkingPathSetCount == EMPTY_WORKING_PATH_SET_AWAIT_COUNT) {
				LOGGER.trace("Working path set keeps empty. Finish!");
				onFinish();
				break;
			}

			try {
				TimeUnit.MILLISECONDS.sleep(SCAN_PROGRESS_POLLING_INTERVAL_MILLISECOND);
			} catch (InterruptedException e) {
				LOGGER.error(e);
			}
		}
	}

}