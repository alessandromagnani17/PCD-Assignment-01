package Model;

/**
 * Latch of the implementation.
 *
 */
public class TaskCompletionLatch {

	private final int nWorkers;
	private boolean stopped;
	private int nCompletionsNotified;
	
	public TaskCompletionLatch(int nWorkers){
		this.nWorkers = nWorkers;
		nCompletionsNotified = 0;
		stopped = false;
	}
	
	public synchronized void waitCompletion() throws InterruptedException {
		while (nCompletionsNotified < nWorkers && !stopped) {
			wait();
		}
		if (stopped) {
			throw new InterruptedException();
		}
	}

	public synchronized void notifyCompletion() {
		nCompletionsNotified++;
		notifyAll();
	}
}
