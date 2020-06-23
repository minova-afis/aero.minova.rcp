package aero.minova.rcp.rcp.parts;

public class SearchPart extends SimplePart {
	@Override
	public void setText(String text) {
		this.text.setText("Suchbereich " + text);
	}
}