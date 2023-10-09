
package aero.minova.rcp.rcp.parts;

import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_PROPERTY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBException;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.PersistState;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.UIEvents.EventTags;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.prefs.BackingStoreException;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.css.widgets.DetailLayout;
import aero.minova.rcp.css.widgets.MinovaSection;
import aero.minova.rcp.css.widgets.MinovaSectionData;
import aero.minova.rcp.dataservice.ImageUtil;
import aero.minova.rcp.dataservice.XmlProcessor;
import aero.minova.rcp.form.model.xsd.Browser;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.form.model.xsd.Grid;
import aero.minova.rcp.form.model.xsd.Head;
import aero.minova.rcp.form.setup.util.XBSUtil;
import aero.minova.rcp.form.setup.xbs.Node;
import aero.minova.rcp.form.setup.xbs.Preferences;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.form.MBooleanField;
import aero.minova.rcp.model.form.MBrowser;
import aero.minova.rcp.model.form.MDateTimeField;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MGrid;
import aero.minova.rcp.model.form.MLabelText;
import aero.minova.rcp.model.form.MLookupField;
import aero.minova.rcp.model.form.MNumberField;
import aero.minova.rcp.model.form.MParamStringField;
import aero.minova.rcp.model.form.MPeriodField;
import aero.minova.rcp.model.form.MQuantityField;
import aero.minova.rcp.model.form.MRadioField;
import aero.minova.rcp.model.form.MSection;
import aero.minova.rcp.model.form.MShortDateField;
import aero.minova.rcp.model.form.MShortTimeField;
import aero.minova.rcp.model.form.MTextField;
import aero.minova.rcp.model.form.ModelToViewModel;
import aero.minova.rcp.model.helper.IHelper;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.rcp.accessor.BrowserAccessor;
import aero.minova.rcp.rcp.accessor.DetailAccessor;
import aero.minova.rcp.rcp.accessor.GridAccessor;
import aero.minova.rcp.rcp.accessor.SectionAccessor;
import aero.minova.rcp.rcp.fields.BooleanField;
import aero.minova.rcp.rcp.fields.DateTimeField;
import aero.minova.rcp.rcp.fields.FieldUtil;
import aero.minova.rcp.rcp.fields.LabelTextField;
import aero.minova.rcp.rcp.fields.LookupField;
import aero.minova.rcp.rcp.fields.NumberField;
import aero.minova.rcp.rcp.fields.PeriodField;
import aero.minova.rcp.rcp.fields.QuantityField;
import aero.minova.rcp.rcp.fields.RadioField;
import aero.minova.rcp.rcp.fields.ShortDateField;
import aero.minova.rcp.rcp.fields.ShortTimeField;
import aero.minova.rcp.rcp.fields.TextField;
import aero.minova.rcp.rcp.util.CreateButtonInDetailUtil;
import aero.minova.rcp.rcp.util.DirtyFlagUtil;
import aero.minova.rcp.rcp.util.SectionWrapper;
import aero.minova.rcp.rcp.util.TabUtil;
import aero.minova.rcp.rcp.util.TranslateUtil;
import aero.minova.rcp.rcp.util.WFCDetailCASRequestsUtil;
import aero.minova.rcp.rcp.widgets.BrowserSection;
import aero.minova.rcp.rcp.widgets.SectionGrid;
import aero.minova.rcp.util.ScreenshotUtil;

