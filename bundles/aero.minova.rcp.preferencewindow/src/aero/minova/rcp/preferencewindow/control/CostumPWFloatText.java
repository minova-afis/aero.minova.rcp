package aero.minova.rcp.preferencewindow.control;

import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWFloatText;

public class CostumPWFloatText extends PWFloatText {

	public CostumPWFloatText(String label, String propertyKey) {
		super(label, propertyKey);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object convertValue() {
		if(text.getText().equals("")) {
			return 0.0;
		}
		return Float.parseFloat(text.getText());
	}
}

