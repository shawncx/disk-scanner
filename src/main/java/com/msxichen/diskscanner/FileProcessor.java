package com.msxichen.diskscanner;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import com.msxichen.diskscanner.model.DirectoryTree;

public class FileProcessor implements Runnable {

	private PriorityBlockingQueue<FileSnap> fileQueue;
	private DirectoryTree dirTree;
	private BlockingQueue<FileSnap> candidates;
	private AtomicLong fileCount;
	private AtomicLong dirCount;
	private String[] excludedDirs;

	public FileProcessor(BlockingQueue<FileSnap> candidates, PriorityBlockingQueue<FileSnap> fileQueue,
			DirectoryTree dirTree, AtomicLong fileCount, AtomicLong dirCount,
			String[] excludedDirs) {
		this.fileQueue = fileQueue;
		this.dirTree = dirTree;
		this.candidates = candidates;
		this.fileCount = fileCount;
		this.dirCount = dirCount;
		this.excludedDirs = excludedDirs == null ? new String[0] : excludedDirs;
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
			}
		}
	}

	private void processDirectory(FileSnap dir) {
		boolean shouldExclude = Arrays.stream(excludedDirs)
				.anyMatch((exDir) -> dir.getAbsolutPathInLowerCase().equalsIgnoreCase(exDir));
		if (shouldExclude) {
			return;
		}
		Arrays.stream(dir.listFiles()).forEach((file) -> candidates.offer(new FileSnap(file)));
	}

	private void processFile(FileSnap file) {
		fileQueue.offer(file);
		if (fileQueue.size() > 100) {
			fileQueue.poll();
		}

		int dirIndex = file.getAbsolutePath().lastIndexOf('\\');
		String path = file.getAbsolutePath().substring(0, dirIndex);
		dirTree.increaseSizeDescade(path, file.getSizeInByte());
	}
}