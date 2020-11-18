package aero.minova.rcp.rcp.nattable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsSortModel;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByHeaderLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByHeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultRowHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultColumnHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultRowHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.config.DefaultRowSelectionLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.sort.SortHeaderLayer;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.tree.config.TreeLayerExpandCollapseKeyBindings;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.widgets.Composite;

import aero.minova.rcp.form.model.xsd.Column;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.nattable.data.MinovaColumnPropertyAccessor;
import aero.minova.rcp.rcp.nattable.NatTableWrapper.BodyLayerStack;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;

public class NatTableSearchPart {

	private NatTable natTable;
	private BodyLayerStack<Row> bodyLayerStack;
	private IEclipseContext context;

	public void createNatTable(Composite parent, Form form, Table table) {

		Map<String, String> tableHeadersMap = new HashMap<>();
		List<Column> columns = form.getIndexView().getColumn();
		String[] propertyNames = new String[columns.size()];
		int i = 0;
		for (Column column : columns) {
			tableHeadersMap.put(column.getName(), column.getLabel());
			propertyNames[i++] = column.getName();
		}

		// Datenmodel für die Eingaben
		ConfigRegistry configRegistry = new ConfigRegistry();
		IColumnPropertyAccessor<Row> columnPropertyAccessor = new MinovaColumnPropertyAccessor(table);

		
		// create the body stack
		EventList<Row> eventList = GlazedLists.eventList(table.getRows());
		SortedList<Row> sortedList = new SortedList<>(eventList, null);
		
		IDataProvider bodyDataProvider = new ListDataProvider<Row>(sortedList, columnPropertyAccessor);
		
		DataLayer bodyDataLayer = new DataLayer(bodyDataProvider);
		// selection layer
		SelectionLayer selectionLayer = new SelectionLayer(bodyDataLayer);
		// Enable full selection for rows
		selectionLayer.addConfiguration(new DefaultRowSelectionLayerConfiguration());
		
		//view port layer
		ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);

		// as the selection mouse bindings are registered for the region label
		// GridRegion.BODY
		// we need to set that region label to the viewport so the selection via mouse
		// is working correctly
		viewportLayer.setRegionName(GridRegion.BODY);
		

		// build the column header layer
		IDataProvider columnHeaderDataProvider = new DefaultColumnHeaderDataProvider(propertyNames, tableHeadersMap);
		DataLayer columnHeaderDataLayer = new DefaultColumnHeaderDataLayer(columnHeaderDataProvider);
		ColumnHeaderLayer columnHeaderLayer = new ColumnHeaderLayer(columnHeaderDataLayer,
				bodyLayerStack,
				bodyLayerStack.getSelectionLayer());

		SortHeaderLayer<Row> sortHeaderLayer = new SortHeaderLayer<>(columnHeaderLayer,
				new GlazedListsSortModel<>(bodyLayerStack.getSortedList(), columnPropertyAccessor, configRegistry,
						columnHeaderDataLayer));

		// build the row header layer
		IDataProvider rowHeaderDataProvider = new DefaultRowHeaderDataProvider(bodyLayerStack.getBodyDataProvider());
		DataLayer rowHeaderDataLayer = new DefaultRowHeaderDataLayer(rowHeaderDataProvider);
		ILayer rowHeaderLayer = new RowHeaderLayer(rowHeaderDataLayer, bodyLayerStack,
				bodyLayerStack.getSelectionLayer());

		// build the corner layer
		IDataProvider cornerDataProvider = new DefaultCornerDataProvider(columnHeaderDataProvider,
				rowHeaderDataProvider);
		DataLayer cornerDataLayer = new DataLayer(cornerDataProvider);
		ILayer cornerLayer = new CornerLayer(cornerDataLayer, rowHeaderLayer, columnHeaderLayer);

		// build the grid layer
		GridLayer gridLayer = new GridLayer(viewportLayer, sortHeaderLayer, rowHeaderLayer, cornerLayer);

		// set the group by header on top of the grid
		CompositeLayer compositeGridLayer = new CompositeLayer(1, 2);
		final GroupByHeaderLayer groupByHeaderLayer = new GroupByHeaderLayer(bodyLayerStack.getGroupByModel(),
				gridLayer, columnHeaderDataProvider, columnHeaderLayer);
		compositeGridLayer.setChildLayer(GroupByHeaderLayer.GROUP_BY_REGION, groupByHeaderLayer, 0, 0);
		compositeGridLayer.setChildLayer("Grid", gridLayer, 0, 1);

		natTable = new NatTable(parent, compositeGridLayer, false);



