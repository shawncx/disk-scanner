package com.msxichen.diskscanner.io;

import java.io.BufferedWriter;
import java.io.IOException;

import com.msxichen.diskscanner.core.IDirectoryTreeBFSVisitor;
import com.msxichen.diskscanner.core.model.DirectoryNode;
import com.msxichen.diskscanner.core.model.OutputUnit;

public class DirectoryTreeVisitor implements IDirectoryTreeBFSVisitor {

	private boolean consoleOutput;
	private BufferedWriter fileWriter;
	private OutputUnit outputUnit;

	public DirectoryTreeVisitor(boolean consoleOutput, BufferedWriter fileWriter, OutputUnit outputUnit) {
		this.consoleOutput = consoleOutput;
		this.fileWriter = fileWriter;
		this.outputUnit = outputUnit;
	}

	@Override
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

	@Override
	public void visitNode(DirectoryNode node) {
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

	private String formatNode(DirectoryNode node) {
		double size = 0;
		if (OutputUnit.Gb == outputUnit) {
			size = node.getSizeInByte() / 1024d / 1024d / 1024d;
		} else if (OutputUnit.Mb == outputUnit) {
			size = node.getSizeInByte() / 1024d / 1024d;
		} else if (OutputUnit.Kb == outputUnit) {
			size = node.getSizeInByte() / 1024d;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("Path: ").append(node.getAbsolutePath()).append("\r\n");
		sb.append("Size: ").append(Utilities.formatSize(size)).append(outputUnit).append("\r\n");
		return sb.toString();
	}

}
