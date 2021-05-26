
package aero.minova.rcp.rcp.handlers;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.nebula.widgets.opal.textassist.TextAssist;
import org.eclipse.swt.widgets.Control;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MSection;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.preferencewindow.builder.DisplayType;
import aero.minova.rcp.preferencewindow.builder.InstancePreferenceAccessor;
import aero.minova.rcp.rcp.accessor.AbstractValueAccessor;
import aero.minova.rcp.rcp.parts.WFCDetailPart;
import aero.minova.rcp.rcp.widgets.Lookup;

public class TraverseEnterHandler {

	@Inject
	@Named(TranslationService.LOCALE)
	Locale locale;

	@Inject
	EPartService partService;

	@Inject
	ESelectionService selectionService;

	@Execute
	public void execute() {
		MPart part = partService.getActivePart();

		System.out.println("Key Binding funktioniert");
		if (part.getObject() instanceof WFCDetailPart) {
			MDetail detail = ((WFCDetailPart) part.getObject()).getDetail();
			for (MSection s : detail.getPageList()) {
				if (s.getSelectedField() != null) {
					Control focussedControl = s.getSelectedField();
					getNextRequired(focussedControl.getParent(), detail);
					break;
				}
			}
		}
	}

	@CanExecute
	public boolean canExecute() {
		boolean emptyRequiredField = false;
		MPart part = partService.getActivePart();

		System.out.println("Key Binding funktioniert (CanExecute)");
		if (part.getObject() instanceof WFCDetailPart) {
			MDetail detail = ((WFCDetailPart) part.getObject()).getDetail();

			for (MSection section : detail.getPageList()) {
				for (MField field : section.getTabList()) {
					if (field.isRequired() && field.getValue() == null) {
						emptyRequiredField = true;
						break;
					}
				}
			}

			System.out.println(emptyRequiredField);
			return emptyRequiredField;
		}

		return false;
	}

	/**
	 * Ermittelt das in der Tab Reihenfolge nachfolgende Pflichtfeld und selektiert dieses.
	 * <p>
	 * Wenn LookupEnterSelectsNextRequired gesetzt ist und das Lookup offen ist, wird der ausgewählte Wert festgesetzt und das nächste Pflichtfeld wird
	 * selektiert. Wenn LookupEnterSelectsNextRequired nicht gesetzt ist, wird der Wert fesetgesetzt und der Benutzer bleibt im Feld.
	 * <p>
	 * Wenn EnterSelectsFirstRequired gesetzt ist, wird das erste nicht ausgefüllte Pflichtfeld ermittelt und selektiert.
	 *
	 * @param focussedControl
	 *            das aktuell selektierte Feld
	 */
	private void getNextRequired(Control focussedControl, MDetail detail) {
		Preferences preferences = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE);
		boolean lookupEnterSelectsNextRequired = (boolean) InstancePreferenceAccessor.getValue(preferences,
				ApplicationPreferences.LOOKUP_ENTER_SELECTS_NEXT_REQUIRED, DisplayType.CHECK, true, locale);
		boolean enterSelectsFirstRequired = (boolean) InstancePreferenceAccessor.getValue(preferences, ApplicationPreferences.ENTER_SELECTS_FIRST_REQUIRED,
				DisplayType.CHECK, true, locale);

		boolean popupOpen = false;
		if (focussedControl instanceof Lookup) {
			Lookup lookup = (Lookup) focussedControl;
			// Wir holen uns den Status des Popup des Lookup
			popupOpen = lookup.popupIsOpen();
		}
		if (focussedControl instanceof TextAssist) {
			popupOpen = false;
		}

		// Wir holen uns das MField des selektierten Felds.
		MField selectedField = null;
		List<MSection> sectionList = detail.getPageList();
		for (MSection page : sectionList) {
			List<MField> tabList = page.getTabList();
			for (MField field : tabList) {
				if (((AbstractValueAccessor) field.getValueAccessor()).getControl() == focussedControl) {
					selectedField = field;
					break;
				}
			}
			if (selectedField != null)
				break;

		}

