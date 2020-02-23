package com.msxichen.diskscanner.core;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import com.msxichen.diskscanner.core.model.DirectoryTree;
import com.msxichen.diskscanner.core.model.FileSnap;

public class FileProcessor implements Runnable {

	private PriorityBlockingQueue<FileSnap> fileQueue;
	private DirectoryTree dirTree;
	private BlockingQueue<FileSnap> candidates;
	private AtomicLong fileCount;
	private AtomicLong dirCount;
	private Pattern[] excludedPatterns;
	private long fileQueueSize;

	public FileProcessor(BlockingQueue<FileSnap> candidates, PriorityBlockingQueue<FileSnap> fileQueue,
			DirectoryTree dirTree, AtomicLong fileCount, AtomicLong dirCount,
			Pattern[] excludedPatterns, long fileQueueSize) {
		this.fileQueue = fileQueue;
		this.dirTree = dirTree;
		this.candidates = candidates;
		this.fileCount = fileCount;
		this.dirCount = dirCount;
		this.excludedPatterns = excludedPatterns == null ? new Pattern[0] : excludedPatterns;
		this.fileQueueSize = fileQueueSize;
	}

	public void run() {
		while (true) {
			FileSnap file = null;
			try {
				file = candidates.take();
				if (isExcluded(file)) {
					continue;
				}
				
				if (file.isDirectory()) {
					dirCount.incrementAndGet();
					processDirectory(file);
				} else {
					fileCount.incrementAndGet();
					processFile(file);
				}
			} catch (InterruptedException e) {
//				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean isExcluded(FileSnap file) {
		for (Pattern pattern : excludedPatterns) {
			if (pattern.matcher(file.getAbsolutePath()).matches()) {
				return true;
			}
		}
		return false;
	}

	private void processDirectory(FileSnap dir) {
		File[] subFiles = dir.listFiles();
		if (subFiles != null) {
			for (File file : dir.listFiles()) {
				candidates.offer(new FileSnap(file));
			}
		}
	}

	private void processFile(FileSnap file) {
		fileQueue.offer(file);
		if (fileQueue.size() > fileQueueSize) {
			fileQueue.poll();
		}
		dirTree.increaseSizeDescade(file.getAbsolutePath(), file.getSizeInByte());

	}
}