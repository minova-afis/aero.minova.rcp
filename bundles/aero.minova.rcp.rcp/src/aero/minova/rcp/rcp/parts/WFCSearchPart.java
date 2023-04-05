package aero.minova.rcp.rcp.parts;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.edit.action.MouseEditAction;
import org.eclipse.nebula.widgets.nattable.edit.command.UpdateDataCommand;
import org.eclipse.nebula.widgets.nattable.edit.command.UpdateDataCommandHandler;
import org.eclipse.nebula.widgets.nattable.edit.config.DefaultEditBindings;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.CellPainterMouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.FilterValue;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.preferencewindow.control.CustomLocale;
import aero.minova.rcp.rcp.nattable.MinovaColumnConfiguration;
import aero.minova.rcp.rcp.nattable.MinovaSearchConfiguration;
import aero.minova.rcp.rcp.nattable.TriStateCheckBoxPainter;
import aero.minova.rcp.util.DateTimeUtil;
import aero.minova.rcp.util.DateUtil;
import aero.minova.rcp.util.TimeUtil;
import ca.odell.glazedlists.SortedList;

public class WFCSearchPart extends WFCNattablePart {

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.TIMEZONE)
	String timezone;

	protected SortedList<Row> sortedList;

	@Override
	public void createComposite(Composite parent) {
		new FormToolkit(parent.getDisplay());
		getForm();
		if (form == null) {
			return;
		}

		// "&" Spalte erstellen
		aero.minova.rcp.form.model.xsd.Column xsdColumn = new aero.minova.rcp.form.model.xsd.Column();
		xsdColumn.setBoolean(Boolean.FALSE);
		xsdColumn.setLabel("&");
		xsdColumn.setName("&");
		form.getIndexView().getColumn().add(0, xsdColumn);

		data = dataFormService.getTableFromFormIndex(form);
		getData().addRow();
		// Wir setzen die Verundung auf false im Default-Fall!
		getData().getRows().get(getData().getRows().size() - 1).setValue(new Value(false), 0);

		parent.setLayout(new GridLayout());
		mPerspective.getContext().set(Constants.SEARCH_TABLE, getData());

		natTable = createNatTable(parent, form, getData());

		sortedList = bodyLayerStack.getSortedList();

		restorePrefs(Constants.LAST_STATE);
	}

	@Override
	public MinovaColumnConfiguration createColumnConfiguration(Table table) {
		return new MinovaSearchConfiguration(table.getColumns(), form);
	}

	@Override
	protected void addNattableConfiguration(NatTable natTable) {

		bodyLayerStack.getBodyDataLayer().unregisterCommandHandler(UpdateDataCommand.class);
		bodyLayerStack.getBodyDataLayer().registerCommandHandler(new UpdateDataCommandHandler(bodyLayerStack.getBodyDataLayer()) {
			@Override
			protected boolean doCommand(UpdateDataCommand command) {
				if (super.doCommand(command)) {
					Object newValue = command.getNewValue();
					if (getData().getRows().size() - 1 == command.getRowPosition() && newValue != null) {
						Table dummy = getData();
						dummy.addRow();
						// Datentablle muss angepasst weden, weil die beiden Listen sonst divergieren
						dummy.getRows().get(dummy.getRows().size() - 1).setValue(new Value(false), 0);
						sortedList.add(dummy.getRows().get(dummy.getRows().size() - 1));
					}
					return true;
				}
				return false;
			}
		});

		// Hinzufügen von BindingActions, damit in der TriStateCheckBoxPainter der Mouselistener anschlägt!
		natTable.addConfiguration(new DefaultEditBindings() {

			@Override
			public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
				MouseEditAction mouseEditAction = new MouseEditAction();
				super.configureUiBindings(uiBindingRegistry);
				uiBindingRegistry.registerFirstSingleClickBinding(
						new CellPainterMouseEventMatcher(GridRegion.BODY, MouseEventMatcher.LEFT_BUTTON, TriStateCheckBoxPainter.class), mouseEditAction);
			}

		});

	}

	@Override
	protected boolean useGroupBy() {
		return false;
	}

	@Override
	protected boolean useSortingHeader() {
		return false;
	}

	@Override
	protected boolean useSummaryRow() {
		return false;
	}

	@Inject
	@Optional
	public void revertSearch(@UIEventTopic(Constants.BROKER_REVERTSEARCHTABLE) MPart mPart) {
		if (!mPart.equals(this.mPart)) {
			return;
		}

		natTable.commitAndCloseActiveCellEditor();

		// Alle Einträge entfernen
		getData().getRows().clear();
		sortedList.clear();

		// Neue Zeile hinzufügen (erste Spalte darf nicht null sein)
		getData().addRow();
		getData().getRows().get(0).setValue(new Value(false), 0);
		sortedList.add(getData().getRows().get(0));
	}

	@Inject
	@Optional
	public void deleteSearchRow(@UIEventTopic(Constants.BROKER_DELETEROWSEARCHTABLE) String id) {
		Set<Range> selectedRowPositions = bodyLayerStack.getSelectionLayer().getSelectedRowPositions();
		List<Row> rows2delete = new ArrayList<>();
		for (Range range : selectedRowPositions) {
			for (int i = range.start; i < range.end; i++) {
				rows2delete.add(sortedList.get(i));
			}
		}

		natTable.commitAndCloseActiveCellEditor();
		deleteSearchRows(rows2delete);
		refreshNatTable();
	}

	public void deleteSearchRows(List<Row> rows) {
		sortedList.removeAll(rows);
		getData().getRows().removeAll(rows);
		if (sortedList.isEmpty()) {
			Table dummy = getData();
			dummy.addRow();
			getData().getRows().get(0).setValue(new Value(false), 0);
			sortedList.add(dummy.getRows().get(dummy.getRows().size() - 1));
		}
	}

	// Instants aktualisieren, damit der angezeigte Wert zur Nutzereingabe passt
	public void updateUserInput() {
		for (Row r : getData().getRows()) {
			for (int i = 0; i < getData().getColumnCount(); i++) {
				Value v = r.getValue(i);
				if (v instanceof FilterValue fv && fv.getFilterValue() != null && ((FilterValue) v).getFilterValue().getInstantValue() != null) {
					Instant inst;
					if (form.getIndexView().getColumn().get(i).getShortTime() != null) {
						inst = TimeUtil.getTime(fv.getUserInputWithoutOperator());
					} else if (form.getIndexView().getColumn().get(i).getShortDate() != null) {
						inst = DateUtil.getDate(fv.getUserInputWithoutOperator());
					} else {
						inst = DateTimeUtil.getDateTime(LocalDateTime.now(ZoneId.of(timezone)).toInstant(ZoneOffset.UTC), fv.getUserInputWithoutOperator(),
								CustomLocale.getLocale(), timezone);
					}

					r.setValue(new FilterValue(fv.getOperatorValue(), inst, fv.getUserInput()), i);
				}
			}
		}

		sortedList.clear();
		sortedList.addAll(getData().getRows());
	}
}
