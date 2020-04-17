package com.msxichen.diskscanner.core.model;

import java.util.ArrayList;
import java.util.List;

public class ScanResultDirectoryInfo {

	private ScanResultDirectoryNode root;
	private List<ScanResultExtensionItem> extensionItems = new ArrayList<>();

	public ScanResultDirectoryNode getRoot() {
		return root;
	}

	public void setRoot(ScanResultDirectoryNode root) {
		this.root = root;
	}

	public List<ScanResultExtensionItem> getExtensionItems() {
		return extensionItems;
	}

	public void setExtensionItems(List<ScanResultExtensionItem> extensionItems) {
		this.extensionItems = extensionItems;
	}

}
