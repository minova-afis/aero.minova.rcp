package aero.minova.rcp.rcp.gridvalidation;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.form.IGridValidator;

public class CrossValidationConfiguration extends AbstractRegistryConfiguration {
	private IGridValidator validator;
	private TranslationService translationService;

	public CrossValidationConfiguration(IGridValidator validator, TranslationService translationService) {
		this.validator = validator;
		this.translationService = translationService;
	}

	@Override
	public void configureRegistry(IConfigRegistry configRegistry) {

		configRegistry.registerConfigAttribute(EditConfigAttributes.DATA_VALIDATOR, new RowDataValidator(this.validator), DisplayMode.EDIT,
				Constants.VALIDATION_CELL_LABEL);
		configRegistry.registerConfigAttribute(EditConfigAttributes.VALIDATION_ERROR_HANDLER, new CrossValidationDialogErrorHandling(true, translationService),
				DisplayMode.EDIT, Constants.VALIDATION_CELL_LABEL);
	}
}