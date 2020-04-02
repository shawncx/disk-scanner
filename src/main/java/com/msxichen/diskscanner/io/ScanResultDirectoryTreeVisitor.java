package com.msxichen.diskscanner.io;

import java.io.BufferedWriter;
import java.io.IOException;

import com.msxichen.diskscanner.core.model.OutputUnit;
import com.msxichen.diskscanner.core.model.ScanResultDirectoryNode;

public class ScanResultDirectoryTreeVisitor {

	private boolean consoleOutput;
	private BufferedWriter fileWriter;
	private OutputUnit outputUnit;

	public ScanResultDirectoryTreeVisitor(boolean consoleOutput, BufferedWriter fileWriter, OutputUnit outputUnit) {
		this.consoleOutput = consoleOutput;
		this.fileWriter = fileWriter;
		this.outputUnit = outputUnit;
	}

	public void visitDepth(int depth) {
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

	public void visitNode(ScanResultDirectoryNode node) {
		String message = formatNode(node);
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

	private String formatNode(ScanResultDirectoryNode node) {
		String size = Utilities.formatSize(outputUnit, node.getSizeInByte());
		StringBuilder sb = new StringBuilder();
		sb.append("Path: ").append(node.getAbsolutePath()).append("\r\n");
		sb.append("Size: ").append(size).append(outputUnit).append("\r\n");
		return sb.toString();
	}

}
