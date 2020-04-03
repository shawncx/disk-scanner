package com.msxichen.diskscanner.core.model;

public class DirectoryTree {

	private DirectoryNode root;

	public DirectoryTree(String basePath, boolean isDirectory) {
		root = new DirectoryNode(basePath, isDirectory);
	}

	public void increaseSizeDescade(String path, long sizeInByte, boolean isDirectory) {
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
		increaseSizeDescade(root, base, pathSegs, 0, sizeInByte, isDirectory);
	}

	private void increaseSizeDescade(DirectoryNode node, StringBuilder path, String[] pathSegs, int pathIndex,
			long sizeInByte, boolean isDirectory) {
		node.increaseSizeInByte(sizeInByte);
		if (pathSegs == null || pathIndex >= pathSegs.length) {
			return;
		}
		StringBuilder nextPath = path.append("\\").append(pathSegs[pathIndex]);

		node.getChildern().putIfAbsent(nextPath.toString(), new DirectoryNode(nextPath.toString(), isDirectory));
		increaseSizeDescade(node.getChildern().get(nextPath.toString()), nextPath, pathSegs, pathIndex + 1, sizeInByte,
				isDirectory);
	}

	public DirectoryNode getRoot() {
		return root;
	}

}
