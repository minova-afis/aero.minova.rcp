package aero.minova.rcp.rcp.handlers;

import java.util.List;
import java.util.Locale;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.nebula.widgets.opal.textassist.TextAssist;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Control;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MSection;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.preferencewindow.builder.DisplayType;
import aero.minova.rcp.preferencewindow.builder.InstancePreferenceAccessor;
import aero.minova.rcp.rcp.accessor.AbstractValueAccessor;
import aero.minova.rcp.rcp.widgets.Lookup;

public class TraverseListenerImpl implements TraverseListener {

	private static final boolean LOG = "true".equalsIgnoreCase(Platform.getDebugOption("aero.minova.rcp.rcp/debug/traverselistenerimpl"));

	String INIT_FIELD = "InitializeField";

	Logger logger;
	MDetail detail;
	Locale locale;
	EPartService partService;
	IEclipseContext context;

	public TraverseListenerImpl(Logger logger, MDetail detail, Locale locale, EPartService partService, IEclipseContext context) {
		this.logger = logger;
		this.detail = detail;
		this.locale = locale;
		this.partService = partService;
		this.context = context;
	}

	@Override
	public void keyTraversed(TraverseEvent e) {

		logger.info(
				"keyTraversed(detail=" + e.detail + ", stateMask=" + Integer.toHexString(e.stateMask) + ", keyCode= " + Integer.toHexString(e.keyCode) + ")");
		List<MSection> pageList;
		// if (!e.doit) return; // wir tun nichts, wenn ein anderer etwas getan hat

		Control focussedControl = (Control) e.widget;
		if ((focussedControl.getParent() instanceof Lookup) || (focussedControl.getParent() instanceof TextAssist)) {
			focussedControl = focussedControl.getParent();
		}

		switch (e.detail) {
		case SWT.TRAVERSE_ARROW_NEXT:
			logger.info("SWT.TRAVERSE_ARROW_NEXT");
			break;
		case SWT.TRAVERSE_ARROW_PREVIOUS:
			logger.info("SWT.TRAVERSE_ARROW_PREVIOUS");
			break;
		case SWT.TRAVERSE_ESCAPE:
			logger.info("SWT.TRAVERSE_ESCAPE");
			break;
		case SWT.TRAVERSE_MNEMONIC:
			logger.info("SWT.TRAVERSE_MNEMONIC");
			break;
		case SWT.TRAVERSE_NONE:
			logger.info("SWT.TRAVERSE_NONE");
			break;
		case SWT.TRAVERSE_PAGE_NEXT:
			logger.info("SWT.TRAVERSE_PAGE_NEXT");
			break;
		case SWT.TRAVERSE_PAGE_PREVIOUS:
			logger.info("SWT.TRAVERSE_PAGE_PREVIOUS");
			break;
		case SWT.TRAVERSE_RETURN:
			logger.info("SWT.TRAVERSE_RETURN");
			getNextRequired(focussedControl);
			e.doit = false;
			break;
		case SWT.TRAVERSE_TAB_NEXT:
			logger.info("SWT.TRAVERSE_TAB_NEXT");
			if (e.keyCode == SWT.TAB) {
				getNextField(focussedControl);
			} else if (e.keyCode == SWT.CR) {
				getNextRequired(focussedControl);
			}
			e.doit = false;
			break;
		case SWT.TRAVERSE_TAB_PREVIOUS:
			logger.info("SWT.TRAVERSE_TAB_PREVIOUS");
			getPreviousField(focussedControl);
			e.doit = false;
			break;
		default:
			logger.info("UNKNOWN");
		}

	}

