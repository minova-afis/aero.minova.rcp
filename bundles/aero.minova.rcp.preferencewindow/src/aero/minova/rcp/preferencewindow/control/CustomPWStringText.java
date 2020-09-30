package aero.minova.rcp.preferencewindow.control;

import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWStringText;

public class CustomPWStringText extends PWStringText {

	public CustomPWStringText(String label, String propertyKey) {
		super(label, propertyKey);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object convertValue() {
		if(text.getText().equals(null)) {
			return "";
		}
		return this.text.getText();
	}
}
