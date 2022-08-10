package aero.minova.rcp.rcp.handlers;

import java.util.HashMap;

import javax.inject.Inject;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;

public class SearchCriteriaActionHandler {

	@Inject
	EHandlerService handlerService;

	@Inject
	ECommandService commandService;

	public void execute(String action, String criterianame) {
		HashMap<String, String> parameters = new HashMap<>();
		parameters.put("aero.minova.rcp.rcp.commandparameter.criteriaaction", action);
		parameters.put("aero.minova.rcp.rcp.commandparameter.criterianame", criterianame);
		ParameterizedCommand command = commandService.createCommand("aero.minova.rcp.rcp.command.searchCriteria", parameters);
		handlerService.executeHandler(command);
	}
}
