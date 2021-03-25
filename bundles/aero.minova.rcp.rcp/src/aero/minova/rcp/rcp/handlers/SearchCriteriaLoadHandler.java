
package aero.minova.rcp.rcp.handlers;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;

public class SearchCriteriaLoadHandler {

	@Inject
	@Preference
	private IEclipsePreferences prefs;

	@AboutToShow
	public void aboutToShow(List<MMenuElement> items) {

	}

}