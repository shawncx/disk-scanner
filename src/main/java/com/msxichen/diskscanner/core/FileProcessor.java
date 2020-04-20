package com.msxichen.diskscanner.core;

import java.io.File;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.msxichen.diskscanner.core.model.DirectoryTree;
import com.msxichen.diskscanner.core.model.ExtensionItem;
import com.msxichen.diskscanner.core.model.FileSnap;

public class FileProcessor implements Runnable {

	private PriorityBlockingQueue<FileSnap> fileQueue;
	private DirectoryTree dirTree;
	private ConcurrentHashMap<String, ExtensionItem> extensionMap;
	private BlockingQueue<FileSnap> candidates;
	private AtomicLong fileCount;
	private AtomicLong dirCount;
	private Pattern[] excludedPatterns;
	private long fileQueueSize;
	private Set<String> workingSet;

	private static final Logger LOGGER = LogManager.getLogger();

	public FileProcessor(BlockingQueue<FileSnap> candidates, PriorityBlockingQueue<FileSnap> fileQueue,
			DirectoryTree dirTree, ConcurrentHashMap<String, ExtensionItem> extensionMap, AtomicLong fileCount,
			AtomicLong dirCount, Pattern[] excludedPatterns, long fileQueueSize, Set<String> workingPaths) {
		this.fileQueue = fileQueue;
		this.dirTree = dirTree;
		this.extensionMap = extensionMap;
		this.candidates = candidates;
		this.fileCount = fileCount;
		this.dirCount = dirCount;
		this.excludedPatterns = excludedPatterns == null ? new Pattern[0] : excludedPatterns;
		this.fileQueueSize = fileQueueSize;
		this.workingSet = workingPaths;
	}

	public void run() {
		LOGGER.trace("FileProcessor is started.");
		while (true) {
			FileSnap file = null;
			try {
				file = candidates.take();
				if (isExcluded(file)) {
					continue;
				}
				
				this.workingSet.add(file.getAbsolutePath());

				if (file.isDirectory()) {
					dirCount.incrementAndGet();
					processDirectory(file);
				} else {
					fileCount.incrementAndGet();
					processFile(file);
				}
			} catch (InterruptedException e) {
				LOGGER.trace("FileProcessor is interupted.");
			} catch (Exception e) {
				LOGGER.error(e);
			} finally {
				if (file != null) {
					this.workingSet.remove(file.getAbsolutePath());
				}
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
		if (subFiles == null || subFiles.length == 0) {
			// Add empty file into dir tree, or it will never appear
			dirTree.increaseSizeDescade(dir.getAbsolutePath(), 0, true);
		}
	}

	private void processFile(FileSnap file) {
		fileQueue.offer(file);
		if (fileQueue.size() > fileQueueSize) {
			fileQueue.poll();
		}
		dirTree.increaseSizeDescade(file.getAbsolutePath(), file.getSizeInByte(), false);
		
		String extension = file.getExtension();
		extensionMap.putIfAbsent(file.getExtension(), new ExtensionItem(extension));
		ExtensionItem item = extensionMap.getOrDefault(file.getExtension(), new ExtensionItem(extension));
		item.increaseCount(1);
		item.increaseSizeInByte(file.getSizeInByte());
		
	}
}