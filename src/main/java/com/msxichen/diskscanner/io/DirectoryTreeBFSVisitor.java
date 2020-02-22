package com.msxichen.diskscanner.io;

import java.io.BufferedWriter;
import java.io.IOException;

import com.msxichen.diskscanner.core.IDirectoryTreeBFSVisitor;
import com.msxichen.diskscanner.core.model.DirectoryNode;
import com.msxichen.diskscanner.core.model.ScanConfiguration;

public class DirectoryTreeBFSVisitor implements IDirectoryTreeBFSVisitor {

	private boolean consoleOutput;
	private BufferedWriter fileWriter;
	private String sizeUnit;

	public DirectoryTreeBFSVisitor(boolean consoleOutput, BufferedWriter fileWriter, String sizeUnit) {
		this.consoleOutput = consoleOutput;
		this.fileWriter = fileWriter;
		this.sizeUnit = sizeUnit;
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
		if (ScanConfiguration.FILE_SIZE_UNIT_GB.equalsIgnoreCase(sizeUnit)) {
			size = node.getSizeInByte() / 1024d / 1024d / 1024d;
		} else if (ScanConfiguration.FILE_SIZE_UNIT_MB.equalsIgnoreCase(sizeUnit)) {
			size = node.getSizeInByte() / 1024d / 1024d;
		} else if (ScanConfiguration.FILE_SIZE_UNIT_KB.equalsIgnoreCase(sizeUnit)) {
			size = node.getSizeInByte() / 1024d;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("Path: ").append(node.getAbsolutePath()).append("\r\n");
		sb.append("Size: ").append(Utilies.formatSize(size)).append(sizeUnit).append("\r\n");
		return sb.toString();
	}

}
