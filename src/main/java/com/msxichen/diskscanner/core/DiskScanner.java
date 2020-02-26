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

import com.msxichen.diskscanner.core.model.DirectoryTree;
import com.msxichen.diskscanner.core.model.FileSnap;
import com.msxichen.diskscanner.core.model.ScanConfiguration;
import com.msxichen.diskscanner.io.DefaultScanResultWriter;
import com.msxichen.diskscanner.io.Utilities;

public class DiskScanner {

	private ScanConfiguration configuration;

	private PriorityBlockingQueue<FileSnap> fileQueue;
	private DirectoryTree dirTree;
	private AtomicLong fileCount;
	private AtomicLong dirCount;
	private Pattern[] excludedPatterns;

	private ExecutorService consumerPool;
	private BlockingQueue<FileSnap> candidates;

	private long fileQueueSize = DEFAULT_FILE_QUEUE_SIZE;

	private static final long DEFAULT_FILE_QUEUE_SIZE = 1000;

	public DiskScanner(ScanConfiguration config) {
		this.configuration = config;
	}

	public void scan() throws IOException {

		initialize();

		// For case like: C:. It cannot be recognized.
		String base = configuration.getBaseDir().endsWith(":") ? configuration.getBaseDir() + "\\"
				: configuration.getBaseDir();

		File file = new File(base);
		candidates.offer(new FileSnap(file));
		for (int i = 0; i < configuration.getThreadNum(); i++) {
			consumerPool.submit(new FileProcessor(candidates, fileQueue, dirTree, fileCount, dirCount, excludedPatterns,
					fileQueueSize));
		}

		long startTime = System.currentTimeMillis();

		int emptyQueueCount = 0;
		while (true) {
			System.out.println("Candidate queue size: " + candidates.size());
			if (candidates.size() == 0) {
				emptyQueueCount++;
			} else {
				emptyQueueCount = 0;
			}
			if (emptyQueueCount == 3) {
				System.out.println("Candidate queue keeps empty. Finish!");
				long endTime = System.currentTimeMillis();

				consumerPool.shutdownNow();

				writeResult(startTime, endTime);
				break;
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void initialize() {
		consumerPool = Executors.newFixedThreadPool(configuration.getThreadNum());

		fileQueue = new PriorityBlockingQueue<FileSnap>(100, new FileSnapSizeComparator());
		candidates = new LinkedBlockingQueue<FileSnap>();

		fileCount = new AtomicLong();
		dirCount = new AtomicLong();

		dirTree = new DirectoryTree(configuration.getBaseDir());
		fileQueueSize = configuration.getFileTopCount() <= 0 ? DEFAULT_FILE_QUEUE_SIZE
				: configuration.getFileTopCount();

		if (configuration.getExcludedPaths() != null) {
			excludedPatterns = new Pattern[configuration.getExcludedPaths().length];
			for (int i = 0; i < configuration.getExcludedPaths().length; i++) {
				String regex = Utilities.wildcardToRegex(configuration.getExcludedPaths()[i]);
				Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
				excludedPatterns[i] = p;
			}
		}
	}

	private void writeResult(long startTime, long endTime) {
		try (DefaultScanResultWriter writer = new DefaultScanResultWriter(configuration)) {
			writer.writeSummery(endTime - startTime, fileCount.longValue(), dirCount.longValue(), dirTree);
			writer.writeDirectoryInfo(dirTree);
			writer.writeFileInfo(fileQueue);
		} catch (Exception e) {
			e.printStackTrace();
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