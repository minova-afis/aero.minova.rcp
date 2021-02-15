package aero.minova.rcp.rcp.widgets;

import org.eclipse.jface.fieldassist.IContentProposal;

public class MinovaContentProposal implements IContentProposal {

	final int keyLong;
	final String keyText;
	final String desription;

	public MinovaContentProposal(int keyLong, String keyText, String desription) {
		this.keyLong = keyLong;
		this.keyText = keyText;
		this.desription = desription;
	}

	@Override
	public String getContent() {
		return keyText;
	}

	@Override
	public int getCursorPosition() {
		return 0;
	}

	@Override
	public String getLabel() {
		return keyText + " (" + desription + ")";
	}

	@Override
	public String getDescription() {
		return desription;
	}

	@Override
	public String toString() {
		return "MinovaContentProposal [keyLong=" + keyLong + ", keyText=" + keyText + ", desription=" + desription
				+ "]";
	}

}
