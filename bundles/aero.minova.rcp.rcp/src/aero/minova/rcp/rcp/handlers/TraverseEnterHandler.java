
package aero.minova.rcp.rcp.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectCellCommand;
import org.eclipse.nebula.widgets.opal.textassist.TextAssist;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.Twistie;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.LookupValue;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.preferencewindow.builder.DisplayType;
import aero.minova.rcp.preferencewindow.builder.InstancePreferenceAccessor;
import aero.minova.rcp.rcp.parts.WFCDetailPart;
import aero.minova.rcp.rcp.widgets.Lookup;

/**
 * Dieser Handler reagiert auf das Enter KeyBinding im DetailPart. Er sucht das nächste leere Pflichtfeld und selktiert es. Sobald kein leeres Feld mehr
 * gefunden wird und der SaveDetailHandler Enabled ist, wird dieser ausgeführt.
 * 
 * @author bauer
 */
public class TraverseEnterHandler {

	@Inject
	@Named(TranslationService.LOCALE)
	Locale locale;

	@Inject
	EPartService partService;

	@Inject
	ECommandService commandService;

	@Inject
	EHandlerService handlerService;

	@Execute
	public void execute() {
		MPart part = partService.getActivePart();

		boolean popupOpen = false;

		if (part.getObject() instanceof WFCDetailPart) {
			MDetail mDetail = ((WFCDetailPart) part.getObject()).getDetail();
			if (mDetail.getSelectedControl() != null) {
				// aktuell selektiertes Feld holen
				Control focussedControl = mDetail.getSelectedControl();

				// Ist ein Popup offen?
				if (focussedControl instanceof Lookup) {
					Lookup lookup = (Lookup) focussedControl;
					// Wir holen uns den Status des Popup des Lookup
					popupOpen = lookup.popupIsOpen();
				}
				// nächstes Pflichtfeld suchen und fokussieren
				getNextRequired(focussedControl, mDetail);
			}
		}

		// Bei offenem Lookup-Popup wollen wir nicht speichern
		if (!popupOpen) {
			// SaveDetailHandler holen
			IHandler handler = commandService.getCommand("aero.minova.rcp.rcp.command.savedetail").getHandler();
			// prüfen, ob der SaveDetailHandler enabled ist
			if (handler.isEnabled()) {
				// ParameterizedCommand des SaveDetailsHandlers erstellen und ausführen
				ParameterizedCommand cmd = commandService.createCommand("aero.minova.rcp.rcp.command.savedetail", null);
				handlerService.executeHandler(cmd);
				return;
			}
		}
	}

	private void getNextRequired(Control control, MDetail mDetail) {
		Preferences preferences = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE);
		boolean lookupEnterSelectsNextRequired = (boolean) InstancePreferenceAccessor.getValue(preferences,
				ApplicationPreferences.LOOKUP_ENTER_SELECTS_NEXT_REQUIRED, DisplayType.CHECK, true, locale);
		boolean enterSelectsFirstRequired = (boolean) InstancePreferenceAccessor.getValue(preferences, ApplicationPreferences.ENTER_SELECTS_FIRST_REQUIRED,
				DisplayType.CHECK, true, locale);

		Control focussedControl = null;
		boolean popupOpen = false;

		if (control.getParent() instanceof TextAssist || control.getParent() instanceof Lookup) {
			focussedControl = control.getParent();
			if (focussedControl instanceof Lookup) {
				Lookup lookup = (Lookup) focussedControl;
				// Wir holen uns den Status des Popup des Lookup
				popupOpen = lookup.popupIsOpen();
			}
		} else {
			focussedControl = control;
		}

		// Wir holen uns das MField des selektierten Felds.
		List<Section> sectionList = mDetail.getSectionList();

		Control fc = null;
		Composite comp = null;
		Section fcSection = null;

		if (focussedControl instanceof NatTable) {
			fcSection = (Section) focussedControl.getData(Constants.GRID_DATA_SECTION);
		} else {
			fcSection = ((MField) control.getData(Constants.CONTROL_FIELD)).getmSection().getSection();
		}

		if (fcSection.getChildren()[0] instanceof Twistie) {
			comp = (Composite) fcSection.getChildren()[2];
		} else {
			comp = (Composite) fcSection.getChildren()[1];
		}

		// Wir prüfen ob die Preference LookupEnterSelectsNextRequired nicht gesetzt ist und das Lookup offen ist.
		if (!lookupEnterSelectsNextRequired && popupOpen) {
			focussedControl.setFocus();
			if (focussedControl instanceof Lookup) {
				Lookup lookup = (Lookup) focussedControl;
				lookup.closePopup();
				MField field = (MField) focussedControl.getData(Constants.CONTROL_FIELD);
				setLookupValue(field, lookup);
			}
			return;
		}

