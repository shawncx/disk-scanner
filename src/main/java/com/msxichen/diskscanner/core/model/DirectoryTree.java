package com.msxichen.diskscanner.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.msxichen.diskscanner.core.IDirectoryTreeBFSVisitor;

public class DirectoryTree {

	private DirectoryNode root;
	
	private static final DirectoryNodeComparator DIRECTORY_NODE_COMPARATOR = new DirectoryNodeComparator();

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

	public void retrieveBFS(IDirectoryTreeBFSVisitor visitor) {
		LinkedList<DirectoryNode> queue = new LinkedList<>();
		queue.offer(root);
		int size = 1;
		int depth = 0;
		List<DirectoryNode> curLevel = new ArrayList<>();
		while (size > 0) {
			for (int i = 0; i < size; i++) {
				DirectoryNode cur = queue.poll();
				curLevel.add(cur);
				for(DirectoryNode child : cur.getChildern().values()) {
					queue.offer(child);
				}
			}
			Collections.sort(curLevel, DIRECTORY_NODE_COMPARATOR);
			visitor.visitDepth(++depth);
			curLevel.forEach((node) -> visitor.visitNode(node));
			curLevel = new ArrayList<DirectoryNode>();
			size = queue.size();
		}
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

	public DirectoryNode getRoot() {
		return root;
	}
	
	private static class DirectoryNodeComparator implements Comparator<DirectoryNode> {

		@Override
		public int compare(DirectoryNode o1, DirectoryNode o2) {
			long diff = o2.getSizeInByte() - o1.getSizeInByte();
			return diff > 0 ? 1 : diff == 0 ? 0 : -1;
		}
		
	}
	
}
