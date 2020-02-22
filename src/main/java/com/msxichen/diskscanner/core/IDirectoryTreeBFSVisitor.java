package com.msxichen.diskscanner.core;

import com.msxichen.diskscanner.core.model.DirectoryNode;

public interface IDirectoryTreeBFSVisitor {

	public void visitDepth(int depth);
	
	public void visitNode(DirectoryNode node);
	
}