		if (!enterSelectsFirstRequired || popupOpen) {

			boolean cellSelected = false;
			if (focussedControl instanceof NatTable) {
				cellSelected = getNextRequiredNatTableCell(focussedControl);
			}

			if (!cellSelected) {
				Control[] tabListArrayFromFocussedControlSection = comp.getTabList();
				List<Control> tabListFromFocussedControlSection = arrayToList(tabListArrayFromFocussedControlSection);

				// [0,1,2,3,4,5,6,7,8,9] --> sublist(0,5) = [0,1,2,3,4]
				int indexFocussedControl = tabListFromFocussedControlSection.indexOf(focussedControl);
				fc = getNextRequiredControl(tabListFromFocussedControlSection.subList(indexFocussedControl + 1, tabListFromFocussedControlSection.size()));
				if (fc != null) {
					return;
				}

				int indexFocussedControlSection = sectionList.indexOf(comp.getParent());
				fc = getNextRequiredControlOtherSection(focussedControl, sectionList.subList(indexFocussedControlSection + 1, sectionList.size()));
				if (fc != null) {
					return;
				}

				fc = getNextRequiredControlOtherSection(focussedControl, sectionList.subList(0, indexFocussedControlSection));
				if (fc != null) {
					return;
				}

				fc = getNextRequiredControl(tabListFromFocussedControlSection.subList(0, indexFocussedControl));
				if (fc != null) {
					if (focussedControl instanceof Lookup) {
						Lookup lookup = (Lookup) focussedControl;
						lookup.closePopup();
					}
					return;
				}
			}
		} else {
			for (Section section : sectionList) {
				Composite compo = null;
				if (section.getChildren()[0] instanceof Twistie) {
					compo = (Composite) section.getChildren()[2];
				} else {
					compo = (Composite) section.getChildren()[1];
				}
				List<Control> tabList = arrayToList(compo.getTabList());
				fc = getNextRequiredControl(tabList);
				if (fc != null) {
					if (focussedControl instanceof Lookup) {
						Lookup lookup = (Lookup) focussedControl;
						lookup.closePopup();
					}
					return;
				} else {
					continue;
				}
			}
		}
	}

	private Control getNextRequiredControl(List<Control> tabList) {
		Control fc = null;

		for (Control control : tabList) {
			if (!(control instanceof NatTable)) {
				MField field = (MField) control.getData(Constants.CONTROL_FIELD);
				if (field.getValue() == null && field.isRequired() && !field.isReadOnly()) {
					Section section = field.getmSection().getSection();
					// Prüfen, ob die Section in der das nächste Pflichtfeld sich befindet geschlossen ist
					if (!section.isExpanded()) {
						// Section öffnen
						section.setExpanded(true);
					}
					fc = control;
					fc.setFocus();
					return fc;
				}
			} else {
				boolean natTableSelected = getNextRequiredNatTableCell(control);
				if (natTableSelected) {
					Section section = (Section) control.getData(Constants.GRID_DATA_SECTION);
					if (!section.isExpanded()) {
						// Section öffnen
						section.setExpanded(true);
					}
					fc = control;
					return fc;
				}
			}
		}

		return fc;
	}

	private Control getNextRequiredControlOtherSection(Control focussedControl, List<Section> sectionList) {
		Control fc = null;
		Composite compo = null;
		for (Section section : sectionList) {
			if (section.getChildren()[0] instanceof Twistie) {
				compo = (Composite) section.getChildren()[2];
			} else {
				compo = (Composite) section.getChildren()[1];
			}
			List<Control> tabList = arrayToList(compo.getTabList());
			fc = getNextRequiredControl(tabList);
			if (fc != null) {
				return fc;
			}
		}

		return fc;
	}

	private boolean getNextRequiredNatTableCell(Control focussedControl) {
		NatTable natTable = (NatTable) focussedControl;
		Table dataTable = (Table) natTable.getData(Constants.GRID_DATA_DATATABLE);
		int irs = 1;
		int ics = 1;

		if (natTable.isFocusControl()) {
			for (int ir = 1; ir < natTable.getRowCount(); ir++) {
				for (int ic = 1; ic < natTable.getColumnCount(); ic++) {
					ICellPainter painter = natTable.getCellPainter(ic, ir, natTable.getCellByPosition(ic, ir), natTable.getConfigRegistry());
					if (natTable.getCellByPosition(ic, ir).getConfigLabels().hasLabel("selectionAnchor")) {
						if(ic == 2) {
							if(ir == natTable.getRowCount()) {
								irs = ir;
								ics = ic;
							} else {
								irs = ir + 1;							
							}
						} else {
							irs = ir;
						}
						break;
					}

				}
			}
		}

		for (int ir = irs; ir < natTable.getRowCount(); ir++) {
			for (int ic = ics; ic < natTable.getColumnCount(); ic++) {
				for (Column column : dataTable.getColumns()) {
					if (column.getName().equals(dataTable.getColumnName(ic + 1)) && column.isRequired()) {
						if ((dataTable.getRows().get(ir - 1).getValue(dataTable.getColumnIndex(column.getName())) == null
								|| dataTable.getRows().get(ir - 1).getValue(dataTable.getColumnIndex(column.getName())).getValue() == null)
								&& !natTable.getCellByPosition(ic, ir).getConfigLabels().hasLabel(Constants.SELECTED_ANCHOR_LABEL)) {
							SelectionLayer selectionLayer = (SelectionLayer) natTable.getData(Constants.GRID_DATA_SELECTIONLAYER);
							natTable.setFocus();
							int ici = ic - 1;
							int iri = ir - 1;
							natTable.doCommand(new SelectCellCommand(selectionLayer, ici, iri, false, false));
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private List<Control> arrayToList(Control[] tabListArray) {
		List<Control> tabList = new ArrayList<>();
		for (Control control : tabListArray) {
			tabList.add(control);
		}
		return tabList;
	}

	private void setLookupValue(MField field, Lookup lookup) {
		LookupValue lv = null;
		if (lookup.getTable().getSelectionIndex() > 0) {
			lv = lookup.getPopupValues().get(lookup.getTable().getSelectionIndex());
		} else {
			lv = lookup.getPopupValues().get(0);
		}
		field.setValue(lv, true);
	}

}