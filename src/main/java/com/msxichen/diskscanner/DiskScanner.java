package com.msxichen.diskscanner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import com.msxichen.diskscanner.model.DirectoryNode;
import com.msxichen.diskscanner.model.DirectoryTree;
import com.msxichen.diskscanner.model.DirectoryTreeLevelVisitor;
import com.msxichen.diskscanner.model.DirectoryTreeNodeVisitor;
import com.msxichen.diskscanner.model.ScanConfiguration;

public class DiskScanner {

	private ScanConfiguration configuration;

	private PriorityBlockingQueue<FileSnap> fileQueue;
	private DirectoryTree dirTree;
	private AtomicLong fileCount;
	private AtomicLong dirCount;

	private ExecutorService consumerPool;
	private BlockingQueue<FileSnap> candidates;

	public DiskScanner(ScanConfiguration config) {
		this.configuration = config;
	}

	public void scan() throws IOException {

		initialize();

		File file = new File(configuration.getBaseDir());
		candidates.offer(new FileSnap(file));
		for (int i = 0; i < configuration.getThreadNum(); i++) {
			consumerPool.submit(new FileProcessor(candidates, fileQueue, dirTree, fileCount, dirCount,
					configuration.getExcludedPaths()));
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

				BufferedWriter fileWriter = null;
				boolean consoleOutput = false;
				for (String type : configuration.getOutputTypes()) {
					if (ScanConfiguration.OUTPUT_TYPE_FILE.equalsIgnoreCase(type)) {
						fileWriter = new BufferedWriter(new FileWriter(configuration.getOutputFile()));
					} else if (ScanConfiguration.OUTPUT_TYPE_CONSOLE.equalsIgnoreCase(type)) {
						consoleOutput = true;
					}
				}
				writeLine(consoleOutput, fileWriter,
						"File count: " + fileCount.longValue() + ", " + "Directory count: " + dirCount.longValue());
				writeLine(consoleOutput, fileWriter,
						"Time cost: " + (System.currentTimeMillis() - startTime) / 1000 + " seconds.");
				dirTree.visitTreeBFS(new DirectoryTreeLevelWriter(consoleOutput, fileWriter),
						new DirectoryTreeNodeWriter(consoleOutput, fileWriter));
				if (fileWriter != null) {
					fileWriter.close();
				}
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

	private void writeLine(final boolean consoleOutput, final BufferedWriter fileWriter, final String message)
			throws IOException {
		if (consoleOutput) {
			System.out.println(message);
		}
		if (fileWriter != null) {
			fileWriter.append(message).append("\r\n");
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

	private class DirectoryTreeLevelWriter implements DirectoryTreeLevelVisitor {

		private boolean consoleOutput;
		private BufferedWriter fileWriter;

		public DirectoryTreeLevelWriter(boolean consoleOutput, BufferedWriter fileWriter) {
			super();
			this.consoleOutput = consoleOutput;
			this.fileWriter = fileWriter;
		}

		@Override
		public void visit(int depth) {
			String message = "******Level " + depth + "******\r\n";
			if (consoleOutput) {
				System.out.print(message);
			}
			if (fileWriter != null) {
				try {
					fileWriter.append(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private class DirectoryTreeNodeWriter implements DirectoryTreeNodeVisitor {

		private boolean consoleOutput;
		private BufferedWriter fileWriter;

		public DirectoryTreeNodeWriter(boolean consoleOutput, BufferedWriter fileWriter) {
			super();
			this.consoleOutput = consoleOutput;
			this.fileWriter = fileWriter;
		}

		@Override
		public void visit(DirectoryNode node) {
			String message = node.toString() + "\r\n";
			if (consoleOutput) {
				System.out.print(message);
			}
			if (fileWriter != null) {
				try {
					fileWriter.append(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}