		// Wir prüfen ob die Preference LookupEnterSelectsNextRequired nicht gesetzt ist und das Lookup offen ist.
		if (!lookupEnterSelectsNextRequired && popupOpen) {
			focussedControl = ((AbstractValueAccessor) selectedField.getValueAccessor()).getControl();
			Lookup lookup = (Lookup) focussedControl;
			lookup.closePopup();
		}

		// Wir prüfen ob die Preference EnterSelectsFirstRequired gesetzt ist.
		if (!enterSelectsFirstRequired || popupOpen) {
			Control fc = null;
			List<MField> tabListFromSelectedFieldSection = selectedField.getmSection().getTabList();
			// [0,1,2,3,4,5,6,7,8,9] --> sublist(0,5) = [0,1,2,3,4]
			// Size = 10
			int indexOfSelectedField = tabListFromSelectedFieldSection.indexOf(selectedField);
			// Sind auf der selben Section nach meinem Feld noch unausgefüllte Required Fields?
			fc = getNextRequiredFieldWhichNull(tabListFromSelectedFieldSection.subList(indexOfSelectedField + 1, tabListFromSelectedFieldSection.size()));
			if (fc != null) {
				return;
			}

			int indexOfSection = sectionList.indexOf(selectedField.getmSection());

			fc = getNextControlFromSectionIfNull(selectedField, sectionList.subList(indexOfSection + 1, sectionList.size()));
			if (fc != null) {
				return;
			}
			fc = getNextControlFromSectionIfNull(selectedField, sectionList.subList(0, indexOfSection));
			if (fc != null) {
				return;
			}
			// Sind auf der selben Section vor meinem Feld noch unausgefüllte Required Fields?
			fc = getNextRequiredFieldWhichNull(tabListFromSelectedFieldSection.subList(0, indexOfSelectedField));
			if (fc == null) {
				if (focussedControl instanceof Lookup) {
					Lookup lookup = (Lookup) focussedControl;
					lookup.closePopup();
				}
				return;
			}
		} else {
			for (MSection section : sectionList) {
				List<MField> tabList = section.getTabList();
				for (MField field : tabList) {
					if (field.isRequired() && field.getValue() == null && !field.isReadOnly() && field != selectedField) {
						focussedControl = ((AbstractValueAccessor) field.getValueAccessor()).getControl();
						focussedControl.setFocus();
						return;
					}
				}
			}
		}
		return;
	}

	private Control getNextControlFromSectionIfNull(MField selectedField, List<MSection> sectionList) {
		Control focussedControl = null;
		for (MSection section : sectionList) {
			List<MField> tabList = section.getTabList();

			if (selectedField.getmSection().equals(section)) {
				int indexOfSelectedField = tabList.indexOf(selectedField);

				focussedControl = getNextRequiredFieldWhichNull(tabList.subList(indexOfSelectedField, tabList.size() - 1));
				if (focussedControl != null) {
					return focussedControl;
				}
				focussedControl = getNextRequiredFieldWhichNull(tabList.subList(0, indexOfSelectedField));
				if (focussedControl != null) {
					return focussedControl;
				}
			} else {
				focussedControl = getNextRequiredFieldWhichNull(tabList);
				if (focussedControl != null) {
					return focussedControl;
				}
			}
		}
		return focussedControl;
	}

	private Control getNextRequiredFieldWhichNull(List<MField> tabListAfter) {
		Control focussedControl = null;
		for (MField field : tabListAfter) {
			// 1. mein Feld kommt nach dem aktuellen INIT_FIELD ##
			// 2. Mein Feld kommt nach dem aktuellen INIT_FIELD, auf nächster Section ##
			// 3. Mein Feld kommt vor dem aktuellen INIT_FIELD, auf vorheriger Section ##
			// 4. Mein Feld kommt vor dem aktuellen INIT_FIELD, auf gleicher Section ##
			if (field.getValue() == null && field.isRequired() && !field.isReadOnly()) {
				focussedControl = ((AbstractValueAccessor) field.getValueAccessor()).getControl();
				focussedControl.setFocus();
				return focussedControl;
			}
		}
		return focussedControl;
	}

}