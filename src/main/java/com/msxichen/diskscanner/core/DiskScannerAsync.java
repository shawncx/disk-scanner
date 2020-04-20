package com.msxichen.diskscanner.core;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.msxichen.diskscanner.core.model.ScanContext;
import com.msxichen.diskscanner.core.model.ScanProgress;

public class DiskScannerAsync extends AbsDiskScanner {

	private Boolean isDone = false;

	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void scan(ScanContext context) {
		LOGGER.trace("Start scan");
		onInitialize(context);
		onLaunchScan(context);

		new Thread(new ScanMonitor()).start();
	}

	@Override
	protected void onInitialize(ScanContext context) {
		super.onInitialize(context);
		isDone = false;
	}

	@Override
	protected void onFinish() {
		super.onFinish();
		isDone = true;
	}

	public ScanProgress getProgress() {
		ScanProgress progress = new ScanProgress();
		progress.setDone(isDone);
		progress.setCandidateInQueue(candidates.size());
		progress.setDirProcessed(dirCount.longValue());
		progress.setFileProcessed(fileCount.longValue());
		progress.setTimeCostInSecond(Duration.between(startInstant, Instant.now()).getSeconds());
		return progress;
	}

	private class ScanMonitor implements Runnable {

		@Override
		public void run() {
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
}