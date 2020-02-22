package com.msxichen.diskscanner.core;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import com.msxichen.diskscanner.core.model.DirectoryTree;
import com.msxichen.diskscanner.core.model.FileSnap;

public class FileProcessor implements Runnable {

	private PriorityBlockingQueue<FileSnap> fileQueue;
	private DirectoryTree dirTree;
	private BlockingQueue<FileSnap> candidates;
	private AtomicLong fileCount;
	private AtomicLong dirCount;
	private String[] excludedDirs;
	private long fileQueueSize;

	public FileProcessor(BlockingQueue<FileSnap> candidates, PriorityBlockingQueue<FileSnap> fileQueue,
			DirectoryTree dirTree, AtomicLong fileCount, AtomicLong dirCount,
			String[] excludedDirs, long fileQueueSize) {
		this.fileQueue = fileQueue;
		this.dirTree = dirTree;
		this.candidates = candidates;
		this.fileCount = fileCount;
		this.dirCount = dirCount;
		this.excludedDirs = excludedDirs == null ? new String[0] : excludedDirs;
		this.fileQueueSize = fileQueueSize;
	}

	public void run() {
		while (true) {
			FileSnap file = null;
			try {
				file = candidates.take();
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

	private void processDirectory(FileSnap dir) {
		boolean shouldExclude = Arrays.stream(excludedDirs)
				.anyMatch((exDir) -> dir.getAbsolutPathInLowerCase().equalsIgnoreCase(exDir));
		if (shouldExclude) {
			return;
		}
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