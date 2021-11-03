package aero.minova.rcp.rcp.gridvalidation;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.form.IGridValidator;

public class CrossValidationConfiguration extends AbstractRegistryConfiguration {
	private IGridValidator validator;
	IEventBroker broker;

	public CrossValidationConfiguration(IGridValidator validator, IEventBroker broker) {
		this.validator = validator;
		this.broker = broker;
	}

	@Override
	public void configureRegistry(IConfigRegistry configRegistry) {
		configRegistry.registerConfigAttribute(EditConfigAttributes.DATA_VALIDATOR, new RowDataValidator(this.validator), DisplayMode.EDIT,
				Constants.VALIDATION_CELL_LABEL);
		configRegistry.registerConfigAttribute(EditConfigAttributes.VALIDATION_ERROR_HANDLER, new CrossValidationDialogErrorHandling(true, broker),
				DisplayMode.EDIT, Constants.VALIDATION_CELL_LABEL);
	}
}