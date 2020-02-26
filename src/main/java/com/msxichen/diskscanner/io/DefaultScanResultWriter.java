package com.msxichen.diskscanner.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

import com.msxichen.diskscanner.core.IScanResultWriter;
import com.msxichen.diskscanner.core.model.DirectoryNode;
import com.msxichen.diskscanner.core.model.DirectoryTree;
import com.msxichen.diskscanner.core.model.FileSnap;
import com.msxichen.diskscanner.core.model.ScanConfiguration;

public class DefaultScanResultWriter implements IScanResultWriter, AutoCloseable {

	private boolean consoleOutput;
	private BufferedWriter summaryWriter;
	private BufferedWriter fileInfoWriter;
	private BufferedWriter dirInfoWriter;

	private String fileUnit;
	private String dirUnit;

	public DefaultScanResultWriter(ScanConfiguration config) throws IOException {
		for (String type : config.getOutputTypes()) {
			if (ScanConfiguration.OUTPUT_TYPE_FILE.equalsIgnoreCase(type)) {
				if (config.getOutputSummaryTo() != null && config.getOutputSummaryTo().length() > 0) {
					summaryWriter = new BufferedWriter(new FileWriter(config.getOutputSummaryTo()));
				}
				if (config.getOutputFileInfoTo() != null && config.getOutputFileInfoTo().length() > 0) {
					fileInfoWriter = new BufferedWriter(new FileWriter(config.getOutputFileInfoTo()));
				}
				if (config.getOutputDirInfoTo() != null && config.getOutputDirInfoTo().length() > 0) {
					dirInfoWriter = new BufferedWriter(new FileWriter(config.getOutputDirInfoTo()));
				}
			} else if (ScanConfiguration.OUTPUT_TYPE_CONSOLE.equalsIgnoreCase(type)) {
				consoleOutput = true;
			}
		}
		fileUnit = config.getFileSizeUnit();
		dirUnit = config.getDirSizeUnit();
	}

	@Override
	public void writeSummery(long timeCostInMillsecond, long fileCount, long dirCount, DirectoryTree dirTree) {
		writeLine(summaryWriter, "Time cost: " + timeCostInMillsecond / 1000 + " seconds.");
		writeLine(summaryWriter, "File count: " + fileCount);
		writeLine(summaryWriter, "Directory count: " + dirCount);
		double size = getSizeWithUnit(dirTree.getRoot());
		writeLine(summaryWriter, "Root directory: " + dirTree.getRoot().getAbsolutePath());
		writeLine(summaryWriter, "Size: " + Utilities.formatSize(size) + fileUnit);
	}

	@Override
	public void writeDirectoryInfo(DirectoryTree dirTree) {
		dirTree.retrieveBFS(new DirectoryTreeBFSVisitor(consoleOutput, dirInfoWriter, dirUnit));
	}

	@Override
	public void writeFileInfo(PriorityBlockingQueue<FileSnap> fileQueue) {
		writeLine(fileInfoWriter, "Top size File: ");
		writeLine(fileInfoWriter, "*********************************************************");
		List<FileSnap> list = new ArrayList<>();
		while (!fileQueue.isEmpty()) {
			list.add(fileQueue.poll());
		}

		for (int i = 0; list.size() - i - 1 >= 0; i++) {
			FileSnap file = list.get(list.size() - i - 1);
			double size = getSizeWithUnit(file);
			StringBuilder msg = new StringBuilder();
			msg.append("Path: ").append(file.getAbsolutePath()).append("\r\n");
			msg.append("Size: ").append(Utilities.formatSize(size)).append(fileUnit);
			writeLine(fileInfoWriter, msg.toString());
		}
	}

	@Override
	public void close() throws Exception {
		if (summaryWriter != null) {
			summaryWriter.close();
		}
		if (fileInfoWriter != null) {
			fileInfoWriter.close();
		}
		if (dirInfoWriter != null) {
			dirInfoWriter.close();
		}
	}

	private void writeLine(BufferedWriter fileWriter, String message) {
		try {
			if (consoleOutput) {
				System.out.println(message);
			}
			if (fileWriter != null) {
				fileWriter.append(message).append("\r\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private double getSizeWithUnit(FileSnap file) {
		if (ScanConfiguration.FILE_SIZE_UNIT_GB.equalsIgnoreCase(fileUnit)) {
			return file.getSizeInGigaByte();
		} else if (ScanConfiguration.FILE_SIZE_UNIT_MB.equalsIgnoreCase(fileUnit)) {
			return file.getSizeMegaByte();
		} else if (ScanConfiguration.FILE_SIZE_UNIT_KB.equalsIgnoreCase(fileUnit)) {
			return file.getSizeKiloByte();
		} else {
			return 0;
		}
	}

	private double getSizeWithUnit(DirectoryNode node) {
		if (ScanConfiguration.FILE_SIZE_UNIT_GB.equalsIgnoreCase(fileUnit)) {
			return node.getSizeInGigaByte();
		} else if (ScanConfiguration.FILE_SIZE_UNIT_MB.equalsIgnoreCase(fileUnit)) {
			return node.getSizeMegaByte();
		} else if (ScanConfiguration.FILE_SIZE_UNIT_KB.equalsIgnoreCase(fileUnit)) {
			return node.getSizeKiloByte();
		} else {
			return 0;
		}
	}
}