	//////////////////////////////////////////////////////////////////////////////////////////////
	////////////////// Ermittlung des vorherigen Feldes //////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Ermittelt das in der Tab Reihenfolge vorherige Feld und selektiert dieses.
	 * <p>
	 * Wenn SelectAllControls gesetzt ist, wird beim Wechsel der Page die Registerkarte der Page selektiert. Falls sich der Benutzer im ersten Feld vom HEAD
	 * befindet und SelectAllControls gesetzt ist, wird die Toolbar des Details selektiert.
	 *
	 * @param focussedControl
	 *            das aktuell selektierte Feld
	 */
	private void getPreviousField(Control focussedControl) {
		Preferences preferences = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE);
		boolean selectAllControls = (boolean) InstancePreferenceAccessor.getValue(preferences, ApplicationPreferences.SELECT_ALL_CONTROLS, DisplayType.CHECK,
				true, locale);

		List<MSection> sectionList = detail.getPageList();

		if (context.get(INIT_FIELD) instanceof MField) {
			// Wir prüfen, ob das ausgewählte Feld als initial Feld gespeichert wurde oder ob ein i Feld gesetzt ist.
			if (focussedControl.equals(((AbstractValueAccessor) ((MField) context.get(INIT_FIELD)).getValueAccessor()).getControl())) {
				// Wir holen uns die Page des initial Feldes
				MSection initSection = sectionList.get(sectionList.indexOf(((MField) context.get(INIT_FIELD)).getmSection()));
				// Wir holen uns die Tab Liste der Initial Page
				List<MField> initTabList = initSection.getTabList();
				// Wir holen uns das initial Feld aus dem Context.
				MField initiField = (MField) context.get(INIT_FIELD);

				// Wir geben uns das initial Feld aus.
				if (LOG) {
					System.out.println("Gespeichertes Feld:" + context.get(INIT_FIELD));
				}

				setPreviousControl(selectAllControls, sectionList, initSection, initTabList, initiField);

				// Wir geben uns das neue initial Feld aus.
				if (LOG) {
					System.out.println("Gespeichertes neues Feld:" + context.get(INIT_FIELD));
				}
				return;
			}
		}

