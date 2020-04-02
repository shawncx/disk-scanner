package com.msxichen.diskscanner.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.msxichen.diskscanner.core.model.FileSnap;
import com.msxichen.diskscanner.core.model.OutputType;
import com.msxichen.diskscanner.core.model.ScanContext;
import com.msxichen.diskscanner.core.model.ScanResult;
import com.msxichen.diskscanner.core.model.ScanResultDirectoryInfo;
import com.msxichen.diskscanner.core.model.ScanResultDirectoryNode;
import com.msxichen.diskscanner.core.model.ScanResultFileInfo;
import com.msxichen.diskscanner.core.model.ScanResultSummaryInfo;

public class ScanResultLocalWriter implements AutoCloseable {

	private boolean consoleOutput;
	private BufferedWriter summaryWriter;
	private BufferedWriter fileInfoWriter;
	private BufferedWriter dirInfoWriter;

	private ScanContext context;

	private static final Logger LOGGER = LogManager.getLogger();

	public ScanResultLocalWriter(ScanContext context) throws IOException {
		this.context = context;

		if (context.getOutputTypes().contains(OutputType.File)) {
			summaryWriter = new BufferedWriter(new FileWriter(context.getSummaryOutputPath().toString()));
			fileInfoWriter = new BufferedWriter(new FileWriter(context.getFileInfoOutputPath().toString()));
			dirInfoWriter = new BufferedWriter(new FileWriter(context.getDirInfoOutputPath().toString()));
		}
		if (context.getOutputTypes().contains(OutputType.Console)) {
			consoleOutput = true;
		}
	}

	public void writeResult(ScanResult result) {
		writeSummery(result.getSummaryInfo());
		writeDirectoryInfo(result.getDirectoryInfo());
		writeFileInfo(result.getFileInfo());
	}

	public void writeSummery(ScanResultSummaryInfo info) {
		writeLine(summaryWriter, "Time cost: " + info.getTimeCostInSecond() + " seconds.");
		writeLine(summaryWriter, "File count: " + info.getFileCount());
		writeLine(summaryWriter, "Directory count: " + info.getDirCount());
		writeLine(summaryWriter, "Root directory: " + info.getBaseDir());
		writeLine(summaryWriter, "Size: " + Utilities.formatSize(context.getDirOutputUnit(), info.getSizeInByte())
				+ context.getDirOutputUnit());
	}

	public void writeDirectoryInfo(ScanResultDirectoryInfo info) {
		ScanResultDirectoryTreeVisitor visitor = new ScanResultDirectoryTreeVisitor(consoleOutput, dirInfoWriter, context.getDirOutputUnit());
		LinkedList<ScanResultDirectoryNode> queue = new LinkedList<>();
		queue.offer(info.getRoot());
		int size = 1;
		int depth = 0;
		List<ScanResultDirectoryNode> curLevel = new ArrayList<>();
		while (size > 0) {
			for (int i = 0; i < size; i++) {
				ScanResultDirectoryNode cur = queue.poll();
				curLevel.add(cur);
				for (ScanResultDirectoryNode child : cur.getChildren()) {
					queue.offer(child);
				}
			}
			visitor.visitDepth(++depth);
			curLevel.forEach((node) -> visitor.visitNode(node));
			curLevel = new ArrayList<ScanResultDirectoryNode>();
			size = queue.size();
		}
	}

	public void writeFileInfo(ScanResultFileInfo info) {
		writeLine(fileInfoWriter, "Top size File: ");
		writeLine(fileInfoWriter, "*********************************************************");
		for (FileSnap file : info.getFiles()) {
			StringBuilder msg = new StringBuilder();
			msg.append("Path: ").append(file.getAbsolutePath()).append("\r\n");
			msg.append("Size: ").append(Utilities.formatSize(context.getFileOutputUnit(), file.getSizeInByte()))
					.append(context.getFileOutputUnit());
			writeLine(fileInfoWriter, msg.toString());
		}
	}

	@Override
	public void close() throws Exception {
		if (summaryWriter != null) {
			System.out.println("Write summary info into " + context.getSummaryOutputPath().toString());
			summaryWriter.close();
		}
		if (fileInfoWriter != null) {
			System.out.println("Write file info into " + context.getFileInfoOutputPath().toString());
			fileInfoWriter.close();
		}
		if (dirInfoWriter != null) {
			System.out.println("Write directory info into " + context.getDirInfoOutputPath().toString());
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
			LOGGER.error(e);
		}
	}
}
