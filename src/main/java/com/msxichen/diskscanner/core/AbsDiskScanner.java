package com.msxichen.diskscanner.core;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import com.msxichen.diskscanner.core.model.DirectoryNode;
import com.msxichen.diskscanner.core.model.DirectoryTree;
import com.msxichen.diskscanner.core.model.ExtensionItem;
import com.msxichen.diskscanner.core.model.FileSnap;
import com.msxichen.diskscanner.core.model.OutputUnit;
import com.msxichen.diskscanner.core.model.ScanContext;
import com.msxichen.diskscanner.core.model.ScanResult;
import com.msxichen.diskscanner.core.model.ScanResultDirectoryInfo;
import com.msxichen.diskscanner.core.model.ScanResultDirectoryNode;
import com.msxichen.diskscanner.core.model.ScanResultExtensionItem;
import com.msxichen.diskscanner.core.model.ScanResultFile;
import com.msxichen.diskscanner.core.model.ScanResultFileInfo;
import com.msxichen.diskscanner.core.model.ScanResultSummaryInfo;
import com.msxichen.diskscanner.io.Utilities;

public abstract class AbsDiskScanner {

	protected PriorityBlockingQueue<FileSnap> fileQueue;
	protected DirectoryTree dirTree;
	protected ConcurrentHashMap<String, ExtensionItem> extensionMap;
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
	
	private static final int SUMMARY_TOP_FILE_COUNT = 10;

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
		extensionMap = new ConcurrentHashMap<String, ExtensionItem>();
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
			consumerPool.submit(new FileProcessor(candidates, fileQueue, dirTree, extensionMap, fileCount, dirCount,
					excludedPatterns, fileQueueSize));
		}
	}

	protected void onFinish() {
		consumerPool.shutdownNow();
		endInstant = Instant.now();
		
		List<ScanResultExtensionItem> resExtensionItems = new ArrayList<>();
		for (ExtensionItem item : extensionMap.values()) {
			resExtensionItems.add(
					new ScanResultExtensionItem(item.getExtension(), item.getSizeInByteValue(), item.getCountValue()));
		}
		Collections.sort(resExtensionItems, new Comparator<ScanResultExtensionItem>() {
			public int compare(ScanResultExtensionItem a, ScanResultExtensionItem b) {
				return b.getSizeInByte() - a.getSizeInByte() > 0 ? 1 : -1;
			}
		});
		
		List<FileSnap> topFiles = new ArrayList<>();
		List<ScanResultFile> resTopFiles = new ArrayList<>();
		while (!fileQueue.isEmpty()) {
			topFiles.add(fileQueue.poll());
		}
		Collections.reverse(topFiles);
		for (FileSnap snap : topFiles) {
			ScanResultFile file = new ScanResultFile();
			file.setAbsolutePath(snap.getAbsolutePath());
			file.setSize(Utilities.formatSize(fileUnit, snap.getSizeInByte()));
			file.setSizeInByte(snap.getSizeInByte());
			resTopFiles.add(file);
		}
		
		result = new ScanResult();
		result.setSummaryInfo(buildSummaryInfo(resExtensionItems, resTopFiles));
		result.setFileInfo(buildFileInfo(resTopFiles));
		result.setDirectoryInfo(buildDirectoryInfo(resExtensionItems));
	}

	private ScanResultSummaryInfo buildSummaryInfo(List<ScanResultExtensionItem> resExtensionItems, List<ScanResultFile> resTopFiles) {
		ScanResultSummaryInfo info = new ScanResultSummaryInfo();
		info.setBaseDir(dirTree.getRoot().getAbsolutePath());
		info.setDirCount(dirCount.get());
		info.setExcludedPaths(excludedPaths);
		info.setFileCount(fileCount.get());
		info.setSizeInByte(dirTree.getRoot().getSizeInByte());
		info.setTimeCostInSecond(Duration.between(startInstant, endInstant).toSeconds());
		info.setSize(Utilities.formatSize(dirUnit, dirTree.getRoot().getSizeInByte()));
		info.setExtensionItems(resExtensionItems);
		for (int i = 0; i < SUMMARY_TOP_FILE_COUNT && i < resTopFiles.size(); i++) {
			info.getTopFiles().add(resTopFiles.get(i));
		}
		return info;
	}

	private ScanResultFileInfo buildFileInfo(List<ScanResultFile> resTopFiles) {
		ScanResultFileInfo info = new ScanResultFileInfo();
		info.setFiles(resTopFiles);
		return info;
	}

	private ScanResultDirectoryInfo buildDirectoryInfo(List<ScanResultExtensionItem> resExtensionItems) {
		ScanResultDirectoryNode resRoot = new ScanResultDirectoryNode();
		resRoot.setAbsolutePath(dirTree.getRoot().getAbsolutePath());
		resRoot.setSizeInByte(dirTree.getRoot().getSizeInByte());
		resRoot.setExtension(dirTree.getRoot().getExtension());
		resRoot.setSize(Utilities.formatSize(dirUnit, dirTree.getRoot().getSizeInByte()));
		resRoot.setDirectory(dirTree.getRoot().isDirectory());
		buildScanResultDirectoryTree(resRoot, dirTree.getRoot());

		ScanResultDirectoryInfo info = new ScanResultDirectoryInfo();
		info.setRoot(resRoot);
		info.setExtensionItems(resExtensionItems);
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
			resChild.setExtension(dirChild.getExtension());
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