public class WFCDetailPart extends WFCFormPart {

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.TIMEZONE)
	String timezone;

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.SELECT_ALL_CONTROLS)
	boolean selectAllControls;

	@Inject
	@Preference
	private IEclipsePreferences prefs;

	IEclipsePreferences prefsDetailSections = InstanceScope.INSTANCE.getNode(Constants.PREFERENCES_DETAILSECTIONS);

	private FormToolkit formToolkit;

	private Composite composite;

	private MDetail mDetail = new MDetail();

	@Inject
	private MPart mPart;

	@Inject
	private TranslationService translationService;
	private Locale locale;

	private LocalResourceManager resManager;
	private WFCDetailCASRequestsUtil casRequestsUtil;
	private DirtyFlagUtil dirtyFlagUtil;

	private int detailWidth;

	ILog logger = Platform.getLog(this.getClass());

	MApplication mApplication;

	private List<SectionGrid> sectionGrids = new ArrayList<>();
	private List<BrowserSection> browserSections = new ArrayList<>();
	private ScrolledComposite scrolled;

	private MinovaSection headSection;

	private int sectionCount = -1;

	private CreateButtonInDetailUtil buttonUtil;

	@PostConstruct
	public void postConstruct(Composite parent, MWindow window, MApplication mApp) {

		resManager = new LocalResourceManager(JFaceResources.getResources(), parent);
		composite = parent;
		formToolkit = new FormToolkit(parent.getDisplay());
		mApplication = mApp;
		getForm();

		if (form == null) {
			return;
		}

		if (form.getDetail() == null) {
			mPart.setVisible(false);
			return;
		}

		// DiryFlagUtil erstellen und in Kontext setzten
		dirtyFlagUtil = ContextInjectionFactory.make(DirtyFlagUtil.class, mPart.getContext());
		mPerspective.getContext().set(DirtyFlagUtil.class, dirtyFlagUtil);

		buttonUtil = ContextInjectionFactory.make(CreateButtonInDetailUtil.class, mPart.getContext());
		buttonUtil.setMDetail(mDetail);
		buttonUtil.setForm(form);

		layoutForm(parent);

		mDetail.setBooking("booking".equalsIgnoreCase(form.getDetail().getType()));
		mDetail.setDetailAccessor(new DetailAccessor(mDetail));
		ContextInjectionFactory.inject(mDetail.getDetailAccessor(), mPerspective.getContext()); // In Context, damit Injection verfügbar ist
		mPerspective.getContext().set(MDetail.class, mDetail); // MDetail per Injection ermöglichen

		mDetail.setClearAfterSave(form.getDetail().isClearAfterSave());

		// Label und Icon aus Maske setzten
		mPart.setIconURI(ImageUtil.retrieveIcon(form.getIcon(), false));
		mPart.setLabel(form.getTitle());
		mPart.updateLocalization();

		// Erstellen der Util-Klasse, welche sämtliche funktionen der Detailansicht steuert
		casRequestsUtil = ContextInjectionFactory.make(WFCDetailCASRequestsUtil.class, mPerspective.getContext());
		casRequestsUtil.initializeCasRequestUtil(getDetail(), mPerspective, this);
		mPerspective.getContext().set(WFCDetailCASRequestsUtil.class, casRequestsUtil);
		mPerspective.getContext().set(Constants.DETAIL_WIDTH, detailWidth);
		TranslateUtil.translate(composite, translationService, locale);

		// Helpers erst initialisieren, wenn casRequestsUtil erstellt wurde
		for (IHelper helper : mDetail.getHelpers()) {
			helper.setControls(mDetail);
		}
		// Default Werte aus der Maske in Feldern wieder füllen.
		// WICHTIG: Zuerst Default Werte setzen und dann die Werte aus der XBS!
		casRequestsUtil.setDefaultValues();
		// In XBS gegebene Felder füllen
		casRequestsUtil.setValuesAccordingToXBS();
	}

	@Inject
	@Optional
	/**
	 * Sobald das Toolbar-Widget des Detail-Parts erstellt wird Rechtsklick-Menü zum Erstellen eines Screenshots hinzufügen
	 * 
	 * @param event
	 */
	public void subscribeTopicWidgetChange(@UIEventTopic(UIEvents.UIElement.TOPIC_WIDGET) Event event) {

		// Nur SET-Event der Detail-Toolbar herausfiltern
		Object element = event.getProperty(EventTags.ELEMENT);
		if (!UIEvents.isSET(event) || !(element instanceof MToolBar) || !((MToolBar) element).getElementId().equals(Constants.DETAIL_TOOLBAR)) {
			return;
		}

		// Screenshot-Möglichkeit zu Toolbar hinzufügen
		Control toolbar = (Control) mPart.getToolbar().getWidget();
		if (toolbar != null && toolbar.getListeners(SWT.MenuDetect).length == 0) { // Nur einmal Menü hinzufügen
			toolbar.addMenuDetectListener(e -> ScreenshotUtil.menuDetectAction(e, toolbar,
					mPerspective.getPersistedState().get(Constants.FORM_NAME).replace(".xml", "") + "_Toolbar", translationService));
		}
	}

	private void layoutForm(Composite parent) {

		// Wir wollen eine horizontale Scrollbar, damit auch bei breiten Details alles erreichbar ist
		scrolled = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolled.setShowFocusedControl(true);
		Composite wrap = new Composite(scrolled, SWT.NO_SCROLL);
		DetailLayout detailLayout = new DetailLayout();
		wrap.setLayout(detailLayout);
		parent.setData(Constants.DETAIL_COMPOSITE, wrap);
		mPerspective.getContext().set(Constants.DETAIL_LAYOUT, detailLayout);

		// Abschnitte der Hauptmaske und OPs erstellen
		for (Object headOrPage : form.getDetail().getHeadAndPageAndGrid()) {
			SectionWrapper wrapper = new SectionWrapper(headOrPage);
			layoutSection(wrap, wrapper);
		}
		loadOptionPages(wrap);

		scrolled.setContent(wrap);
		scrolled.setExpandHorizontal(true);
		scrolled.setExpandVertical(true);

		scrolled.addListener(SWT.Resize, event -> adjustScrollbar(scrolled, wrap));

		// Setzen der TabListe der Sections.
		parent.setTabList(parent.getChildren());
		// Holen des Parts
		Composite part = parent.getParent();
		// Setzen der TabListe des Parts. Dabei bestimmt SelectAllControls, ob die Toolbar mit selektiert wird.
		part.setTabList(TabUtil.getTabListForPart(part, selectAllControls));
		// Wir setzen eine leere TabListe für die Perspektive, damit nicht durch die Anwendung mit Tab navigiert werden kann.
		part.getParent().setTabList(new Control[0]);

		// Helper-Klasse initialisieren
		initializeHelper(form.getHelperClass());
	}

	private void adjustScrollbar(ScrolledComposite scrolled, Composite wrap) {
		int height = scrolled.getClientArea().height;
		int width = scrolled.getClientArea().width;

		scrolled.setMinSize(wrap.computeSize(SWT.DEFAULT, height).x, wrap.computeSize(width, SWT.DEFAULT).y);
	}

	private void initializeHelper(String helperName) {
		if (helperName == null) {
			return;
		}

		IHelper iHelper = null;

		pluginService.activatePlugin(helperName);
		BundleContext bundleContext = FrameworkUtil.getBundle(WFCDetailPart.class).getBundleContext();
		try {
			ServiceReference<?>[] allServiceReferences = bundleContext.getAllServiceReferences(IHelper.class.getName(), null);
			for (ServiceReference<?> serviceReference : allServiceReferences) {
				String property = (String) serviceReference.getProperty("component.name");
				if (property.equals(helperName)) {
					iHelper = (IHelper) bundleContext.getService(serviceReference);
				}
			}
		} catch (Exception e1) {
			logger.error("Error finding Helper", e1);
		}

		if (iHelper == null) {
			logger.error("Couldn't find Helper " + helperName);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error",
					translationService.translate("@msg.HelperNotFound", null) + " (" + helperName + ")");
		} else {
			getDetail().addHelper(iHelper);
			ContextInjectionFactory.inject(iHelper, mPerspective.getContext()); // In Context, damit Injection verfügbar ist
		}
	}

	private void loadOptionPages(Composite parent) {
		Preferences preferences = (Preferences) mApplication.getTransientData().get(Constants.XBS_FILE_NAME);

		Node maskNode = XBSUtil.getNodeWithName(preferences, mPerspective.getPersistedState().get(Constants.FORM_NAME));
		if (maskNode == null) {
			return;
		}

		for (Node settingsForMask : maskNode.getNode()) {
			if (settingsForMask.getName().equals(Constants.OPTION_PAGES)) {
				for (Node op : settingsForMask.getNode()) {
					try {
						try {
							Form opForm = dataFormService.getForm(op.getName());
							addOPFromForm(opForm, parent, op);
						} catch (IllegalArgumentException e) {
							try {
								String opContent = dataService.getHashedFile(op.getName()).get();
								addOPForGridOrBrowser(parent, op, opContent);
							} catch (JAXBException e1) {
								logger.error("JAXB Error", e1);
							}
						}
					} catch (ExecutionException e) {
						logger.error("Error getting OPs", e);
					} catch (InterruptedException e) {
						logger.error("Error getting OPs", e);
						Thread.currentThread().interrupt();
					} catch (NoSuchFieldException e) {
						MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", e.getMessage());
					}
				}
			}
		}
	}

	private void addOPForGridOrBrowser(Composite parent, Node op, String opContent) throws JAXBException, NoSuchFieldException {
		try {
			Grid opGrid = XmlProcessor.get(opContent, Grid.class);
			addOPFromGrid(opGrid, parent, op);
		} catch (IllegalArgumentException e2) {
			Browser opBrowser = XmlProcessor.get(opContent, Browser.class);
			addOPFromBrowser(opBrowser, parent);
		}
	}

	private void addOPFromForm(Form opForm, Composite parent, Node opNode) throws NoSuchFieldException {
		mDetail.addOptionPage(opForm);
		Map<String, String> keynamesToValues = XBSUtil.getKeynamesToValues(opNode);
		mDetail.addOptionPageKeys(opForm.getDetail().getProcedureSuffix(), keynamesToValues);

		for (Object headOrPage : opForm.getDetail().getHeadAndPageAndGrid()) {
			SectionWrapper wrapper;
			if (headOrPage instanceof Head) {
				// Head in der OP braucht titel und icon der Form
				wrapper = new SectionWrapper(headOrPage, true, opForm.getDetail().getProcedureSuffix(), opForm.getTitle(), opForm.getIcon());
			} else {
				wrapper = new SectionWrapper(headOrPage, true, opForm.getDetail().getProcedureSuffix());
			}
			layoutSection(parent, wrapper);
		}

		// Keyzuordnung aus .xbs prüfen, gibt es alle Felder?
		for (Entry<String, String> e : keynamesToValues.entrySet()) {
			String opFieldName = opForm.getDetail().getProcedureSuffix() + "." + e.getKey();
			String mainFieldName = e.getValue();

			if (mDetail.getField(opFieldName) == null) {
				NoSuchFieldException error = new NoSuchFieldException(
						"Option Page \"" + opForm.getDetail().getProcedureSuffix() + "\" does not contain Field \"" + e.getKey() + "\"! (As defined in .xbs)");
				logger.error(error.getMessage(), error);
				throw error;
			}

			if (mainFieldName.startsWith(Constants.OPTION_PAGE_QUOTE_ENTRY_SYMBOL)) {
				continue;
			}

			if (mDetail.getField(mainFieldName) == null) {
				NoSuchFieldException error = new NoSuchFieldException("Main Mask does not contain Field \"" + mainFieldName + "\", needed for OP \""
						+ opForm.getDetail().getProcedureSuffix() + "\"! (As defined in .xbs)");
				logger.error(error.getMessage(), error);
				throw error;
			}
		}

		initializeHelper(opForm.getHelperClass());
	}

	private void addOPFromGrid(Grid opGrid, Composite parent, Node opNode) throws NoSuchFieldException {
		SectionWrapper wrapper = new SectionWrapper(opGrid);
		layoutSection(parent, wrapper);
		addKeysFromXBSToGrid(opGrid, opNode);
	}

	private void addOPFromBrowser(Browser opBrowser, Composite parent) {
		SectionWrapper wrapper = new SectionWrapper(opBrowser);
		layoutSection(parent, wrapper);
	}

	/**
	 * Diese Methode extrahiert die Keyzuordnung für ein Grid aus der XBS, setzt diese ins Grid und überprüft, ob es alle Felder gibt
	 *
	 * @param grid
	 * @param Node
	 * @throws NoSuchFieldException
	 */
	private void addKeysFromXBSToGrid(Grid grid, Node node) throws NoSuchFieldException {
		// OP-Feldnamen zu Values Map aus .xbs setzten
		MGrid opMGrid = mDetail.getGrid(grid.getId());
		SectionGrid sg = ((GridAccessor) opMGrid.getGridAccessor()).getSectionGrid();
		Map<String, String> keynamesToValues = XBSUtil.getKeynamesToValues(node);
		sg.setFieldnameToValue(keynamesToValues);

		// Keyzuordnung aus .xbs prüfen, gibt es alle Felder?
		List<String> sgColumnNames = new ArrayList<>();
		for (Column c : sg.getDataTable().getColumns()) {
			sgColumnNames.add(c.getName());
		}
		for (Entry<String, String> e : keynamesToValues.entrySet()) {
			if (!sgColumnNames.contains(e.getKey())) {
				NoSuchFieldException error = new NoSuchFieldException(
						"Grid \"" + sg.getDataTable().getName() + "\" does not contain Field \"" + e.getKey() + "\"! (As defined in .xbs)");
				logger.error(error.getMessage(), error);
				throw error;
			}
			if (e.getValue().startsWith(Constants.OPTION_PAGE_QUOTE_ENTRY_SYMBOL)) {
				continue;
			}
			if (mDetail.getField(e.getValue()) == null) {
				NoSuchFieldException error = new NoSuchFieldException("Main Mask does not contain Field \"" + e.getValue() + "\", needed for Grid \""
						+ sg.getDataTable().getName() + "\"! (As defined in .xbs)");
				logger.error(error.getMessage(), error);
				throw error;
			}
		}
	}

	/**
	 * Diese Methode bekommt einen Composite übergeben, und erstellt aus dem übergenen Objekt ein Section. Diese Sektion ist entweder der Head (Kopfdaten) oder
	 * eine OptionPage die sich unterhalb der Kopfdaten eingliedert. Zusätzlich wird ein TraverseListener übergeben, der das Verhalten für TAB und Enter
	 * festlegt.
	 *
	 * @param parent
	 * @param headOrPageOrGrid
	 */

	private void layoutSection(Composite parent, SectionWrapper headOrPageOrGrid) {
		MinovaSectionData sectionData = new MinovaSectionData();
		MinovaSection section;
		if (headOrPageOrGrid.isHead()) {
			section = new MinovaSection(parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED, mPerspective);
			headSection = section;
		} else {
			section = new MinovaSection(parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED | ExpandableComposite.TWISTIE, mPerspective);
			section.getImageLink().addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDoubleClick(MouseEvent e) {
					minimizeSection(section);
				}
			});
		}
		section.setLayoutData(sectionData);
		section.setData(TRANSLATE_PROPERTY, headOrPageOrGrid.getTranslationText());
		section.setData(Constants.SECTION_NAME, headOrPageOrGrid.getId());

		ImageDescriptor imageDescriptor = ImageUtil.getImageDescriptor(headOrPageOrGrid.getIcon(), false);
		if (!imageDescriptor.equals(ImageDescriptor.getMissingImageDescriptor())) {
			section.setImage(resManager.createImage(imageDescriptor));
		}

		section.addControlListener(new ControlAdapter() {
			@Override
			public void controlMoved(ControlEvent e) {
				parent.setTabList(TabUtil.getSortedSectionTabList(parent));
			}
		});

		// Wir erstellen die Section des Details.
		MSection mSection = new MSection(headOrPageOrGrid.isHead(), "open", mDetail, headOrPageOrGrid.getId(), section.getText());
		mSection.setSectionAccessor(new SectionAccessor(mSection, section));

		// Button erstellen, falls vorhanden
		buttonUtil.createButton(headOrPageOrGrid, section);

		layoutSectionClient(headOrPageOrGrid, section, mSection);

		section.addListener(SWT.Resize, event -> adjustScrollbar(scrolled, parent));

		// Order setzen und sectionCount erhöhen
		sectionCount++;
		sectionData.setOrder(sectionCount);

		// Alten Zustand wiederherstellen
		// HorizontalFill
		String prefsHorizontalFillKey = form.getTitle() + "." + headOrPageOrGrid.getTranslationText() + ".horizontalFill";
		String horizontalFillString = prefsDetailSections.get(prefsHorizontalFillKey, "false");
		sectionData.setHorizontalFill(Boolean.parseBoolean(horizontalFillString));

		// Ein-/Ausgeklappt
		String prefsExpandedString = form.getTitle() + "." + headOrPageOrGrid.getTranslationText() + ".expanded";
		String expandedString = prefsDetailSections.get(prefsExpandedString, "true");
		section.setExpanded(Boolean.parseBoolean(expandedString));

		// Minimiert
		String prefsMinimizedString = form.getTitle() + "." + headOrPageOrGrid.getTranslationText() + ".minimized";
		String minimizedString = prefsDetailSections.get(prefsMinimizedString, "false");
		if (Boolean.parseBoolean(minimizedString)) {
			minimizeSection(section);
		}

		// Sichtbarkeit entsprechend der Maske setzen
		mSection.setVisible(headOrPageOrGrid.isVisible());

		detailWidth = section.getCssStyler().getSectionWidth();
		section.requestLayout();
		section.style();
	}

	private void minimizeSection(MinovaSection section) {
		Image image = section.getImageLink().getImage();
		if (image == null) {
			return;
		}

		section.setVisible(false);
		section.setMinimized(true);
		Control textClient = headSection.getTextClient();
		ToolBar bar = (ToolBar) textClient;
		ToolItem tItem = new ToolItem(bar, SWT.PUSH);
		tItem.setImage(image);
		tItem.setData(FieldUtil.TRANSLATE_PROPERTY, section.getData(FieldUtil.TRANSLATE_PROPERTY));
		tItem.setToolTipText(translationService.translate((String) section.getData(FieldUtil.TRANSLATE_PROPERTY), null));

		tItem.addSelectionListener(SelectionListener.widgetSelectedAdapter(selectionEvent -> {
			section.setVisible(true);
			section.setMinimized(false);
			tItem.dispose();
			bar.requestLayout();
			headSection.requestLayout();
		}));
		headSection.requestLayout();
		bar.requestLayout();
	}

	private void layoutSectionClient(SectionWrapper headOrPageOrGrid, MinovaSection section, MSection mSection) {
		// Client Area
		Composite clientComposite = getFormToolkit().createComposite(section);
		clientComposite.setLayout(new FormLayout());
		getFormToolkit().paintBordersFor(clientComposite);
		section.setClient(clientComposite);

		// Erstellen der Field des Section.
		createSectionContent(clientComposite, headOrPageOrGrid, mSection, section);
		// Setzen der TabListe für die einzelnen Sections.
		TabUtil.updateTabListOfSectionComposite(clientComposite);
		// Setzen der TabListe der Sections im Part.
		clientComposite.getParent().setTabList(TabUtil.getTabListForSection(section, mSection, selectAllControls));

		// MSection wird zum MDetail hinzugefügt.
		mDetail.addMSection(mSection);
	}

	private MGrid createMGrid(Grid grid, MSection section) {

		if (grid.getId() == null) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error",
					"Grid " + grid.getProcedureSuffix() + " has no ID! Please add an id in the xml mask");
			logger.error("Grid " + grid.getProcedureSuffix() + " has no ID! Please add an id in the xml mask");
		}

		MGrid mgrid = new MGrid(grid.getId());
		mgrid.setTitle(grid.getTitle());
		mgrid.setFill(grid.getFill());
		mgrid.setProcedureSuffix(grid.getProcedureSuffix());
		mgrid.setProcedurePrefix(grid.getProcedurePrefix());
		mgrid.setmSection(section);
		final ImageDescriptor gridImageDescriptor = ImageUtil.getImageDescriptor(grid.getIcon(), false);
		Image gridImage = resManager.createImage(gridImageDescriptor);
		mgrid.setIcon(gridImage);
		mgrid.setHelperClass(grid.getHelperClass());
		List<MField> mFields = new ArrayList<>();
		for (Field f : grid.getField()) {
			try {
				MField mF = ModelToViewModel.convert(f, locale);
				mFields.add(mF);
			} catch (NullPointerException e) {
				showErrorMissingSQLIndex(f, grid.getId() + "." + f.getName(), e);
			}
		}
		mgrid.setGrid(grid);
		mgrid.setFields(mFields);
		return mgrid;
	}

	private MBrowser createMBrowser(Browser browser, MSection section) {

		if (browser.getId() == null) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Browser has no ID!");
		}

		MBrowser mBrowser = new MBrowser(browser.getId());
		mBrowser.setTitle(browser.getTitle());
		final ImageDescriptor browserImageDescriptor = ImageUtil.getImageDescriptor(browser.getIcon(), false);
		Image browserImage = resManager.createImage(browserImageDescriptor);
		mBrowser.setIcon(browserImage);
		mBrowser.setmSection(section);
		return mBrowser;
	}

	/**
	 * Erstellt den Inhalt einer Sektion.
	 *
	 * @param composite
	 *            der parent des Fields
	 * @param headOrPage
	 *            bestimmt ob die Fields nach den Regeln des Heads erstellt werden oder der einer Page.
	 * @param mSection
	 *            die Section deren Fields erstellt werden sollen.
	 */
	private void createSectionContent(Composite composite, SectionWrapper headOrPage, MSection mSection, MinovaSection section) {
		IEclipseContext context = mPerspective.getContext();
		List<MField> visibleMFields = new ArrayList<>();
		for (Object fieldOrGrid : headOrPage.getFieldOrGrid()) {
			if (fieldOrGrid instanceof Grid) {

				createGrid(composite, mSection, section, context, fieldOrGrid);

			} else if (fieldOrGrid instanceof Browser) {
				createBrowser(composite, mSection, fieldOrGrid, context);
			} else {

				Field field = (Field) fieldOrGrid;

				String suffix = headOrPage.isOP() ? headOrPage.getFormSuffix() + "." : "";
				MField mField = createMField(field, mSection, suffix);
				if (mField.isVisible()) {
					visibleMFields.add(mField);
				}

				if (mField instanceof MParamStringField psf) {
					for (Field f : psf.getSubFields()) {
						MField subfield = createMField(f, mSection, suffix);
						((MParamStringField) mField).addSubMField(subfield);
						if (subfield.isVisible()) {
							visibleMFields.add(subfield);
						}
					}
				}
			}
		}
		createUIFields(visibleMFields, composite);
	}

	private void createBrowser(Composite composite, MSection mSection, Object fieldOrGrid, IEclipseContext context) {
		BrowserSection browserSection = new BrowserSection(composite);
		browserSection.createBrowser();
		MBrowser mBrowser = createMBrowser((Browser) fieldOrGrid, mSection);
		BrowserAccessor browserAccessor = new BrowserAccessor();
		browserAccessor.setBrowserSection(browserSection);
		browserAccessor.setmBrowser(mBrowser);
		mBrowser.setBrowserAccessor(browserAccessor);
		mSection.getmDetail().putBrowser(mBrowser);
		browserSections.add(browserSection);

		ContextInjectionFactory.inject(browserSection, context); // In Context injected, damit Injection in der Klasse verfügbar ist
	}

	public void createUIFields(List<MField> mFields, Composite clientComposite) {
		int row = 0;
		int column = 0;
		for (MField mField : mFields) {
			int width = mField.getNumberColumnsSpanned();

			if (column + width > 4) {
				column = 0;
				row++;
			}

			createField(clientComposite, mField, row, column);

			row += mField.getNumberRowsSpanned() - 1;
			column += width;
			if (mField.isFillHorizontal()) {
				column = 4;
			}
		}
	}

	public MField createMField(Field field, MSection mSection, String suffix) {
		String fieldName = suffix + field.getName();
		try {
			MField f = ModelToViewModel.convert(field, locale);
			f.addValueChangeListener(dirtyFlagUtil);
			f.setName(fieldName);

			getDetail().putField(f);
			f.setMSection(mSection);

			if (field.isVisible()) {
				mSection.addMField(f);
			}

			// Wird ein primary-key geändert muss dies weitergegeben werden, damit Lookup-Masken funktionieren
			if ("primary".equalsIgnoreCase(field.getKeyType())) {
				f.addValueChangeListener(evt -> {
					if (f.getValue() == null) {
						return;
					}
					if (casRequestsUtil.getKeys() == null) {
						casRequestsUtil.setKeys(new HashMap<>());
					}
					casRequestsUtil.getKeys().put(f.getName(), f.getValue());
				});
			}

			return f;
		} catch (NullPointerException e) {
			showErrorMissingSQLIndex(field, fieldName, e);
		}
		return null;
	}

	private void createGrid(Composite composite, MSection mSection, MinovaSection section, IEclipseContext context, Object fieldOrGrid) {
		SectionGrid sg = new SectionGrid(composite, section, (Grid) fieldOrGrid, mDetail);
		MGrid mGrid = createMGrid((Grid) fieldOrGrid, mSection);
		mGrid.addGridChangeListener(dirtyFlagUtil);
		GridAccessor gA = new GridAccessor(mGrid);
		gA.setSectionGrid(sg);
		mGrid.setGridAccessor(gA);
		mSection.getmDetail().putGrid(mGrid);
		sectionGrids.add(sg);
		initializeHelper(((Grid) fieldOrGrid).getHelperClass());

		ContextInjectionFactory.inject(sg, context); // In Context injected, damit Injection in der Klasse verfügbar ist
		sg.createGrid();
		mGrid.setDataTable(sg.getDataTable());

		// XBS nach Keyzuordnung überprüfen, gilt nur fürs erste Grid
		try {
			if (mDetail.getGrids().size() == 1) {
				checkXBSForGridKeys((Grid) fieldOrGrid);
			}
		} catch (NoSuchFieldException e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", e.getMessage());
		}
	}

	public void showErrorMissingSQLIndex(Field field, String fieldname, NullPointerException e) {
		if (field.getSqlIndex() == null) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Field " + fieldname + " has no SQL-Index!");
			logger.error("Field " + fieldname + " has no SQL-Index!");
		} else {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", e.getMessage());
			logger.error(e.getMessage());
		}
	}

	/**
	 * XBS überprüfen, ob es eine Keyzuordnung für dieses Grid gibt
	 */
	private void checkXBSForGridKeys(Grid grid) throws NoSuchFieldException {
		Preferences preferences = (Preferences) mApplication.getTransientData().get(Constants.XBS_FILE_NAME);
		Node maskNode = XBSUtil.getNodeWithName(preferences, mPerspective.getPersistedState().get(Constants.FORM_NAME));
		if (maskNode == null) {
			return;
		}

		for (Node settingsForMask : maskNode.getNode()) {
			if (settingsForMask.getName().equals(Constants.OPTION_PAGE_GRID)) {
				addKeysFromXBSToGrid(grid, settingsForMask);
			}
		}
	}

	private void createField(Composite composite, MField field, int row, int column) {
		if (field instanceof MBooleanField) {
			BooleanField.create(composite, field, row, column, locale, mPerspective);
		} else if (field instanceof MNumberField mnf) {
			NumberField.create(composite, mnf, row, column, locale, mPerspective, translationService);
		} else if (field instanceof MDateTimeField) {
			DateTimeField.create(composite, field, row, column, locale, timezone, mPerspective, translationService);
		} else if (field instanceof MShortDateField) {
			ShortDateField.create(composite, field, row, column, locale, timezone, mPerspective, translationService);
		} else if (field instanceof MShortTimeField) {
			ShortTimeField.create(composite, field, row, column, locale, timezone, mPerspective, translationService);
		} else if (field instanceof MLookupField) {
			LookupField.create(composite, field, row, column, locale, mPerspective);
		} else if (field instanceof MTextField) {
			TextField.create(composite, field, row, column, mPerspective);
		} else if (field instanceof MRadioField) {
			RadioField.create(composite, field, row, column, locale, mPerspective);
		} else if (field instanceof MLabelText) {
			LabelTextField.createBold(composite, field, row, column, mPerspective);
		} else if (field instanceof MPeriodField) {
			PeriodField.create(composite, field, row, locale, mPerspective);
		} else if (field instanceof MQuantityField mqf) {
			QuantityField.create(composite, mqf, row, column, locale, mPerspective, translationService);
		}
	}

	@Inject
	@Optional
	private void getNotified(@Named(TranslationService.LOCALE) Locale s) {
		this.locale = s;
		if (translationService != null && composite != null) {
			TranslateUtil.translate(composite, translationService, locale);
		}
	}

	public MDetail getDetail() {
		return mDetail;
	}

	public WFCDetailCASRequestsUtil getRequestUtil() {
		return casRequestsUtil;
	}

	@PersistState
	public void persistState() {
		// Grids
		for (SectionGrid sg : sectionGrids) {
			sg.saveState();
		}

		// Sections, ein-/ausgeklappt
		for (MSection s : mDetail.getMSectionList()) {
			MinovaSection section = ((SectionAccessor) s.getSectionAccessor()).getSection();
			String prefsExpandedString = form.getTitle() + "." + section.getData(TRANSLATE_PROPERTY) + ".expanded";
			prefsDetailSections.put(prefsExpandedString, section.isExpanded() + "");

			String prefsMinimizedString = form.getTitle() + "." + section.getData(TRANSLATE_PROPERTY) + ".minimized";
			prefsDetailSections.put(prefsMinimizedString, section.isMinimized() + "");
		}

		try {
			prefsDetailSections.flush();
		} catch (BackingStoreException e1) {
			logger.error(e1.getMessage(), e1);
		}
	}

	public FormToolkit getFormToolkit() {
		return formToolkit;
	}

	public Composite getComposite() {
		return composite;
	}

}