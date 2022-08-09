
package aero.minova.rcp.rcp.handlers;

import org.eclipse.e4.core.di.annotations.Execute;

public class DeleteSavedSearchCriteriaHandler extends SearchCriteriaActionHandler {

	@Execute
	public void execute() {
		super.execute("DELETE", "");
	}
}