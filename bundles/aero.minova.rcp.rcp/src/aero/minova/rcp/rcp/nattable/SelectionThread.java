package aero.minova.rcp.rcp.nattable;

public abstract class SelectionThread extends Thread {
	private int sleepMillis;

	/*
	 * Thread, der für sleepMillis Millisekunden schläft und danach die Daten ins Detail lädt, wenn er nicht unterbrochen wurde
	 */
	protected SelectionThread(int sleepMillis) {
		this.sleepMillis = sleepMillis;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(sleepMillis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return;
		}

		doSelectionAction();
	}

	protected abstract void doSelectionAction();

}