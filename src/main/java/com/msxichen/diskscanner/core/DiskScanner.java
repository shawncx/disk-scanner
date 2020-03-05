package com.msxichen.diskscanner.core;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.msxichen.diskscanner.core.model.DirectoryTree;
import com.msxichen.diskscanner.core.model.FileSnap;
import com.msxichen.diskscanner.core.model.ScanContext;
import com.msxichen.diskscanner.io.DefaultScanResultWriter;
import com.msxichen.diskscanner.io.Utilities;

public class DiskScanner {

	private PriorityBlockingQueue<FileSnap> fileQueue;
	private DirectoryTree dirTree;
	private AtomicLong fileCount;
	private AtomicLong dirCount;
	private Pattern[] excludedPatterns;

	private ExecutorService consumerPool;
	private BlockingQueue<FileSnap> candidates;

	private long fileQueueSize = DEFAULT_FILE_QUEUE_SIZE;

	private static final Logger LOGGER = LogManager.getLogger();

	private static final long DEFAULT_FILE_QUEUE_SIZE = 1000;
	private static final int EMPTY_QUEUE_WAIT_COUNT = 3;
	private static final long QUEUE_POLLING_INTERVAL_MILLISECOND = 1000;

	public void scan(ScanContext context) throws IOException {
		
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

				writeResult(context, System.currentTimeMillis());
				break;
			}

			try {
				Thread.sleep(QUEUE_POLLING_INTERVAL_MILLISECOND);
			} catch (InterruptedException e) {
				LOGGER.error(e);
			}
		}
	}

	private void initialize(ScanContext context) {
		consumerPool = Executors.newFixedThreadPool(context.getThreadNum());

		fileQueue = new PriorityBlockingQueue<FileSnap>(100, new FileSnapSizeComparator());
		candidates = new LinkedBlockingQueue<FileSnap>();

		fileCount = new AtomicLong();
		dirCount = new AtomicLong();

		dirTree = new DirectoryTree(context.getBaseDir());
		fileQueueSize = context.getFileTopCount() <= 0 ? DEFAULT_FILE_QUEUE_SIZE : context.getFileTopCount();

		excludedPatterns = new Pattern[context.getExcludedPaths().length];
		for (int i = 0; i < context.getExcludedPaths().length; i++) {
			String regex = Utilities.wildcardToRegex(context.getExcludedPaths()[i]);
			Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			excludedPatterns[i] = p;
		}

		File baseDir = new File(context.getBaseDir());
		candidates.offer(new FileSnap(baseDir));
		for (int i = 0; i < context.getThreadNum(); i++) {
			consumerPool.submit(new FileProcessor(candidates, fileQueue, dirTree, fileCount, dirCount, excludedPatterns,
					fileQueueSize));
		}
	}

	private void writeResult(ScanContext context, long endTime) {
		try (DefaultScanResultWriter writer = new DefaultScanResultWriter(context, endTime)) {
			writer.writeSummery(fileCount.longValue(), dirCount.longValue(), dirTree);
			writer.writeDirectoryInfo(dirTree);
			writer.writeFileInfo(fileQueue);
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private class FileSnapSizeComparator implements Comparator<FileSnap> {

		@Override
		public int compare(FileSnap o1, FileSnap o2) {
			long diff = o1.getSizeInByte() - o2.getSizeInByte();
			if (diff > 0) {
				return 1;
			} else if (diff < 0) {
				return -1;
			} else {
				return 0;
			}
		}
	}
}