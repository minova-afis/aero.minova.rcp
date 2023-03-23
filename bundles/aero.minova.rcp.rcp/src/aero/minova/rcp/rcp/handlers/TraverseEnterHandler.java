
package aero.minova.rcp.rcp.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectCellCommand;
import org.eclipse.nebula.widgets.opal.textassist.TextAssist;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.Section;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.LookupValue;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MSection;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.rcp.accessor.DetailAccessor;
import aero.minova.rcp.rcp.accessor.SectionAccessor;
import aero.minova.rcp.rcp.parts.WFCDetailPart;
import aero.minova.rcp.widgets.LookupComposite;

/**
 * Dieser Handler reagiert auf das Enter KeyBinding im DetailPart. Er sucht das nächste leere Pflichtfeld und selktiert es. Sobald kein leeres Feld mehr
 * gefunden wird und der SaveDetailHandler Enabled ist, wird dieser ausgeführt.
 * 
 * @author bauer
 */
public class TraverseEnterHandler {

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.LOOKUP_ENTER_SELECTS_NEXT_REQUIRED)
	boolean lookupEnterSelectsNextRequired;

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.ENTER_SELECTS_FIRST_REQUIRED)
	boolean enterSelectsFirstRequired;

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
			if (((DetailAccessor) mDetail.getDetailAccessor()).getSelectedControl() != null) {
				// aktuell selektiertes Feld holen
				Control focussedControl = ((DetailAccessor) mDetail.getDetailAccessor()).getSelectedControl();

				// Ist ein Popup offen?
				if (focussedControl instanceof LookupComposite) {
					LookupComposite lookup = (LookupComposite) focussedControl;
					// Wir holen uns den Status des Popup des Lookup
					popupOpen = lookup.popupIsOpen();
				}
				// nächstes Pflichtfeld suchen und fokussieren
				getNextRequired(focussedControl, mDetail, popupOpen);
			}
		}

		// Bei offenem Lookup-Popup wollen wir nicht speichern
		if (!popupOpen) {
			// ParameterizedCommand des SaveDetailsHandlers erstellen und ausführen
			ParameterizedCommand cmd = commandService.createCommand(Constants.AERO_MINOVA_RCP_RCP_COMMAND_SAVEDETAIL, null);
			handlerService.executeHandler(cmd);
		}
	}

	/**
	 * Diese Methode ermittelt das nächste leere Pflichtfeld nach folgendem Muster: <br>
	 * <br>
	 * 1. Prüfen, ob LookupEnterSelectsNextRequired gesetzt ist und ein Lookup mit offenen Popup selektiert ist:<br>
	 * <br>
	 * LookupEnterSelectsNextRequired ist nicht gesetzt: <br>
	 * Bei einem Lookup mit offenen Popup wird der ausgewählte Wert festgesetzt und das Lookup bleibt selektiert. <br>
	 * <br>
	 * LookupEnterSelectsNextRequired ist gesetzt:<br>
	 * Bei einem Lookup mit offenen Popup wird der ausgewählte Wert festgesetzt und das nächste leere Pflichtfeld selektiert. <br>
	 * <br>
	 * 2. Prüfen, ob EnterSelectsFirstRequired gesetzt ist und kein Lookup mit offenem Popup selektiert ist: <br>
	 * <br>
	 * EnterSelectsFirstRequired ist gesetzt: <br>
	 * Begonnen mit der ersten Section, wird das erste leere Pflichtfeld ermittelt und selektiert. <br>
	 * <br>
	 * EnterSelectsFirstRequired ist nicht gesetzt und es ist kein Lookup mit offenem Popup selektiert: <br>
	 * - Beginnend mit der Section, in der das aktuell selektierte Control sich befindet, wird das nächste leere Pflichtfeld ermittelt. Dabei werden nur die
	 * Controls nach dem aktuell selektiertem geprüft.<br>
	 * - Wenn kein Pflichtfeld gefunden wurde, werden die Sections, die nach der Section des selektierten Controls kommen, durchsucht.<br>
	 * - Falls am Ende des Parts angekommen und immer noch kein Pflichtfeld selektiert wurde, wird mit den Sections vor dem des selektierten Control weiter
	 * gemacht. Beginnend mit der ersten section <br>
	 * - Bei der Section des aktuell selektierten Controls angekommen ohne ein Pflichtfeld selktiert zu haben, werden nun die Controls in der Section und vor
	 * dem selktierten Control durchsucht. <br>
	 * <br>
	 * Wenn kein Pflichtfeld gefunden wird, bleibt das aktuelle Pflichtfeld selektiert.
	 * 
	 * @param control
	 *            fokussiertes Control
	 * @param mDetail
	 * @param popupOpen
	 */
	private void getNextRequired(Control control, MDetail mDetail, boolean popupOpen) {

		Control focussedControl = null;

		if (control.getParent() instanceof TextAssist || control.getParent() instanceof LookupComposite) {
			focussedControl = control.getParent();
		} else {
			focussedControl = control;
		}

		// Wir holen uns das MField des selektierten Felds.
		Section fcSection = null;
		if (focussedControl instanceof NatTable) {
			fcSection = (Section) focussedControl.getData(Constants.GRID_DATA_SECTION);
			((NatTable) focussedControl).commitAndCloseActiveCellEditor();
		} else {
			fcSection = ((SectionAccessor) ((MField) control.getData(Constants.CONTROL_FIELD)).getMSection().getSectionAccessor()).getSection();
		}

		if (focussedControl instanceof LookupComposite) {
			LookupComposite lookup = (LookupComposite) focussedControl;
			// Wir holen uns den Status des Popup des Lookup
			popupOpen = lookup.popupIsOpen();
		}

		Composite comp = (Composite) fcSection.getClient();

		// Wir prüfen ob die Preference LookupEnterSelectsNextRequired nicht gesetzt ist und das Lookup offen ist.
		if (!lookupEnterSelectsNextRequired && popupOpen) {
			focussedControl.setFocus();
			if (focussedControl instanceof LookupComposite) {
				LookupComposite lookup = (LookupComposite) focussedControl;
				lookup.closePopup();
				MField field = (MField) focussedControl.getData(Constants.CONTROL_FIELD);
				setLookupValue(field, lookup);
			}
			return;
		}

		if (!enterSelectsFirstRequired || popupOpen) {

			boolean cellSelected = false;
			if (focussedControl instanceof NatTable) {
				cellSelected = getNextRequiredNatTableCell(focussedControl, true);
			}

			LookupComposite lookup = null;
			if (focussedControl instanceof LookupComposite) {
				MField selectedField = (MField) focussedControl.getData(Constants.CONTROL_FIELD);
				lookup = (LookupComposite) focussedControl;
				if (popupOpen) {
					setLookupValue(selectedField, lookup);
				} else {
					selectedField.setValue(selectedField.getValue(), false);
				}
			}

			if (!cellSelected && focussedControl instanceof NatTable) {
				NatTable natTable = (NatTable) focussedControl;
				((SelectionLayer) natTable.getData(Constants.GRID_DATA_SELECTIONLAYER)).clear();
			}

			if (!cellSelected && !comp.isDisposed()) {
				List<Control> tabListFromFocussedControlSection = Arrays.asList(comp.getTabList());
				List<Section> sectionList = ((DetailAccessor) mDetail.getDetailAccessor()).getSectionList();

				// [0,1,2,3,4,5,6,7,8,9] --> sublist(0,5) = [0,1,2,3,4]
				int indexFocussedControl = tabListFromFocussedControlSection.indexOf(focussedControl);
				Control fc = getNextRequiredControl(
						tabListFromFocussedControlSection.subList(indexFocussedControl + 1, tabListFromFocussedControlSection.size()));
				if (fc != null) {
					return;
				}

				int indexFocussedControlSection = sectionList.indexOf(fcSection);
				fc = getNextRequiredControlOtherSection(sectionList.subList(indexFocussedControlSection + 1, sectionList.size()));
				if (fc != null) {
					return;
				}

				fc = getNextRequiredControlOtherSection(sectionList.subList(0, indexFocussedControlSection + 1));
				if (fc != null) {
					return;
				}

				fc = getNextRequiredControl(tabListFromFocussedControlSection.subList(0, indexFocussedControl + 1));
				if (fc == null && focussedControl instanceof LookupComposite) {
					lookup = (LookupComposite) focussedControl;
					lookup.closePopup();
				}
			}
		} else {
			LookupComposite lookup = null;
			if (focussedControl instanceof LookupComposite) {
				MField selectedField = (MField) focussedControl.getData(Constants.CONTROL_FIELD);
				lookup = (LookupComposite) focussedControl;
				if (popupOpen) {
					setLookupValue(selectedField, lookup);
				} else {
					selectedField.setValue(selectedField.getValue(), false);
				}
			}

			for (MSection mSection : mDetail.getMSectionList()) {
				Section section = ((SectionAccessor) mSection.getSectionAccessor()).getSection();
				Composite compo = (Composite) section.getClient();
				List<Control> tabList = Arrays.asList(compo.getTabList());
				Control fc = getNextRequiredControl(tabList);

				if (fc != null) {
					if (focussedControl instanceof LookupComposite) {
						lookup = (LookupComposite) focussedControl;
						lookup.closePopup();
					}
					return;
				}
			}
		}
	}

	/**
	 * Ermittelt das erste leere Pflichtfeld aus der übergebenen Liste von Controls.
	 * 
	 * @param tabList
	 *            Liste der Controls in einer Section.
	 * @return
	 */
	private Control getNextRequiredControl(List<Control> tabList) {
		Control fc = null;

		for (Control control : tabList) {
			if (!(control instanceof NatTable)) {
				MField field = (MField) control.getData(Constants.CONTROL_FIELD);
				if (field.getValue() == null && field.isRequired() && !field.isReadOnly()) {
					Section section = ((SectionAccessor) field.getMSection().getSectionAccessor()).getSection();
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
				boolean natTableSelected = getNextRequiredNatTableCell(control, false);
				if (natTableSelected) {
					Section section = (Section) control.getData(Constants.GRID_DATA_SECTION);
					if (!section.isExpanded()) {
						// Section öffnen
						section.setExpanded(true);
					}
					fc = control;
					fc.setFocus();
					return fc;
				}
			}
		}

		return fc;
	}

	/**
	 * Emittelt das erste leere Pflichtfeld in einer der Section aus der übergebenen Liste.
	 * 
	 * @param focussedControl
	 *            das fokussierte Control
	 * @param sectionList
	 *            Liste mit Sections
	 * @return
	 */
	private Control getNextRequiredControlOtherSection(List<Section> sectionList) {
		for (Section section : sectionList) {
			Composite compo = (Composite) section.getClient();
			List<Control> tabList = Arrays.asList(compo.getTabList());
			Control fc = getNextRequiredControl(tabList);
			if (fc != null) {
				return fc;
			}
		}

		return null;
	}

	/**
	 * Sucht die übergebene NatTable nach einem leeren Pflichtfeld durch. Wenn eins gefunden wird, wird es selektiert und der Fokus auf die NatTable gesetzt. Im
	 * Fall, dass die NatTable fokussiert ist und countFromSelectedCell = true ist, wird von der Position der selektierten Zelle begonnen.
	 * 
	 * @param focussedControl
	 *            die NatTable, die nach einem Pflichtfeld geprüft werden soll
	 * @param countFromSelectedCell
	 *            bestimmt, ob von der selektierten Zelle aus nach dem nächsten Pflichtfeld gesucht werden soll
	 * @return true, wenn eine Zelle selektiert wurde
	 */
	private boolean getNextRequiredNatTableCell(Control focussedControl, boolean countFromSelectedCell) {
		NatTable natTable = (NatTable) focussedControl;
		Table dataTable = (Table) natTable.getData(Constants.GRID_DATA_DATATABLE);
		SelectionLayer selectionLayer = (SelectionLayer) natTable.getData(Constants.GRID_DATA_SELECTIONLAYER);
		int irs = -1;
		int ics = 0;

		// Prüfen, ob die NatTable selektiert ist und ob von der selektierten Zelle aus das nächste Pflichtfeld ermittelt werden soll
		if (natTable.isFocusControl() && countFromSelectedCell) {
			// Selektierte Zelle suchen und die Row und Column Position setzen
			irs = selectionLayer.getSelectionAnchor().getRowPosition();
			ics = selectionLayer.getSelectionAnchor().getColumnPosition();

			// Nächstes leeres Pflichtfeld nach der selektierten Zelle in der selben Row ermitteln
			for (int ic = ics + 1; ic < selectionLayer.getColumnCount(); ic++) {
				if (selectEmptyRequiredCell(natTable, dataTable, selectionLayer, irs, ics)) {
					return true;
				}
			}
		}

		// Leeres Pflichtfeld ermitteln
		// irs kann 0 oder der Rowindex der selektierten Zelle sein
		for (int ir = irs + 1; ir < selectionLayer.getRowCount(); ir++) {
			for (int ic = -1; ic < selectionLayer.getColumnCount(); ic++) {
				if (selectEmptyRequiredCell(natTable, dataTable, selectionLayer, ir, ic)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean selectEmptyRequiredCell(NatTable natTable, Table dataTable, SelectionLayer selectionLayer, int irs, int ic) {
		List<Column> visibleColumns = new ArrayList<>();
		for (Column column : dataTable.getColumns()) {
			if (column.isVisible()) {
				visibleColumns.add(column);
			}
		}

		for (int i = ic + 1; i < selectionLayer.getColumnCount(); i++) {
			int index = selectionLayer.getColumnIndexByPosition(i);
			Column column = dataTable.getColumns().get(index);

			if (column.isRequired() && (dataTable.getValue(index, irs) == null || dataTable.getValue(index, irs).getValue() == null)) {
				natTable.setFocus();
				return natTable.doCommand(new SelectCellCommand(selectionLayer, i, irs, false, false));
			}
		}

		return false;

	}

	private void setLookupValue(MField field, LookupComposite lookup) {
		LookupValue lv = null;
		if (lookup.getTable().getSelectionIndex() > 0) {
			lv = lookup.getPopupValues().get(lookup.getTable().getSelectionIndex());
		} else {
			lv = lookup.getPopupValues().get(0);
		}
		field.setValue(lv, true);
	}

}