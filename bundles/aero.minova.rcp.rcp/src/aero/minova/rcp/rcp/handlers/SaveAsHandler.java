
package aero.minova.rcp.rcp.handlers;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Shell;

public class SaveAsHandler {

	@Inject
	TranslationService translationService;

	@Inject
	@Preference
	private IEclipsePreferences prefs;

	@Inject
	IEventBroker broker;

	@Execute
	public void execute(Shell shell, MPart part) {
		System.out.println("Not in used!");
	}

}