
package aero.minova.rcp.rcp.handlers;

import org.eclipse.e4.core.di.annotations.Execute;

public class LoadSearchCriteriaHandler extends SearchCriteriaActionHandler {

	@Execute
	public void execute() {
		super.execute("LOAD_DEFAULT", "");
	}
}