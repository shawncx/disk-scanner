package com.msxichen.diskscanner.core.model;

public class ScanProgress {

	private boolean isDone;

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

}