		for (MSection section : sectionList) {
			List<MField> tabList = section.getTabList();
			for (MField f : tabList) {
				if (((AbstractValueAccessor) f.getValueAccessor()).getControl() == focussedControl) {
					setPreviousControl(selectAllControls, sectionList, section, tabList, f);
					if (LOG) {
						System.out.println("Gespeichertes neues Feld:" + context.get(INIT_FIELD));
					}
					return;
				}
			}
		}

	}

	/**
	 * Diese Methode sucht das vorherige Control und gibt diesem Control den Fokus.
	 *
	 * @param selectAllControls
	 * @param sectionList
	 * @param initSection
	 * @param initTabList
	 * @param initiField
	 */
	private void setPreviousControl(boolean selectAllControls, List<MSection> sectionList, MSection initSection, List<MField> initTabList, MField initiField) {
		Control focussedControl;
		if (initTabList.indexOf(initiField) == 0) {
			// Die Preference SelectAllControls ist gesetzt.
			if (selectAllControls) {
				// Wir sind in der ersten Section
				if (sectionList.indexOf(initSection) == 0) {
					MToolBar toolbarElements = partService.getActivePart().getToolbar();
					focussedControl = (Control) toolbarElements.getWidget();
					context.set(INIT_FIELD, focussedControl);
					// Wir prüfen nach dem Twistie der Section
				} else {
					focussedControl = sectionList.get(sectionList.indexOf(initSection)).getSectionControl();
					context.set(INIT_FIELD, focussedControl);
				}
			} else {
				List<MField> previousTabList = sectionList.get(sectionList.indexOf(initSection) - 1).getTabList();
				if (!previousTabList.get(previousTabList.indexOf(initiField) - 1).isReadOnly()) {
					focussedControl = ((AbstractValueAccessor) previousTabList.get(previousTabList.size() - 1).getValueAccessor()).getControl();
				} else {
					focussedControl = getPreviousControlFromSplitSectionList(sectionList, initiField);
				}
			}
		} else {
			if (!initTabList.get(initTabList.indexOf(initiField) - 1).isReadOnly()) {
				focussedControl = ((AbstractValueAccessor) initTabList.get(initTabList.indexOf(initiField) - 1).getValueAccessor()).getControl();
			} else {
				focussedControl = getPreviousControlFromSplitSectionList(sectionList, initiField);
			}
		}
		focussedControl.setFocus();
	}

	/**
	 * Setzt die Sections in die richtige Reihenfolge und ermittelt dann das zu selektierende nicht ReadOnly Feld.
	 * <p>
	 * 1. Zuerst werden die Felder, die vor dem selektierten Feld und in der aktuellen Section stehen, von oben nach unten geprüft. <br>
	 * 2. Wenn kein Control gefunden wurde, werden die Sections, die vor der aktuellen Section stehen geprüft. Beginnend mit dem Feld und der Section am Ende
	 * der Liste. <br>
	 * 3. Falls immer noch kein Control gefunden wurde, werden die Section nach der aktuellen Section geprüft. Beginnend mit dem letzten Feld in der letzten
	 * Section. <br>
	 * 4. Beim der aktuellen Section angekommen und es wurde immer noch kein Control gefunden, werden die Felder, die nach dem slektieren Feld stehen geprüft.
	 * Beginnend mit dem letzten Feld der Section.
	 * 
	 * @param sectionList
	 *            Liste aller Sections in Part.
	 * @param initField
	 *            das selektierte Feld.
	 * @return liefert das ermittelte nicht ReadOnly Feld zurück.
	 */
	private Control getPreviousControlFromSplitSectionList(List<MSection> sectionList, MField initField) {
		Control fc = null;
		List<MField> tabListFromSelectedFieldSection = initField.getmSection().getTabList();
		// [0,1,2,3,4,5,6,7,8,9] --> sublist(0,5) = [0,1,2,3,4]
		// Size = 10
		int indexOfSelectedField = tabListFromSelectedFieldSection.indexOf(initField);
		// Sind auf der selben Section nach meinem Feld noch unausgefüllte Required Fields?
		fc = getPreviousFieldControl(tabListFromSelectedFieldSection.subList(0, indexOfSelectedField));
		if (fc != null) {
			return fc;
		}

		int indexOfSection = sectionList.indexOf(initField.getmSection());

		fc = getPreviousControlFromSectionIfNull(initField, sectionList.subList(0, indexOfSection));
		if (fc != null) {
			return fc;
		}
		fc = getPreviousControlFromSectionIfNull(initField, sectionList.subList(indexOfSection + 1, sectionList.size()));
		if (fc != null) {
			return fc;
		}
		// Sind auf der selben Section vor meinem Feld noch unausgefüllte Required Fields?
		fc = getPreviousFieldControl(tabListFromSelectedFieldSection.subList(indexOfSelectedField + 1, tabListFromSelectedFieldSection.size()));
		return fc;
	}

	/**
	 * Prüft die Felder der Sections der übergebenen SectionList in der angegebenen Reihenfolge und ermittelt das zu selektierende nicht ReadOnly Feld.
	 * 
	 * @param selectedField
	 *            das selektierte Feld
	 * @param sectionList
	 *            Liste von Sections
	 * @return liefert das ermittelte nicht ReadOnly Feld zurück.
	 */
	private Control getPreviousControlFromSectionIfNull(MField selectedField, List<MSection> sectionList) {
		Control focussedControl = null;
		for (int position = sectionList.size() - 1; position >= 0; position--) {
			MSection section = sectionList.get(position);
			List<MField> tabList = section.getTabList();

			focussedControl = getPreviousFieldControl(tabList);
			if (focussedControl != null) {
				return focussedControl;
			}
		}
		return focussedControl;
	}

	/**
	 * Durchsucht die übergebene TabListe nach dem ersten nicht ReadOnly Feld und liefert es zurück.
	 *
	 * @param tabList
	 *            die zu durchsuchende TabListe
	 * @return liefert das ermittelte nicht ReadOnly Feld zurück.
	 */
	private Control getPreviousFieldControl(List<MField> tabList) {
		Control focussedControl = null;
		for (int position = tabList.size() - 1; position >= 0; position--) {
			MField field = tabList.get(position);
			// 1. mein Feld kommt nach dem aktuellen INIT_FIELD ##
			// 2. Mein Feld kommt nach dem aktuellen INIT_FIELD, auf nächster Section ##
			// 3. Mein Feld kommt vor dem aktuellen INIT_FIELD, auf vorheriger Section ##
			// 4. Mein Feld kommt vor dem aktuellen INIT_FIELD, auf gleicher Section ##
			if (!field.isReadOnly()) {
				focussedControl = ((AbstractValueAccessor) field.getValueAccessor()).getControl();
				focussedControl.setFocus();
				context.set(INIT_FIELD, field);
				return focussedControl;
			}
		}
		return focussedControl;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////
	////////////////// Ermittlung des nächsten Feldes ////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Ermittelt das in der Tab Reihenfolge nachfolgende Feld und selektiert dieses.
	 * <p>
	 * Wenn SelectAllControls gesetzt ist, wird beim Wechsel der Page die Registerkarte der Page selektiert. Falls sich der Benutzer im letzten Feld von der
	 * letzten Page befindet und SelectAllControls gesetzt ist, wird die Toolbar des Details selektiert.
	 *
	 * @param focussedControl
	 *            das aktuell selektierte Feld
	 */
	private void getNextField(Control focussedControl) {
		Preferences preferences = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE);
		boolean selectAllControls = (boolean) InstancePreferenceAccessor.getValue(preferences, ApplicationPreferences.SELECT_ALL_CONTROLS, DisplayType.CHECK,
				true, locale);

		List<MSection> sectionList = detail.getPageList();

		if (context.get(INIT_FIELD) instanceof MField) {
			// Wir prüfen, ob das ausgewählte Feld als initial Feld gespeichert wurde oder ob ein initial Feld gesetzt ist.
			if (focussedControl.equals(((AbstractValueAccessor) ((MField) context.get(INIT_FIELD)).getValueAccessor()).getControl())) {
				// Wir holen uns die Page des initial Feldes
				MSection initPage = sectionList.get(sectionList.indexOf(((MField) context.get(INIT_FIELD)).getmSection()));
				// Wir holen uns die Tab Liste der Initial Page
				List<MField> initTabList = initPage.getTabList();
				// Wir holen uns das initial Feld aus dem Context.
				MField initiField = (MField) context.get(INIT_FIELD);

				// Wir geben uns das initial Feld aus.
				if (LOG) {
					System.out.println(context.get(INIT_FIELD));
				}

				setNextControl(selectAllControls, sectionList, initPage, initTabList, initiField);

				// Wir geben uns das neue initial Feld aus.
				if (LOG) {
					System.out.println(context.get(INIT_FIELD));
				}
				return;
			}
		}

		for (MSection section : sectionList) {
			List<MField> tabList = section.getTabList();
			for (MField field : tabList) {
				if (((AbstractValueAccessor) field.getValueAccessor()).getControl() == focussedControl) {
					setNextControl(selectAllControls, sectionList, section, tabList, field);
					if (LOG) {
						System.out.println(context.get(INIT_FIELD));
					}
					return;
				}
			}
		}

	}

	/**
	 * Diese Methode sucht das nächste Control und gibt diesem Control den Fokus.
	 *
	 * @param selectAllControls
	 * @param sectionList
	 * @param initSection
	 * @param initTabList
	 * @param initiField
	 */
	private void setNextControl(boolean selectAllControls, List<MSection> sectionList, MSection initSection, List<MField> initTabList, MField initiField) {
		Control focussedControl;

		// Wir sind im letzten Feld der Page.
		if (initTabList.indexOf(initiField) == initTabList.size() - 1) {
			// Die Preference SelectAllControls is gesetzt.
			if (selectAllControls == true) {
				// Wir sind in der Letzten Section
				if (sectionList.indexOf(initSection) == sectionList.size() - 1) {
					MToolBar toolbarElements = partService.getActivePart().getToolbar();
					focussedControl = (Control) toolbarElements.getWidget();
					context.set(INIT_FIELD, focussedControl);
					// Wir prüfen nach dem Twistie der Section
				} else {
					focussedControl = sectionList.get(sectionList.indexOf(initSection) + 1).getSectionControl();
					context.set(INIT_FIELD, focussedControl);
				}
				// Wir sind in der Letzten Section
			} else {
				if (sectionList.get(sectionList.indexOf(initSection) + 1).getTabList().get(0).isReadOnly()) {
					focussedControl = ((AbstractValueAccessor) sectionList.get(sectionList.indexOf(initSection) + 1).getTabList().get(0).getValueAccessor())
							.getControl();
				} else {
					focussedControl = getNextControlFromSplitSectionList(sectionList, initiField);
				}
				// MField
			}
		} else {
			if (!initTabList.get(initTabList.indexOf(initiField) + 1).isReadOnly()) {
				focussedControl = ((AbstractValueAccessor) initTabList.get(initTabList.indexOf(initiField) + 1).getValueAccessor()).getControl();
			} else {
				focussedControl = getNextControlFromSplitSectionList(sectionList, initiField);
			}

			// MField
		}
		focussedControl.setFocus();
	}

	/**
	 * Setzt die Sections in die richtige Reihenfolge und ermittelt dann das zu selektierende nicht ReadOnly Feld.
	 * <p>
	 * 1. Zuerst werden die Felder, die hinter dem selektierten Feld und in der aktuellen Section stehen, geprüft. Beginnend mit dem ersten Feld nach dem
	 * aktuellen Feld <br>
	 * 2. Wenn kein Control gefunden wurde, werden die Sections, die hinter der aktuellen Section stehen geprüft. Beginnend mit dem Feld und ser Section am
	 * Anfang der Liste. <br>
	 * 3. Falls immer noch kein Control gefunden wurde, werden die Section vor der aktuellen Section geprüft. Beginnend mit dem ersten Feld in der ersten
	 * Section. <br>
	 * 4. Beim der aktuellen Section angekommen und es wurde immer noch kein Control gefunden, werden die Felder, die vor dem slektieren Feld stehen geprüft.
	 * Beginnend mit dem ersten Feld der Section.
	 * 
	 * @param sectionList
	 *            Liste aller Sections in Part.
	 * @param initField
	 *            das selektierte Feld.
	 * @return liefert das ermittelte nicht ReadOnly Feld zurück.
	 */
	private Control getNextControlFromSplitSectionList(List<MSection> sectionList, MField initField) {
		Control fc = null;
		List<MField> tabListFromSelectedFieldSection = initField.getmSection().getTabList();
		// [0,1,2,3,4,5,6,7,8,9] --> sublist(0,5) = [0,1,2,3,4]
		// Size = 10
		int indexOfSelectedField = tabListFromSelectedFieldSection.indexOf(initField);
		// Sind auf der selben Section nach meinem Feld noch unausgefüllte Required Fields?
		fc = getNextFieldControl(tabListFromSelectedFieldSection.subList(indexOfSelectedField + 1, tabListFromSelectedFieldSection.size()));
		if (fc != null) {
			return fc;
		}

		int indexOfSection = sectionList.indexOf(initField.getmSection());

		fc = getNextControlFromSectionIfNull(initField, sectionList.subList(indexOfSection + 1, sectionList.size()));
		if (fc != null) {
			return fc;
		}
		fc = getNextControlFromSectionIfNull(initField, sectionList.subList(0, indexOfSection));
		if (fc != null) {
			return fc;
		}
		// Sind auf der selben Section vor meinem Feld noch unausgefüllte Required Fields?
		fc = getNextFieldControl(tabListFromSelectedFieldSection.subList(0, indexOfSelectedField));
		return fc;
	}

	/**
	 * Prüft die Felder der Sections der übergebenen SectionList in der angegebenen Reihenfolge und ermittelt das zu selektierende nicht ReadOnly Feld.
	 * 
	 * @param selectedField
	 *            das selektierte Feld
	 * @param sectionList
	 *            Liste von Sections
	 * @return liefert das ermittelte nicht ReadOnly Feld zurück.
	 */
	private Control getNextControlFromSectionIfNull(MField selectedField, List<MSection> sectionList) {
		Control focussedControl = null;
		for (MSection section : sectionList) {
			List<MField> tabList = section.getTabList();

			focussedControl = getNextFieldControl(tabList);
			if (focussedControl != null) {
				return focussedControl;
			}
		}
		return focussedControl;
	}

	/**
	 * Durchsucht die übergebene TabListe nach dem ersten nicht ReadOnly Feld und liefert es zurück.
	 *
	 * @param tabList
	 *            die zu durchsuchende TabListe
	 * @return liefert das ermittelte nicht ReadOnly Feld zurück.
	 */
	private Control getNextFieldControl(List<MField> tabList) {
		Control focussedControl = null;
		for (MField field : tabList) {
			// 1. mein Feld kommt nach dem aktuellen INIT_FIELD ##
			// 2. Mein Feld kommt nach dem aktuellen INIT_FIELD, auf nächster Section ##
			// 3. Mein Feld kommt vor dem aktuellen INIT_FIELD, auf vorheriger Section ##
			// 4. Mein Feld kommt vor dem aktuellen INIT_FIELD, auf gleicher Section ##
			if (!field.isReadOnly()) {
				focussedControl = ((AbstractValueAccessor) field.getValueAccessor()).getControl();
				focussedControl.setFocus();
				context.set(INIT_FIELD, field);
				return focussedControl;
			}
		}
		return focussedControl;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////
	////////////////// Ermittlung des nächsten Pflichtfeldes //////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////

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
	private void getNextRequired(Control focussedControl) {
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
				}
			}
		}

		// Wir prüfen ob die Preference LookupEnterSelectsNextRequired nicht gesetzt ist und das Lookup offen ist.
		if (!lookupEnterSelectsNextRequired && popupOpen) {
			focussedControl = ((AbstractValueAccessor) selectedField.getValueAccessor()).getControl();
			focussedControl.setFocus();
			Lookup lookup = (Lookup) focussedControl;
			lookup.closePopup();
			return;
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

			fc = getNextRequiredControlFromSectionIfNull(selectedField, sectionList.subList(indexOfSection + 1, sectionList.size()));
			if (fc != null) {
				return;
			}
			fc = getNextRequiredControlFromSectionIfNull(selectedField, sectionList.subList(0, indexOfSection));
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
			}
		} else {
			for (MSection section : sectionList) {
				List<MField> tabList = section.getTabList();
				for (MField field : tabList) {
					if (field.isRequired() && field.getValue() == null && !field.isReadOnly()) {
						focussedControl = ((AbstractValueAccessor) field.getValueAccessor()).getControl();
						focussedControl.setFocus();
						context.set(INIT_FIELD, tabList.get(tabList.indexOf(field)));
						return;
					}
				}
			}
		}
	}

	/**
	 * Prüft die Felder der Sections der übergebenen SectionList in der angegebenen Reihenfolge und ermittelt das zu selektierende nicht ReadOnly Feld.
	 * 
	 * @param selectedField
	 *            das selektierte Feld
	 * @param sectionList
	 *            Liste von Sections
	 * @return liefert das ermittelte nicht ReadOnly Feld zurück.
	 */
	private Control getNextRequiredControlFromSectionIfNull(MField selectedField, List<MSection> sectionList) {
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

	/**
	 * Durchsucht die übergebene TabListe nach dem ersten nicht ReadOnly Feld und liefert es zurück.
	 *
	 * @param tabList
	 *            die zu durchsuchende TabListe
	 * @return liefert das ermittelte nicht ReadOnly Feld zurück.
	 */
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
				context.set(INIT_FIELD, field);
				return focussedControl;
			}
		}
		return focussedControl;
	}

}
