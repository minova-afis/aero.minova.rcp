
package aero.minova.rcp.rcp.parts;

import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_HEIGHT;
import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_WIDTH;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_LEFT;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_TOP;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_LOCALE;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_PROPERTY;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.di.extensions.Service;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.css.swt.CSSSWTConstants;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.edit.action.MouseEditAction;
import org.eclipse.nebula.widgets.nattable.edit.config.DefaultEditBindings;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsEventLayer;
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
import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.CellPainterMouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.nebula.widgets.opal.textassist.TextAssist;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.Twistie;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.form.model.xsd.Grid;
import aero.minova.rcp.form.model.xsd.Head;
import aero.minova.rcp.form.model.xsd.Onclick;
import aero.minova.rcp.form.model.xsd.Page;
import aero.minova.rcp.form.model.xsd.Wizard;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.form.MBooleanField;
import aero.minova.rcp.model.form.MDateTimeField;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MLookupField;
import aero.minova.rcp.model.form.MNumberField;
import aero.minova.rcp.model.form.MSection;
import aero.minova.rcp.model.form.MShortDateField;
import aero.minova.rcp.model.form.MShortTimeField;
import aero.minova.rcp.model.form.MTextField;
import aero.minova.rcp.model.form.ModelToViewModel;
import aero.minova.rcp.model.helper.IHelper;
import aero.minova.rcp.nattable.data.MinovaColumnPropertyAccessor;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.rcp.fields.BooleanField;
import aero.minova.rcp.rcp.fields.DateTimeField;
import aero.minova.rcp.rcp.fields.LookupField;
import aero.minova.rcp.rcp.fields.NumberField;
import aero.minova.rcp.rcp.fields.ShortDateField;
import aero.minova.rcp.rcp.fields.ShortTimeField;
import aero.minova.rcp.rcp.fields.TextField;
import aero.minova.rcp.rcp.nattable.MinovaGridConfiguration;
import aero.minova.rcp.rcp.util.ImageUtil;
import aero.minova.rcp.rcp.util.WFCDetailCASRequestsUtil;
import aero.minova.rcp.rcp.widgets.Lookup;
import aero.minova.rcp.rcp.widgets.TriStateCheckBoxPainter;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;

@SuppressWarnings("restriction")
public class WFCDetailPart extends WFCFormPart {