		// as the autoconfiguration of the NatTable is turned off, we have to
		// add the DefaultNatTableStyleConfiguration and the ConfigRegistry
		// manually
		natTable.setConfigRegistry(configRegistry);
		natTable.addConfiguration(new DefaultNatTableStyleConfiguration());
		natTable.addConfiguration(new SingleClickSortConfiguration());
//		natTable.registerCommandHandler(new DisplayPersistenceDialogCommandHandler(natTable));
//
//		// add group by configuration
		natTable.addConfiguration(new GroupByHeaderMenuConfiguration(natTable, groupByHeaderLayer));
		// adds the key bindings that allow space bar to be pressed to
        // expand/collapse tree nodes
		natTable.addConfiguration(new TreeLayerExpandCollapseKeyBindings(bodyLayerStack.getTreeLayer(),
				bodyLayerStack.getSelectionLayer()));
		// natTable.addConfiguration(new HeaderMenuConfiguration(natTable) {
//			@Override
//			protected PopupMenuBuilder createCornerMenu(NatTable natTable) {
//				return super.createCornerMenu(natTable).withStateManagerMenuItemProvider()
//						.withMenuItemProvider(new IMenuItemProvider() {
//
//							@Override
//							public void addMenuItem(NatTable natTable, Menu popupMenu) {
//								MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
//								menuItem.setText("Toggle Group By Header"); //$NON-NLS-1$
//								menuItem.setEnabled(true);
//
//								menuItem.addSelectionL	istener(new SelectionAdapter() {
//									@Override
//									public void widgetSelected(SelectionEvent event) {
//										groupByHeaderLayer.setVisible(!groupByHeaderLayer.isVisible());
//									}
//								});
//							}
//						}).withMenuItemProvider(new IMenuItemProvider() {
//
//							@Override
//							public void addMenuItem(final NatTable natTable, Menu popupMenu) {
//								MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
//								menuItem.setText("Collapse All"); //$NON-NLS-1$
//								menuItem.setEnabled(true);
//
//								menuItem.addSelectionListener(new SelectionAdapter() {
//									@Override
//									public void widgetSelected(SelectionEvent event) {
//										natTable.doCommand(new TreeCollapseAllCommand());
//									}
//								});
//							}
//						}).withMenuItemProvider(new IMenuItemProvider() {
//
//							@Override
//							public void addMenuItem(final NatTable natTable, Menu popupMenu) {
//								MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
//								menuItem.setText("Expand All"); //$NON-NLS-1$
//								menuItem.setEnabled(true);
//
//								menuItem.addSelectionListener(new SelectionAdapter() {
//									@Override
//									public void widgetSelected(SelectionEvent event) {
//										natTable.doCommand(new TreeExpandAllCommand());
//									}
//								});
//							}
//						}).withMenuItemProvider(new IMenuItemProvider() {
//
//							@Override
//							public void addMenuItem(final NatTable natTable, Menu popupMenu) {
//								MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
//								menuItem.setText("Expand to Level 2"); //$NON-NLS-1$
//								menuItem.setEnabled(true);
//
//								menuItem.addSelectionListener(new SelectionAdapter() {
//									@Override
//									public void widgetSelected(SelectionEvent event) {
//										natTable.doCommand(new TreeExpandToLevelCommand(2));
//									}
//								});
//							}
//						});
//			}
//		});
//


		natTable.addConfiguration(new MinovaEditConfiguration(table.getColumns()));

		natTable.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
//		

//		DataLayer bodyDataLayer = new DataLayer(bodyDataProvider);
//
//		SelectionLayer selectionLayer = new SelectionLayer(bodyDataLayer);
//		ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);
//
//		IDataProvider columnHeaderDataProvider = new DefaultColumnHeaderDataProvider(
//				propertyNames,propertyToLabelMap);
//		DataLayer columnHeaderDataLayer = new DefaultColumnHeaderDataLayer(columnHeaderDataProvider);
//		ILayer columnHeaderLayer = new ColumnHeaderLayer(columnHeaderDataLayer, viewportLayer, selectionLayer);
//
//		SortHeaderLayer<Person> sortHeaderLayer = new SortHeaderLayer<>(columnHeaderLayer,
//				new GlazedListsSortModel<>(sortedList, accessor, configRegistry, columnHeaderDataLayer));
//
//		CompositeLayer compositeLayer = new CompositeLayer(1, 2);
//		compositeLayer.setChildLayer(GridRegion.COLUMN_HEADER, sortHeaderLayer, 0, 0);
//		compositeLayer.setChildLayer(GridRegion.BODY, viewportLayer, 0, 1);

//		NatTable natTable = new NatTable(parent, compositeLayer, false);
//
//		natTable.setConfigRegistry(configRegistry);
//		natTable.addConfiguration(new DefaultNatTableStyleConfiguration());
		natTable.addConfiguration(new SingleClickSortConfiguration());

		natTable.configure();
		// set the modern theme to visualize the summary better

//		ThemeConfiguration modernTheme = new ModernNatTableThemeConfiguration();
//		modernTheme.addThemeExtension(new ModernGroupByThemeExtension());
//
//		natTable.setTheme(modernTheme);


	}

	public void updateData(List<Row> list) {
//		bodyLayerStack.getSortedList().clear();
//		bodyLayerStack.getSortedList().addAll(list);
	}
}
