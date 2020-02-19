package com.msxichen.diskscanner;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class DiskScanner {

	private PriorityBlockingQueue<FileSnap> fileQueue;
	private ConcurrentHashMap<String, Long> dirSizeMap;
	private AtomicLong fileCount;
	private AtomicLong dirCount;

	private ExecutorService consumerPool;
	private BlockingQueue<FileSnap> candidates;
	private int threadNum;

	public DiskScanner(int threadNum) {
		this.threadNum = threadNum;
		consumerPool = Executors.newFixedThreadPool(threadNum);

		fileQueue = new PriorityBlockingQueue<FileSnap>(100, new FileSnapSizeComparator());
		candidates = new LinkedBlockingQueue<FileSnap>();
		dirSizeMap = new ConcurrentHashMap<String, Long>();
		
		fileCount = new AtomicLong();
		dirCount = new AtomicLong();
	}

	public void scan(String basePath, String[] excludedDirs) {
		File file = new File(basePath);
		candidates.offer(new FileSnap(file));
		for (int i = 0; i < threadNum; i++) {
			consumerPool.submit(new FileProcessor(basePath, candidates, fileQueue, dirSizeMap, fileCount, dirCount, excludedDirs));
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
				consumerPool.shutdownNow();
				System.out.println("File count: " + fileCount.longValue() + ", " + "Directory count: " + dirCount.longValue());
				System.out.println("Time cost: " + (System.currentTimeMillis() - startTime) / 1000 + " seconds.");
				printTopSizeDirectory(100);
				printTopSizeFile(100);
				break;
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private void printTopSizeFile(int top) {
		System.out.println("*********************************************************");
		System.out.println("Top " + top + " size File: ");
		List<FileSnap> list = new ArrayList<>();
		while (!fileQueue.isEmpty()) {
			list.add(fileQueue.poll());
		}

		for (int i = 0; i < top && list.size() - i - 1 >= 0; i++) {
			FileSnap file = list.get(list.size() - i - 1);
			System.out.println(file.getAbsolutePath() + ", size: " + file.getSizeMegaByte() + "mb");
		}
		System.out.println("*********************************************************");
	}

	private void printTopSizeDirectory(int top) {
		System.out.println("*********************************************************");
		System.out.println("Top " + top + " size Directory: ");
		PriorityQueue<FileSnap> queue = new PriorityQueue<>(10000, new DirectorySizeComparator());
		dirSizeMap.keySet().forEach((path) -> queue.offer(new FileSnap(path, dirSizeMap.getOrDefault(path, 0l))));
		int i = 0;
		while (i < top && !queue.isEmpty()) {
			FileSnap dir = queue.poll();
			System.out.println(dir.getAbsolutePath() + ", size: " + dir.getSizeMegaByte() + "mb");
			i++;
		}
		System.out.println("*********************************************************");
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

	private class DirectorySizeComparator implements Comparator<FileSnap> {

		@Override
		public int compare(FileSnap o1, FileSnap o2) {
			long diff = o2.getSizeInByte() - o1.getSizeInByte();
			if (diff > 0) {
				return 1;
			} else if (diff < 0) {
				return -1;
			} else {
				return o2.getAbsolutePath().length() - o1.getAbsolutePath().length();
			}
		}
	}

}