package aero.minova.rcp.rcp.parts;

public class DetailsPart extends SimplePart {
	@Override
	public void setText(String text) {
		this.text.setText("Detailbereich " + text);
	}
}