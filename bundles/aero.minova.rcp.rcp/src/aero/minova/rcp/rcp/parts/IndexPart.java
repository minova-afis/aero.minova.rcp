package aero.minova.rcp.rcp.parts;

public class IndexPart extends SimplePart {
	@Override
	public void setText(String text) {
		this.text.setText("Indexbereich " + text);
	}
}