package com.msxichen.diskscanner.model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.function.Consumer;

public class DirectoryTree {

	private DirectoryNode root;

	public DirectoryTree(String basePath) {
		root = new DirectoryNode(basePath);
	}

	public void increaseSizeDescade(String path, long sizeInByte) {
		if (path.indexOf(root.getAbsolutePath()) != 0) {
			throw new IllegalArgumentException(
					"given path: " + path + " is not sub directory of root path: " + root.getAbsolutePath());
		}
		String relativePath = path.substring(root.getAbsolutePath().length());
		if (relativePath.startsWith("\\")) {
			relativePath = relativePath.substring(1);
		}
		String[] pathSegs = relativePath.length() == 0 ? null : relativePath.split("\\\\");
		StringBuilder base = new StringBuilder().append(root.getAbsolutePath());
		increaseSizeDescade(root, base, pathSegs, 0, sizeInByte);
	}
	
	public void printTreeBFS() {
		LinkedList<DirectoryNode> queue = new LinkedList<>();
		queue.offer(root);
		queue.offer(null);
		int depth = 0;
		System.out.println("******Level " + depth + "******");
		while (queue.size() > 1) {
			DirectoryNode cur = queue.poll();
			if (cur == null) {
				queue.offer(cur);
				System.out.println("******Level " + ++depth + "******");
			} else {
				System.out.println(cur.toString());
				cur.getChildern().forEach((path, child) -> queue.offer(child));
			}
		}
	}
	
	public void visitTreeBFS(DirectoryTreeLevelVisitor levelVisitor, DirectoryTreeNodeVisitor nodeVisitor) {
		LinkedList<DirectoryNode> queue = new LinkedList<>();
		queue.offer(root);
		queue.offer(null);
		int depth = 0;
		levelVisitor.visit(depth);
		while (queue.size() > 1) {
			DirectoryNode cur = queue.poll();
			if (cur == null) {
				queue.offer(cur);
				levelVisitor.visit(++depth);
			} else {
				nodeVisitor.visit(cur);
				cur.getChildern().forEach((path, child) -> queue.offer(child));
			}
		}
	}
	
	public void wirteTreeBFS(String fileName) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
		LinkedList<DirectoryNode> queue = new LinkedList<>();
		queue.offer(root);
		queue.offer(null);
		int depth = 0;
		writer.append("******Level " + depth + "******\r\n");
		while (queue.size() > 1) {
			DirectoryNode cur = queue.poll();
			if (cur == null) {
				queue.offer(cur);
				writer.append("******Level " + ++depth + "******\r\n");
			} else {
				writer.append(cur.toString()).append("\r\n");
				cur.getChildern().forEach((path, child) -> queue.offer(child));
			}
		}
		writer.close();
	}

	private void increaseSizeDescade(DirectoryNode node, StringBuilder path, String[] pathSegs, int pathIndex,
			long sizeInByte) {
		node.increaseSizeInByte(sizeInByte);
		if (pathSegs == null || pathIndex >= pathSegs.length) {
			return;
		}
		StringBuilder nextPath = path.append("\\").append(pathSegs[pathIndex]);

		node.getChildern().putIfAbsent(nextPath.toString(), new DirectoryNode(nextPath.toString()));
		increaseSizeDescade(node.getChildern().get(nextPath.toString()), nextPath, pathSegs, pathIndex + 1, sizeInByte);
	}

}