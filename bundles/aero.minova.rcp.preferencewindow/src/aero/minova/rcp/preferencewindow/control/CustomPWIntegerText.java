package aero.minova.rcp.preferencewindow.control;

import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWIntegerText;

public class CustomPWIntegerText extends PWIntegerText {

	public CustomPWIntegerText(String label, String propertyKey) {
		super(label, propertyKey);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Object convertValue() {
		if(text.getText().equals("")) {
			return 0;
		}
		return Integer.parseInt(text.getText());
	}

}
