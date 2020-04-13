package com.msxichen.diskscanner.core;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import com.msxichen.diskscanner.core.model.DirectoryNode;
import com.msxichen.diskscanner.core.model.DirectoryTree;
import com.msxichen.diskscanner.core.model.FileSnap;
import com.msxichen.diskscanner.core.model.OutputUnit;
import com.msxichen.diskscanner.core.model.ScanContext;
import com.msxichen.diskscanner.core.model.ScanResult;
import com.msxichen.diskscanner.core.model.ScanResultDirectoryInfo;
import com.msxichen.diskscanner.core.model.ScanResultDirectoryNode;
import com.msxichen.diskscanner.core.model.ScanResultFile;
import com.msxichen.diskscanner.core.model.ScanResultFileInfo;
import com.msxichen.diskscanner.core.model.ScanResultSummaryInfo;
import com.msxichen.diskscanner.io.Utilities;

public abstract class AbsDiskScanner {

	protected PriorityBlockingQueue<FileSnap> fileQueue;
	protected DirectoryTree dirTree;
	protected AtomicLong fileCount;
	protected AtomicLong dirCount;
	protected String[] excludedPaths;
	protected Pattern[] excludedPatterns;

	protected ExecutorService consumerPool;
	protected BlockingQueue<FileSnap> candidates;

	protected Instant startInstant;
	protected Instant endInstant;
	
	protected OutputUnit dirUnit;
	protected OutputUnit fileUnit;

	protected long fileQueueSize = DEFAULT_FILE_QUEUE_SIZE;

	protected static final long DEFAULT_FILE_QUEUE_SIZE = 1000;
	protected static final int EMPTY_QUEUE_WAIT_COUNT = 3;
	protected static final long QUEUE_POLLING_INTERVAL_MILLISECOND = 200;

	private ScanResult result;

	private static final ScanResultDirectoryNodeReverseComparator SCAN_RES_DIR_NODE_REV_COMP = new ScanResultDirectoryNodeReverseComparator();

	public abstract void scan(ScanContext context);

	public ScanResult getScanResult() {
		return result;
	}

	protected void onInitialize(ScanContext context) {
		result = null;

		consumerPool = Executors.newFixedThreadPool(context.getThreadNum());

		fileQueue = new PriorityBlockingQueue<FileSnap>(100, new FileSnapSizeComparator());
		candidates = new LinkedBlockingQueue<FileSnap>();

		fileCount = new AtomicLong();
		dirCount = new AtomicLong();

		startInstant = context.getStartInstant();
		
		dirUnit = context.getDirOutputUnit();
		fileUnit = context.getFileOutputUnit();

		dirTree = new DirectoryTree(context.getBaseDir(), true);
		fileQueueSize = context.getFileTopCount() <= 0 ? DEFAULT_FILE_QUEUE_SIZE : context.getFileTopCount();

		excludedPaths = context.getExcludedPaths();
		excludedPatterns = new Pattern[context.getExcludedPaths().length];
		for (int i = 0; i < context.getExcludedPaths().length; i++) {
			String regex = Utilities.wildcardToRegex(context.getExcludedPaths()[i]);
			Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			excludedPatterns[i] = p;
		}
	}

	protected void onLaunchScan(ScanContext context) {
		startInstant = Instant.now();
		File baseDir = new File(context.getBaseDir());
		candidates.offer(new FileSnap(baseDir));
		for (int i = 0; i < context.getThreadNum(); i++) {
			consumerPool.submit(new FileProcessor(candidates, fileQueue, dirTree, fileCount, dirCount, excludedPatterns,
					fileQueueSize));
		}
	}

	protected void onFinish() {
		consumerPool.shutdownNow();
		endInstant = Instant.now();

		result = new ScanResult();
		result.setSummaryInfo(buildSummaryInfo());
		result.setFileInfo(buildFileInfo());
		result.setDirectoryInfo(buildDirectoryInfo());
	}

	private ScanResultSummaryInfo buildSummaryInfo() {
		ScanResultSummaryInfo info = new ScanResultSummaryInfo();
		info.setBaseDir(dirTree.getRoot().getAbsolutePath());
		info.setDirCount(dirCount.get());
		info.setExcludedPaths(excludedPaths);
		info.setFileCount(fileCount.get());
		info.setSizeInByte(dirTree.getRoot().getSizeInByte());
		info.setTimeCostInSecond(Duration.between(startInstant, endInstant).toSeconds());
		info.setSize(Utilities.formatSize(dirUnit, dirTree.getRoot().getSizeInByte()));
		return info;
	}

	private ScanResultFileInfo buildFileInfo() {
		List<FileSnap> list = new ArrayList<FileSnap>();
		while (!fileQueue.isEmpty()) {
			list.add(fileQueue.poll());
		}

		Collections.reverse(list);
		ScanResultFileInfo info = new ScanResultFileInfo();
		for (FileSnap snap : list) {
			ScanResultFile file = new ScanResultFile();
			file.setAbsolutePath(snap.getAbsolutePath());
			file.setSize(Utilities.formatSize(fileUnit, snap.getSizeInByte()));
			file.setSizeInByte(snap.getSizeInByte());
			info.getFiles().add(file);
		}
		return info;
	}

	private ScanResultDirectoryInfo buildDirectoryInfo() {
		ScanResultDirectoryNode resRoot = new ScanResultDirectoryNode();
		resRoot.setAbsolutePath(dirTree.getRoot().getAbsolutePath());
		resRoot.setSizeInByte(dirTree.getRoot().getSizeInByte());
		resRoot.setExtersion(dirTree.getRoot().getExtension());
		resRoot.setSize(Utilities.formatSize(dirUnit, dirTree.getRoot().getSizeInByte()));
		resRoot.setDirectory(dirTree.getRoot().isDirectory());
		buildScanResultDirectoryTree(resRoot, dirTree.getRoot());

		ScanResultDirectoryInfo info = new ScanResultDirectoryInfo();
		info.setRoot(resRoot);
		return info;
	}

	private void buildScanResultDirectoryTree(ScanResultDirectoryNode resNode, DirectoryNode dirNode) {
		if (dirNode.getChildern().size() == 0) {
			return;
		}
		List<ScanResultDirectoryNode> resChildren = new ArrayList<ScanResultDirectoryNode>();
		resNode.setChildren(resChildren);
		for (DirectoryNode dirChild : dirNode.getChildern().values()) {
			ScanResultDirectoryNode resChild = new ScanResultDirectoryNode();
			resChild.setAbsolutePath(dirChild.getAbsolutePath());
			resChild.setSizeInByte(dirChild.getSizeInByte());
			resChild.setSize(Utilities.formatSize(dirUnit, dirChild.getSizeInByte()));
			resChild.setDirectory(dirChild.isDirectory());
			resChild.setExtersion(dirChild.getExtension());
			resChildren.add(resChild);
		}
		Collections.sort(resChildren, SCAN_RES_DIR_NODE_REV_COMP);
		for (ScanResultDirectoryNode resChild : resChildren) {
			buildScanResultDirectoryTree(resChild, dirNode.getChildern().get(resChild.getAbsolutePath()));
		}

	}

	protected class FileSnapSizeComparator implements Comparator<FileSnap> {

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

	private static class ScanResultDirectoryNodeReverseComparator implements Comparator<ScanResultDirectoryNode> {

		@Override
		public int compare(ScanResultDirectoryNode o1, ScanResultDirectoryNode o2) {
			long diff = o2.getSizeInByte() - o1.getSizeInByte();
			return diff > 0 ? 1 : diff == 0 ? 0 : -1;
		}

	}

}
