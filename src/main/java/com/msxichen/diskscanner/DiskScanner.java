package com.msxichen.diskscanner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import com.msxichen.diskscanner.model.DirectoryTree;

public class DiskScanner {

	private PriorityBlockingQueue<FileSnap> fileQueue;
	private DirectoryTree dirTree;
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

		fileCount = new AtomicLong();
		dirCount = new AtomicLong();
	}

	public void scan(String basePath, String[] excludedDirs) {
		dirTree = new DirectoryTree(basePath);

		File file = new File(basePath);
		candidates.offer(new FileSnap(file));
		for (int i = 0; i < threadNum; i++) {
			consumerPool.submit(new FileProcessor(candidates, fileQueue, dirTree, fileCount, dirCount, excludedDirs));
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
				System.out.println(
						"File count: " + fileCount.longValue() + ", " + "Directory count: " + dirCount.longValue());
				System.out.println("Time cost: " + (System.currentTimeMillis() - startTime) / 1000 + " seconds.");
				printDirectoryBFS();
				try {
					dirTree.wirteTreeBFS("D:\\myworkspace\\dirTree.txt");
				} catch (IOException e) {
					e.printStackTrace();
				}
//				printTopSizeFile(100);
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

	private void printDirectoryBFS() {
		System.out.println("*********************************************************");
		System.out.println("Directory size: ");
		dirTree.printTreeBFS();
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