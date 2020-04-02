package com.msxichen.diskscanner.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.msxichen.diskscanner.core.model.ScanContext;
import com.msxichen.diskscanner.core.model.ScanProgress;

public class DiskScannerAsync extends AbsDiskScanner {

	private ScanProgress progress;
	private long endTime;

	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void scan(ScanContext context) {
		LOGGER.trace("Start scan");
		progress = new ScanProgress();
		initialize(context);

		new Thread(new ScanMonitor(progress)).start();
	}

	public ScanProgress getProgress() {
		return progress;
	}

	@Override
	protected long getEndTime() {
		return endTime;
	}

	private class ScanMonitor implements Runnable {

		private ScanProgress progress;

		public ScanMonitor(ScanProgress progress) {
			super();
			this.progress = progress;
		}

		@Override
		public void run() {
			int emptyQueueCount = 0;
			while (true) {
				LOGGER.trace("Candidate queue size: " + candidates.size());
				progress.setCandidateInQueue(candidates.size());
				if (candidates.size() == 0) {
					emptyQueueCount++;
				} else {
					emptyQueueCount = 0;
				}
				if (emptyQueueCount == EMPTY_QUEUE_WAIT_COUNT) {
					LOGGER.trace("Candidate queue keeps empty. Finish!");
					consumerPool.shutdownNow();
					progress.setDone(true);
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
	}
}