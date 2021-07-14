
package aero.minova.rcp.rcp.handlers;

import java.util.HashMap;

import javax.inject.Inject;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Execute;

public class DeleteSavedSearchCriteriaHandler {
	@Inject
	EHandlerService handlerService;

	@Inject
	ECommandService commandService;

	@Execute
	public void execute() {
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("aero.minova.rcp.rcp.commandparameter.criteriaaction", "DELETE");
		parameters.put("aero.minova.rcp.rcp.commandparameter.criterianame", "");
		ParameterizedCommand command = commandService.createCommand("aero.minova.rcp.rcp.command.searchCriteria", parameters);
		handlerService.executeHandler(command);
	}

}