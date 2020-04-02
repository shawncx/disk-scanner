package com.msxichen.diskscanner.core.model;

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
	

	
}
