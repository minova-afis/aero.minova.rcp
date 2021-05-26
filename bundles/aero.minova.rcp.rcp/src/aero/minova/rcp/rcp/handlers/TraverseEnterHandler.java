
package aero.minova.rcp.rcp.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.CanExecute;

public class TraverseEnterHandler {
	@Execute
	public void execute() {
		System.out.println("Key Binding funktioniert");
	}

	@CanExecute
	public boolean canExecute() {
		System.out.println("Key Binding funktioniert");
		return true;
	}

}