	private static final int MARGIN_SECTION = 8;
	private static final int SECTION_WIDTH = 4 * COLUMN_WIDTH + 3 * MARGIN_LEFT + 2 * MARGIN_SECTION + 50; // 4 Spalten = 5 Zwischenräume
	@Inject
	protected UISynchronize sync;

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.TIMEZONE)
	String timezone;

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.SELECT_ALL_CONTROLS)
	boolean selectAllControls;

	@Inject
	@Service
	private List<IHelper> helperlist;

	private FormToolkit formToolkit;

	private Composite composite;

	private MDetail detail = new MDetail();

	private WFCDetailCASRequestsUtil casRequestsUtil = null;

	@Inject
	private TranslationService translationService;
	private Locale locale;

	@Inject
	EPartService partService;

	@Inject
	private ECommandService commandService;

	@Inject
	private EHandlerService handlerService;
	private LocalResourceManager resManager;

	@PostConstruct
	public void postConstruct(Composite parent, IEclipseContext partContext) {
		resManager = new LocalResourceManager(JFaceResources.getResources(), parent);
		composite = parent;
		formToolkit = new FormToolkit(parent.getDisplay());
		if (getForm(parent) == null) {
			return;
		}
		layoutForm(parent, partContext);

		// erzeuge die util Methoden mit DI
		IEclipseContext localContext = EclipseContextFactory.create();
		localContext.set(Form.class, form);

		localContext.setParent(partContext);

		// erstellen der Util-Klasse, welche sämtliche funktionen der Detailansicht steuert
		casRequestsUtil = ContextInjectionFactory.make(WFCDetailCASRequestsUtil.class, localContext);
		casRequestsUtil.initializeCasRequestUtil(getDetail(), perspective);
		partContext.set("Detail_Width", SECTION_WIDTH);
		translate(composite);
	}

	private static class HeadOrPageOrGridWrapper {
		private Object headOrPage;
		public boolean isHead = false;

		public HeadOrPageOrGridWrapper(Object headOrPage) {
			this.headOrPage = headOrPage;
			if (headOrPage instanceof Head) {
				isHead = true;
			}
		}

		public String getTranslationText() {
			if (isHead) {
				return "@Head";
			} else if (headOrPage instanceof Grid) {
				return ((Grid) headOrPage).getTitle();
			}
			return ((Page) headOrPage).getText();
		}

		public List<Object> getFieldOrGrid() {
			if (isHead) {
				return ((Head) headOrPage).getFieldOrGrid();
			} else if (headOrPage instanceof Grid) {
				// es existieren keine Felder, nur eine Table
				List<Object> mylistList = new ArrayList<>();
				mylistList.add(headOrPage);
				return mylistList;
			}
			return ((Page) headOrPage).getFieldOrGrid();
		}

	}

	private void layoutForm(Composite parent, IEclipseContext context) {
		parent.setLayout(new RowLayout(SWT.VERTICAL));
		for (Object headOrPage : form.getDetail().getHeadAndPageAndGrid()) {
			HeadOrPageOrGridWrapper wrapper = new HeadOrPageOrGridWrapper(headOrPage);
			layoutSection(parent, wrapper, context);
		}

		// Setzen der TabListe der Sections.
		parent.setTabList(parent.getChildren());
		// Holen des Parts
		Composite part = parent.getParent();
		// Setzen der TabListe des Parts. Dabei bestimmt SelectAllControls, ob die Toolbar mit selektiert wird.
		part.setTabList(getTabListForPart(part));
		// Wir setzen eine leere TabListe für die Perspektive, damit nicht durch die Anwendung mit Tab navigiert werden kann.
		List<Control> tabList = new ArrayList<>();
		part.getParent().setTabList(listToArray(tabList));

		// Helper-Klasse initialisieren
		if (form.getHelperClass() != null) {
			String helperClass = form.getHelperClass();
			if (!Objects.equals(helperClass, helperlist.get(0).getClass().getName())) {
				// TODO Übersetzung!
				throw new RuntimeException("Helperklasse nicht eindeutig! Bitte Prüfen");
			}
			IHelper iHelper = helperlist.get(0);
			iHelper.setControls(getDetail());
			getDetail().setHelper(iHelper);
		}

	}

	/**
	 * Diese Methode bekommt einen Composite übergeben, und erstellt aus dem übergenen Objekt ein Section. Diese Sektion ist entweder der Head (Kopfdaten) oder
	 * eine OptionPage die sich unterhalb der Kopfdaten eingliedert. Zusätzlich wird ein TraverseListener übergeben, der das Verhalten für TAB und Enter
	 * festlegt.
	 *
	 * @param parent
	 * @param headOrPage
	 * @param traverseListener
	 */

	private void layoutSection(Composite parent, HeadOrPageOrGridWrapper headOrPage, IEclipseContext context) {
		RowData headLayoutData = new RowData();
		Section section;
		Control sectionControl = null;
		if (headOrPage.isHead) {
			section = formToolkit.createSection(parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		} else {
			section = formToolkit.createSection(parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED | ExpandableComposite.TWISTIE);
			sectionControl = section.getChildren()[0];
		}

		headLayoutData.width = SECTION_WIDTH;

		section.setData(TRANSLATE_PROPERTY, headOrPage.getTranslationText());
		section.setLayoutData(headLayoutData);
		section.setText(headOrPage.getTranslationText());

		// Client Area
		Composite composite = formToolkit.createComposite(section);
		composite.setLayout(new FormLayout());
		composite.setData(CSSSWTConstants.CSS_CLASS_NAME_KEY, "TEST");
		formToolkit.paintBordersFor(composite);
		section.setClient(composite);

		// Wir erstellen die HEAD Section des Details.
		MSection mSection = new MSection(true, "open", detail, section.getText(), sectionControl, section);
		// Button erstellen, falls vorhanden
		createButton(headOrPage, section);
		// Erstellen der Field des Section.
		createFields(composite, headOrPage, mSection);
		// Sortieren der Fields nach Tab-Index.
		sortTabList(mSection);
		// Setzen der TabListe für die einzelnen Sections.
		composite.setTabList(getTabListForSectionComposite(mSection, composite));
		// Setzen der TabListe der Sections im Part.
		composite.getParent().setTabList(getTabListForSection(composite.getParent()));

		// MSection wird zum MDetail hinzugefügt.
		detail.addPage(mSection);

	}

	/**
	 * Erstellt einen oder mehrere Button auf der übergebenen Section. Die Button werden in der ausgelesenen Reihelfolge erstellt und in eine Reihe gesetzt.
	 *
	 * @param composite2
	 * @param headOPOGWrapper
	 * @param mSection
	 * @param section
	 */
	private void createButton(HeadOrPageOrGridWrapper headOPOGWrapper, Section section) {
		if (headOPOGWrapper.isHead || headOPOGWrapper.headOrPage instanceof Grid) {
			return;
		}

		final ToolBar bar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL | SWT.RIGHT | SWT.NO_FOCUS);

		Page page = (Page) headOPOGWrapper.headOrPage;
		for (aero.minova.rcp.form.model.xsd.Button btn : page.getButton()) {
			final ToolItem item = new ToolItem(bar, SWT.PUSH);
			item.setData(btn);
			item.setEnabled(btn.isEnabled());
			if (btn.getText() != null) {
				item.setText(translationService.translate(btn.getText(), null));
				item.setToolTipText(translationService.translate(btn.getText(), null));
			}

			Object event = findEventForID(btn.getId());
			if (event instanceof Onclick) {
				Onclick onclick = (Onclick) event;
				item.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						// TODO: Andere procedures/bindings/instances auswerten

						Wizard wizard = getWizard(onclick);
						if (wizard != null) {
							Map<String, String> parameter = Map.of(Constants.CONTROL_WIZARD, wizard.getWizardname());
							ParameterizedCommand command = commandService.createCommand("aero.minova.rcp.rcp.command.dynamicbuttoncommand", parameter);
							handlerService.executeHandler(command);
						}
					}
				});
			}

			if (btn.getIcon() != null && btn.getIcon().trim().length() > 0) {
				final ImageDescriptor buttonImageDescriptor = ImageUtil.getImageDescriptorFromImagesBundle(btn.getIcon());
				Image buttonImage = resManager.createImage(buttonImageDescriptor);
				item.setImage(buttonImage);
			}
		}
		section.setTextClient(bar);

	}

	private Object findEventForID(String id) {
		for (Onclick onclick : form.getEvents().getOnclick()) {
			if (onclick.getRefid().equals(id)) {
				return onclick;
			}
		}
		// TODO: Onbinder und ValueChange implementieren
		return null;
	}

	private Wizard getWizard(Onclick onclick) {
		for (Object o : onclick.getBinderOrProcedureOrInstance()) {
			if (o instanceof Wizard) {
				return (Wizard) o;
			}
		}
		return null;
	}

	/**
	 * Sortiert die Tab Reihenfolge der Fields in der Section(Page)
	 *
	 * @param mSection
	 *            die Section in der die Fields sortiert werden müssen
	 * @param traverseListener
	 *            der zuzuweisende TraverseListener für die Fields
	 */
	private void sortTabList(MSection mSection) {
		List<MField> tabList = mSection.getTabList();
		Collections.sort(tabList, new Comparator<>() {

			@Override
			public int compare(MField f1, MField f2) {
				if (f1.getTabIndex() == f2.getTabIndex()) {
					return 0;
				} else if (f1.getTabIndex() < f2.getTabIndex()) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		mSection.setTabList(tabList);

	}

	/**
	 * Setzt alle Elemente aus der übergebenen Liste in einen Array
	 *
	 * @param tabList
	 * @return Array mit Controls
	 */
	private Control[] listToArray(List<Control> tabList) {
		Control[] tabArray = new Control[tabList.size()];
		int i = 0;
		while (i < tabList.size()) {
			tabArray[i] = tabList.get(i);
			i++;
		}
		return tabArray;
	}

	/**
	 * Gibt einen Array mit den Controls für die TabListe der Section zurück. Wenn SelectAllControls gesetzt ist, wird das SectionControl(der Twistie) mit in
	 * den Array gesetzt.
	 *
	 * @param composite
	 *            die Setion, von der die TabListe gesetzt werden soll.
	 * @return Array mit Controls
	 */
	private Control[] getTabListForSection(Composite composite) {
		List<Control> tabList = new ArrayList<>();

		if (selectAllControls && composite.getChildren()[0] instanceof Twistie) {
			for (Control child : composite.getChildren()) {
				if (child instanceof ToolBar) {
					tabList.add(1, child);
				} else if (child instanceof Label) {
					continue;
				} else {
					tabList.add(child);
				}
			}
		} else {
			for (Control child : composite.getChildren()) {
				if (child instanceof ToolBar) {
					tabList.add(1, child);
				} else if (child instanceof Twistie || child instanceof Label) {
					continue;
				} else {
					tabList.add(child);
				}
			}
		}
		return listToArray(tabList);
	}

	/**
	 * Gibt einen Array mit den Controls für die TabListe des Parts zurück. Wenn SelectAllControls gesetzt ist, wird die Toolbar mit in den Array gesetzt.
	 *
	 * @param composite
	 *            die Setion, von der die TabListe gesetzt werden soll.
	 * @return Array mit Controls
	 */
	private Control[] getTabListForPart(Composite composite) {
		List<Control> tabList = new ArrayList<>();

		if (selectAllControls) {
			int i = 0;
			while (i < composite.getChildren().length) {
				tabList.add(composite.getChildren()[i]);
				i++;
			}
		}
		return listToArray(tabList);
	}

	/**
	 * Gibt einen Array mit den Controls für die TabListe des Composites der Section zurück.
	 *
	 * @param mSection
	 *            der Section
	 * @param composite
	 *            der Section
	 * @return Array mit Controls
	 */
	private Control[] getTabListForSectionComposite(MSection mSection, Composite composite) {

		List<Control> tabList = new ArrayList<>();

		Control[] compositeChilds = composite.getChildren();
		for (Control control : compositeChilds) {
			if (control instanceof Lookup || control instanceof TextAssist || control instanceof Text) {
				MField field = (MField) control.getData(Constants.CONTROL_FIELD);
				if (!field.isReadOnly()) {
					tabList.add(control);
				}
			}
		}

		return listToArray(tabList);
	}

	/**
	 * Erstellt die Field einer Section.
	 *
	 * @param composite
	 *            der parent des Fields
	 * @param headOrPage
	 *            bestimmt ob die Fields nach den Regeln des Heads erstellt werden oder der einer Page.
	 * @param page
	 *            die Section deren Fields erstellt werden.
	 */
	private void createFields(Composite composite, HeadOrPageOrGridWrapper headOrPage, MSection page) {
		int row = 0;
		int column = 0;
		int width;
		for (Object fieldOrGrid : headOrPage.getFieldOrGrid()) {
			if (!(fieldOrGrid instanceof Field)) {
				if (fieldOrGrid instanceof Grid) {
					createNatTableGrid((Grid) fieldOrGrid, composite);
				}
				continue;
			}
			Field field = (Field) fieldOrGrid;
			MField f = ModelToViewModel.convert(field);
			getDetail().putField(f);

			if (!field.isVisible()) {
				continue; // nur sichtbare Felder
			}
			width = getWidth(field);
			if (column + width > 4) {
				column = 0;
				row++;
			}
			createField(composite, f, row, column);
			f.setmPage(page);
			page.addTabField(f);

			column += width;
			if (!headOrPage.isHead) {
				row += getExtraHeight(field);
			}
		}
		addBottonMargin(composite, row + 1, column);
	}

	/**
	 * Diese Methode erstellt eine NatTable als Grid im Detail.
	 *
	 * @param grid
	 * @param composite
	 *            - Section auf die wir die NatTable erstellen
	 */
	private void createNatTableGrid(Grid grid, Composite composite) {
		// Button erstellen, abhängig von Grid

		Table dataTable = dataFormService.getTableFromGrid(grid);
		NatTable natTable = createNatTable(composite, grid, dataTable);


	}

	public NatTable createNatTable(Composite parent, Grid grid, Table table) {
		NatTable natTable;
		// Datenmodel für die Eingaben
		ConfigRegistry configRegistry = new ConfigRegistry();

		// create the body stack
		EventList<Row> eventList = GlazedLists.eventList(table.getRows());
		SortedList<Row> sortedList = new SortedList<>(eventList, null);
		MinovaColumnPropertyAccessor columnPropertyAccessor = new MinovaColumnPropertyAccessor(table, grid);
		columnPropertyAccessor.initPropertyNames(translationService);

		IDataProvider bodyDataProvider = new ListDataProvider<>(sortedList, columnPropertyAccessor);

		DataLayer bodyDataLayer = new DataLayer(bodyDataProvider);

		bodyDataLayer.setConfigLabelAccumulator(new ColumnLabelAccumulator());

		GlazedListsEventLayer<Row> eventLayer = new GlazedListsEventLayer<>(bodyDataLayer, sortedList);

		ColumnReorderLayer columnReorderLayer = new ColumnReorderLayer(eventLayer);
		ColumnHideShowLayer columnHideShowLayer = new ColumnHideShowLayer(columnReorderLayer);
		SelectionLayer selectionLayer = new SelectionLayer(columnHideShowLayer);
		ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);

		// as the selection mouse bindings are registered for the region label GridRegion.BODY we need to set that region label to the viewport so the selection
		// via mouse is working correctly
		viewportLayer.setRegionName(GridRegion.BODY);

		// build the column header layer
		IDataProvider columnHeaderDataProvider = new DefaultColumnHeaderDataProvider(columnPropertyAccessor.getPropertyNames(),
				columnPropertyAccessor.getTableHeadersMap());
		DataLayer columnHeaderDataLayer = new DefaultColumnHeaderDataLayer(columnHeaderDataProvider);
		ColumnHeaderLayer columnHeaderLayer = new ColumnHeaderLayer(columnHeaderDataLayer, viewportLayer, selectionLayer);

		// build the row header layer
		IDataProvider rowHeaderDataProvider = new DefaultRowHeaderDataProvider(bodyDataProvider);
		DataLayer rowHeaderDataLayer = new DefaultRowHeaderDataLayer(rowHeaderDataProvider);
		ILayer rowHeaderLayer = new RowHeaderLayer(rowHeaderDataLayer, viewportLayer, selectionLayer);

		// build the corner layer
		IDataProvider cornerDataProvider = new DefaultCornerDataProvider(columnHeaderDataProvider, rowHeaderDataProvider);
		DataLayer cornerDataLayer = new DataLayer(cornerDataProvider);
		ILayer cornerLayer = new CornerLayer(cornerDataLayer, rowHeaderLayer, columnHeaderLayer);

		// build the grid layer
		GridLayer gridLayer = new GridLayer(viewportLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer);

		natTable = new NatTable(parent, gridLayer, false);

		// as the autoconfiguration of the NatTable is turned off, we have to add the DefaultNatTableStyleConfiguration and the ConfigRegistry manually
		natTable.setConfigRegistry(configRegistry);
		natTable.addConfiguration(new DefaultNatTableStyleConfiguration());
		natTable.addConfiguration(new SingleClickSortConfiguration());

		natTable.addConfiguration(new MinovaGridConfiguration(table.getColumns(), translationService, grid));

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

		FormData fd = new FormData();
		fd.width = SECTION_WIDTH;
		fd.height = COLUMN_HEIGHT * 3;

		natTable.setLayoutData(fd);
		natTable.configure();
		return natTable;
	}

	private int getExtraHeight(Field field) {
		if (field.getNumberRowsSpanned() != null && field.getNumberRowsSpanned().length() > 0) {
			return Integer.parseInt(field.getNumberRowsSpanned()) - 1;
		}
		return 0;
	}

	private void addBottonMargin(Composite composite, int row, int column) {
		// Abstand nach unten
		Label spacing = new Label(composite, SWT.NONE);
		FormData spacingFormData = new FormData();
		spacingFormData.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT + MARGIN_TOP);
		spacingFormData.left = new FormAttachment(composite, MARGIN_LEFT * (column + 1) + (column + 1) * COLUMN_WIDTH);
		spacingFormData.height = 0;
		spacing.setLayoutData(spacingFormData);
	}

	private void createField(Composite composite, MField field, int row, int column) {
		if (field instanceof MBooleanField) {
			BooleanField.create(composite, field, row, column, locale, perspective);
		} else if (field instanceof MNumberField) {
			NumberField.create(composite, (MNumberField) field, row, column, locale, perspective);
		} else if (field instanceof MDateTimeField) {
			DateTimeField.create(composite, field, row, column, locale, timezone, perspective);
		} else if (field instanceof MShortDateField) {
			ShortDateField.create(composite, field, row, column, locale, timezone, perspective);
		} else if (field instanceof MShortTimeField) {
			ShortTimeField.create(composite, field, row, column, locale, timezone, perspective);
		} else if (field instanceof MLookupField) {
			LookupField.create(composite, field, row, column, locale, perspective);
		} else if (field instanceof MTextField) {
			TextField.create(composite, field, row, column, perspective);
		}
	}

	@Inject
	@Optional
	private void getNotified(@Named(TranslationService.LOCALE) Locale s) {
		this.locale = s;
		if (translationService != null && composite != null) {
			translate(composite);
		}
	}

	private void translate(Composite composite) {
		for (Control control : composite.getChildren()) {
			if (control.getData(TRANSLATE_PROPERTY) != null) {
				String property = (String) control.getData(TRANSLATE_PROPERTY);
				String value = translationService.translate(property, null);
				if (control instanceof ExpandableComposite) {
					ExpandableComposite expandableComposite = (ExpandableComposite) control;
					expandableComposite.setText(value);
					translate((Composite) expandableComposite.getClient());
				} else if (control instanceof Label) {
					Label l = ((Label) control);
					Object data = l.getData(LookupField.AERO_MINOVA_RCP_LOOKUP);
					if (data != null) {
						// TODO aus den Preferences Laden
						value = value + " ▼";
					}
					((Label) control).setText(value);
				} else if (control instanceof Button) {
					((Button) control).setText(value);
				}
				if (control instanceof Composite) {
					translate((Composite) control);
				}
			}
		}
		for (Control control : composite.getChildren()) {
			if (control.getData(TRANSLATE_LOCALE) != null) {
				control.setData(TRANSLATE_LOCALE, locale);
			}
		}
	}

	private int getWidth(Field field) {
		BigInteger numberColumnsSpanned = field.getNumberColumnsSpanned();
		return numberColumnsSpanned == null ? 2 : numberColumnsSpanned.intValue();
	}

	public MDetail getDetail() {
		return detail;
	}

}