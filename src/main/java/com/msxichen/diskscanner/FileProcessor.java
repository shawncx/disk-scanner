package com.msxichen.diskscanner;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class FileProcessor implements Runnable {

	private String baseDir;
	private PriorityBlockingQueue<FileSnap> fileQueue;
	private ConcurrentHashMap<String, Long> dirSizeMap;
	private BlockingQueue<FileSnap> candidates;
	private AtomicLong fileCount;
	private AtomicLong dirCount;
	private String[] excludedDirs;

	public FileProcessor(String baseDir, BlockingQueue<FileSnap> candidates, PriorityBlockingQueue<FileSnap> fileQueue,
			ConcurrentHashMap<String, Long> dirSizeMap, AtomicLong fileCount, AtomicLong dirCount,
			String[] excludedDirs) {
		this.baseDir = baseDir;
		this.fileQueue = fileQueue;
		this.dirSizeMap = dirSizeMap;
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

		String path = file.getAbsolutePath();
		String[] pathSegs = path.split("\\\\");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < pathSegs.length - 1; i++) {
			if (i > 0) {
				sb.append("\\");
			}
			sb.append(pathSegs[i]);
			if (sb.length() < baseDir.length()) {
				continue;
			}
			dirSizeMap.compute(sb.toString(),
					(key, value) -> value == null ? file.getSizeInByte() : value + file.getSizeInByte());
		}
	}
}