package com.msxichen.diskscanner.core;

import java.util.concurrent.PriorityBlockingQueue;

import com.msxichen.diskscanner.core.model.DirectoryTree;
import com.msxichen.diskscanner.core.model.FileSnap;

public interface IScanResultWriter {

	public void writeSummery(long startTimeInMillsecond, long endTimeInMillsecond, long fileCount, long dirCount);

	public void writeDirectoryInfo(DirectoryTree dirTree);

	public void writeFileInfo(PriorityBlockingQueue<FileSnap> fileQueue);

}
