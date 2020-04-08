package com.msxichen.diskscanner.core.model;

public class ScanProgress {

	private boolean isDone;

	private long dirProcessed;

	private long fileProcessed;

	private long timeCostInSecond;

	private int candidateInQueue;

	public boolean isDone() {
		return isDone;
	}

	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}

	public int getCandidateInQueue() {
		return candidateInQueue;
	}

	public void setCandidateInQueue(int candidateInQueue) {
		this.candidateInQueue = candidateInQueue;
	}

	public long getDirProcessed() {
		return dirProcessed;
	}

	public void setDirProcessed(long dirProcessed) {
		this.dirProcessed = dirProcessed;
	}

	public long getFileProcessed() {
		return fileProcessed;
	}

	public void setFileProcessed(long fileProcessed) {
		this.fileProcessed = fileProcessed;
	}

	public long getTimeCostInSecond() {
		return timeCostInSecond;
	}

	public void setTimeCostInSecond(long timeCostInSecond) {
		this.timeCostInSecond = timeCostInSecond;
	}

}
