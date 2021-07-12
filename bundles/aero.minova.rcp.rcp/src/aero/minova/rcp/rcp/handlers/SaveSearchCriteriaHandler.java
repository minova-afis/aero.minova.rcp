package aero.minova.rcp.rcp.handlers;

import java.util.HashMap;

import javax.inject.Inject;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import aero.minova.rcp.rcp.parts.WFCSearchPart;

public class SaveSearchCriteriaHandler {
	
	@Inject
	EHandlerService handlerService;
	
	@Inject
	ECommandService commandService;

	@Execute
	public void execute() {
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("aero.minova.rcp.rcp.commandparameter.criteriaaction", "SAVE_DEFAULT");
		parameters.put("aero.minova.rcp.rcp.commandparameter.criterianame", "DEFAULT");
		ParameterizedCommand command = commandService.createCommand("aero.minova.rcp.rcp.command.searchCriteria", parameters);
		handlerService.executeHandler(command);
	}
	
	@CanExecute
	public boolean canExecute(MPart part) {
		boolean state = false;
		if(part.getObject() instanceof WFCSearchPart) {
			state = true;
		}
		return state;
	}
}
