package aero.minova.rcp.preferencewindow.control;

import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWIntegerText;

public class CostumPWIntegerText extends PWIntegerText {

	public CostumPWIntegerText(String label, String propertyKey) {
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
