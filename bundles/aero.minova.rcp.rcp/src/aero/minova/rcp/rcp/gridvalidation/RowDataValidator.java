package aero.minova.rcp.rcp.gridvalidation;

import org.eclipse.nebula.widgets.nattable.data.validate.DataValidator;

import aero.minova.rcp.model.form.IGridValidator;

public class RowDataValidator extends DataValidator {

	private IGridValidator validator;

	public RowDataValidator(IGridValidator validator) {
		this.validator = validator;
	}

	@Override
	public boolean validate(int columnIndex, int rowIndex, Object newValue) {
		validator.validateThrowingException(columnIndex, rowIndex, newValue);
		return true;
	}
}