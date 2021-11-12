
package aero.minova.rcp.rcp.parts;

import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_HEIGHT;
import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_WIDTH;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_LEFT;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_TOP;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_PROPERTY;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBException;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.PersistState;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimBar;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.IWindowCloseHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.prefs.BackingStoreException;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.ImageUtil;
import aero.minova.rcp.dataservice.XmlProcessor;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.form.model.xsd.Grid;
import aero.minova.rcp.form.model.xsd.Head;
import aero.minova.rcp.form.model.xsd.Onclick;
import aero.minova.rcp.form.model.xsd.Page;
import aero.minova.rcp.form.model.xsd.Procedure;
import aero.minova.rcp.form.model.xsd.Wizard;
import aero.minova.rcp.form.setup.util.XBSUtil;
import aero.minova.rcp.form.setup.xbs.Node;
import aero.minova.rcp.form.setup.xbs.Preferences;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.event.GridChangeEvent;
import aero.minova.rcp.model.event.GridChangeListener;
import aero.minova.rcp.model.event.ValueChangeEvent;
import aero.minova.rcp.model.event.ValueChangeListener;
import aero.minova.rcp.model.form.MBooleanField;
import aero.minova.rcp.model.form.MButton;
import aero.minova.rcp.model.form.MDateTimeField;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MGrid;
import aero.minova.rcp.model.form.MLookupField;
import aero.minova.rcp.model.form.MNumberField;
import aero.minova.rcp.model.form.MParamStringField;
import aero.minova.rcp.model.form.MSection;
import aero.minova.rcp.model.form.MShortDateField;
import aero.minova.rcp.model.form.MShortTimeField;
import aero.minova.rcp.model.form.MTextField;
import aero.minova.rcp.model.form.ModelToViewModel;
import aero.minova.rcp.model.helper.IHelper;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.rcp.accessor.ButtonAccessor;
import aero.minova.rcp.rcp.accessor.DetailAccessor;
import aero.minova.rcp.rcp.accessor.GridAccessor;
import aero.minova.rcp.rcp.accessor.SectionAccessor;
import aero.minova.rcp.rcp.fields.BooleanField;
import aero.minova.rcp.rcp.fields.DateTimeField;
import aero.minova.rcp.rcp.fields.LookupField;
import aero.minova.rcp.rcp.fields.NumberField;
import aero.minova.rcp.rcp.fields.ShortDateField;
import aero.minova.rcp.rcp.fields.ShortTimeField;
import aero.minova.rcp.rcp.fields.TextField;
import aero.minova.rcp.rcp.util.TabUtil;
import aero.minova.rcp.rcp.util.TranslateUtil;
import aero.minova.rcp.rcp.util.WFCDetailCASRequestsUtil;
import aero.minova.rcp.rcp.widgets.MinovaSection;
import aero.minova.rcp.rcp.widgets.SectionGrid;

@SuppressWarnings("restriction")
public class WFCDetailPart extends WFCFormPart implements ValueChangeListener, GridChangeListener {

	private static final int MARGIN_SECTION = 8;
	public static final int SECTION_WIDTH = 4 * COLUMN_WIDTH + 3 * MARGIN_LEFT + 2 * MARGIN_SECTION + 50; // 4 Spalten = 5 Zwischenräume
	@Inject
	protected UISynchronize sync;

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

	private boolean dirtyFlag;

	@Inject
	private MPart mpart;

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
	private WFCDetailCASRequestsUtil casRequestsUtil;

	private IEclipseContext appContext;

	@Inject
	MWindow mwindow;

	@Inject
	EModelService eModelService;
	MApplication mApplication;
	private List<SectionGrid> sectionGrids = new ArrayList<>();

	@PostConstruct
	public void postConstruct(Composite parent, MWindow window, MApplication mApp) {
		resManager = new LocalResourceManager(JFaceResources.getResources(), parent);
		composite = parent;
		formToolkit = new FormToolkit(parent.getDisplay());
		appContext = mApp.getContext();
		mApplication = mApp;
		getForm();
		layoutForm(parent);
		mDetail.setDetailAccessor(new DetailAccessor(mDetail));
		mDetail.setClearAfterSave(form.getDetail().isClearAfterSave());

		// Erstellen der Util-Klasse, welche sämtliche funktionen der Detailansicht steuert
		casRequestsUtil = ContextInjectionFactory.make(WFCDetailCASRequestsUtil.class, mPerspective.getContext());
		casRequestsUtil.initializeCasRequestUtil(getDetail(), mPerspective, this);
		mPerspective.getContext().set(WFCDetailCASRequestsUtil.class, casRequestsUtil);
		mPerspective.getContext().set(Constants.DETAIL_WIDTH, SECTION_WIDTH);
		TranslateUtil.translate(composite, translationService, locale);

		// Helper erst initialisieren, wenn casRequestsUtil erstellt wurde
		if (mDetail.getHelper() != null) {
			mDetail.getHelper().setControls(mDetail);
		}

		// Handler, der Dialog anzeigt wenn versucht wird, die Anwendung mit ungespeicherten Änderungen zu schließen
		// Außerdem wird "RESTORING_UI_MESSAGE_SHOWN_THIS_SESSION" wieder auf false gesetzt, damit die Nachricht beim nächsten Starten wieder angezeigt wird
		IWindowCloseHandler handler = mWindow -> {
			@SuppressWarnings("unchecked")
			List<MPerspective> pList = (List<MPerspective>) appContext.get(Constants.DIRTY_PERSPECTIVES);
			if (pList != null && !pList.isEmpty()) {
				StringBuilder listString = new StringBuilder();
				for (MPerspective mPerspective : pList) {
					listString.append(" - " + translationService.translate(mPerspective.getLabel(), null) + "\n");
				}
				MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), translationService.translate("@msg.ChangesDialog", null), null,
						translationService.translate("@msg.Close.DirtyMessage", null) + listString, MessageDialog.CONFIRM,
						new String[] { translationService.translate("@Action.Discard", null), translationService.translate("@Abort", null) }, 0);

				boolean res = dialog.open() == 0;
				if (res) {
					prefs.put(Constants.RESTORING_UI_MESSAGE_SHOWN_THIS_SESSION, "false");
				}
				return res;
			}
			prefs.put(Constants.RESTORING_UI_MESSAGE_SHOWN_THIS_SESSION, "false");

			return true;
		};
		window.getContext().set(IWindowCloseHandler.class, handler);

		openRestoringUIDialog();
	}

	/**
	 * Öffnet des "UI wird wiederhergestellt" Dialog, wenn er diese Session noch nicht geöffnet wurde und die Checkbox "NEVER_SHOW_RESTORING_UI_MESSAGE" nie
	 * gewählt wurde
	 */
	private void openRestoringUIDialog() {
		boolean neverShow = prefs.getBoolean(Constants.NEVER_SHOW_RESTORING_UI_MESSAGE, false);
		boolean shownThisSession = prefs.getBoolean(Constants.RESTORING_UI_MESSAGE_SHOWN_THIS_SESSION, false);
		// Benötigt für UI-Tests damit sich in ihnen Dialog nicht öffnet, wird in LifeCycle gesetzt
		boolean neverShowContext = appContext.get(Constants.NEVER_SHOW_RESTORING_UI_MESSAGE) != null
				&& (boolean) appContext.get(Constants.NEVER_SHOW_RESTORING_UI_MESSAGE);

		if (!neverShow && !shownThisSession && !neverShowContext) {
			MessageDialogWithToggle mdwt = MessageDialogWithToggle.openInformation(Display.getCurrent().getActiveShell(), //
					translationService.translate("@RestoringUIDialog.Title", null), //
					translationService.translate("@RestoringUIDialog.InfoText", null), //
					translationService.translate("@RestoringUIDialog.NeverShowAgain", null), //
					false, null, null);
			if (mdwt.getToggleState()) {
				prefs.put(Constants.NEVER_SHOW_RESTORING_UI_MESSAGE, "true");
			}
			prefs.put(Constants.RESTORING_UI_MESSAGE_SHOWN_THIS_SESSION, "true");
		}
	}

	private static class HeadOrPageOrGridWrapper {
		private Object headOrPageOrGrid;
		public boolean isHead = false;
		public boolean isOP = false;
		private String formTitle;
		public String formSuffix;
		public String id;
		public String icon;

		public HeadOrPageOrGridWrapper(Object headOrPageOrGrid) {
			this.headOrPageOrGrid = headOrPageOrGrid;
			if (headOrPageOrGrid instanceof Head) {
				isHead = true;
				id = "Head";
			} else if (headOrPageOrGrid instanceof Page) {
				id = ((Page) headOrPageOrGrid).getId();
				icon = ((Page) headOrPageOrGrid).getIcon();
			} else {
				id = ((Grid) headOrPageOrGrid).getId();
				icon = ((Grid) headOrPageOrGrid).getIcon();
			}
		}

		public HeadOrPageOrGridWrapper(Object headOrPageOrGrid, boolean isOP, String formSuffix) {
			this(headOrPageOrGrid);
			this.formSuffix = formSuffix;
			this.isOP = isOP;
			if (headOrPageOrGrid instanceof Head && !isOP) {
				isHead = true;
			} else {
				isHead = false;
			}

			if (headOrPageOrGrid instanceof Head && isOP) {
				id = formSuffix + ".Head";
			}
		}

		public HeadOrPageOrGridWrapper(Object headOrPageOrGrid, boolean isOP, String formSuffix, String formTitle, String icon) {
			this(headOrPageOrGrid, isOP, formSuffix);
			this.formTitle = formTitle;
			this.icon = icon;
		}

		public String getTranslationText() {
			if (isHead) {
				return "@Head";
			} else if (headOrPageOrGrid instanceof Head && isOP) {
				return formTitle;
			} else if (headOrPageOrGrid instanceof Grid) {
				return ((Grid) headOrPageOrGrid).getTitle();
			} else if (headOrPageOrGrid instanceof Page) {
				return ((Page) headOrPageOrGrid).getText();
			}
			return "";
		}

		public List<Object> getFieldOrGrid() {
			if (headOrPageOrGrid instanceof Head) {
				return ((Head) headOrPageOrGrid).getFieldOrGrid();
			} else if (headOrPageOrGrid instanceof Grid) {
				// es existieren keine Felder, nur eine Table
				List<Object> mylistList = new ArrayList<>();
				mylistList.add(headOrPageOrGrid);
				return mylistList;
			}
			return ((Page) headOrPageOrGrid).getFieldOrGrid();
		}

	}

	private void layoutForm(Composite parent) {
		parent.setLayout(new RowLayout(SWT.VERTICAL));
		for (Object headOrPage : form.getDetail().getHeadAndPageAndGrid()) {
			HeadOrPageOrGridWrapper wrapper = new HeadOrPageOrGridWrapper(headOrPage);
			layoutSection(parent, wrapper);
		}

		loadOptionPages(parent);

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
			e1.printStackTrace();
		}

		if (iHelper == null) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", translationService.translate("@msg.HelperNotFound", null));
		} else {
			getDetail().setHelper(iHelper);
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
								Grid opGrid = XmlProcessor.get(opContent, Grid.class);
								addOPFromGrid(opGrid, parent, op);
							} catch (JAXBException e1) {
								e1.printStackTrace();
							}
						}
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					} catch (NoSuchFieldException e) {
						MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", e.getMessage());
					}
				}
			}
		}
	}

	private void addOPFromForm(Form opForm, Composite parent, Node opNode) throws NoSuchFieldException {
		mDetail.addOptionPage(opForm);
		Map<String, String> keynamesToValues = XBSUtil.getKeynamesToValues(opNode);
		mDetail.addOptionPageKeys(opForm.getDetail().getProcedureSuffix(), keynamesToValues);

		for (Object headOrPage : opForm.getDetail().getHeadAndPageAndGrid()) {
			HeadOrPageOrGridWrapper wrapper;
			if (headOrPage instanceof Head) {
				// Head in der OP braucht titel und icon der Form
				wrapper = new HeadOrPageOrGridWrapper(headOrPage, true, opForm.getDetail().getProcedureSuffix(), opForm.getTitle(), opForm.getIcon());
			} else {
				wrapper = new HeadOrPageOrGridWrapper(headOrPage, true, opForm.getDetail().getProcedureSuffix());
			}
			layoutSection(parent, wrapper);
		}

		// Keyzuordnung aus .xbs prüfen, gibt es alle Felder?
		for (Entry<String, String> e : keynamesToValues.entrySet()) {
			String opFieldName = opForm.getDetail().getProcedureSuffix() + "." + e.getKey();
			String mainFieldName = e.getValue();
			if (mDetail.getField(opFieldName) == null) {
				throw new NoSuchFieldException(
						"Option Page \"" + opForm.getDetail().getProcedureSuffix() + "\" does not contain Field \"" + e.getKey() + "\"! (As defined in .xbs)");
			}
			if (mDetail.getField(mainFieldName) == null) {
				throw new NoSuchFieldException("Main Mask does not contain Field \"" + mainFieldName + "\", needed for OP \""
						+ opForm.getDetail().getProcedureSuffix() + "\"! (As defined in .xbs)");
			}
		}
	}

	private void addOPFromGrid(Grid opGrid, Composite parent, Node opNode) throws NoSuchFieldException {
		HeadOrPageOrGridWrapper wrapper = new HeadOrPageOrGridWrapper(opGrid);
		layoutSection(parent, wrapper);

		addKeysFromXBSToGrid(opGrid, opNode);
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
				throw new NoSuchFieldException(
						"Grid \"" + sg.getDataTable().getName() + "\" does not contain Field \"" + e.getKey() + "\"! (As defined in .xbs)");
			}
			if (mDetail.getField(e.getValue()) == null) {
				throw new NoSuchFieldException("Main Mask does not contain Field \"" + e.getValue() + "\", needed for Grid \"" + sg.getDataTable().getName()
						+ "\"! (As defined in .xbs)");
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

	private void layoutSection(Composite parent, HeadOrPageOrGridWrapper headOrPageOrGrid) {
		RowData headLayoutData = new RowData();
		MinovaSection section;
		if (headOrPageOrGrid.isHead) {
			section = new MinovaSection(parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		} else {
			section = new MinovaSection(parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED | ExpandableComposite.TWISTIE);
		}

		// Alten Zustand wiederherstellen
		String prefsWidthKey = form.getTitle() + "." + headOrPageOrGrid.getTranslationText() + ".width";
		String widthString = prefsDetailSections.get(prefsWidthKey, SECTION_WIDTH + "");
		headLayoutData.width = Integer.parseInt(widthString);
		String prefsExpandedString = form.getTitle() + "." + headOrPageOrGrid.getTranslationText() + ".expanded";
		String expandedString = prefsDetailSections.get(prefsExpandedString, "true");
		section.setExpanded(Boolean.parseBoolean(expandedString));

		section.setData(TRANSLATE_PROPERTY, headOrPageOrGrid.getTranslationText());
		section.setLayoutData(headLayoutData);

		ImageDescriptor imageDescriptor = ImageUtil.getImageDescriptor(headOrPageOrGrid.icon, false);
		if (!imageDescriptor.equals(ImageDescriptor.getMissingImageDescriptor())) {
			section.setImage(resManager.createImage(imageDescriptor));
		}

		section.addExpansionListener(new IExpansionListener() {
			@Override
			public void expansionStateChanging(ExpansionEvent e) {}

			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				prefsDetailSections.put(prefsExpandedString, e.getState() + "");
				try {
					prefsDetailSections.flush();
				} catch (BackingStoreException e1) {
					e1.printStackTrace();
				}
			}
		});

		// Wir erstellen die Section des Details.
		MSection mSection = new MSection(headOrPageOrGrid.isHead, "open", mDetail, headOrPageOrGrid.id, section.getText());
		mSection.setSectionAccessor(new SectionAccessor(mSection, section));
		// Button erstellen, falls vorhanden
		createButton(headOrPageOrGrid, section);

		layoutSectionClient(headOrPageOrGrid, section, mSection);
	}

	private void layoutSectionClient(HeadOrPageOrGridWrapper headOrPageOrGrid, Section section, MSection mSection) {
		// Client Area
		Composite clientComposite = getFormToolkit().createComposite(section);
		clientComposite.setLayout(new FormLayout());
		getFormToolkit().paintBordersFor(clientComposite);
		section.setClient(clientComposite);

		// Erstellen der Field des Section.
		createFields(clientComposite, headOrPageOrGrid, mSection, section);
		// Sortieren der Fields nach Tab-Index.
		TabUtil.sortTabList(mSection);
		// Setzen der TabListe für die einzelnen Sections.
		clientComposite.setTabList(TabUtil.getTabListForSectionComposite(mSection, clientComposite));
		// Setzen der TabListe der Sections im Part.
		clientComposite.getParent().setTabList(TabUtil.getTabListForSection(section, mSection, selectAllControls));

		// MSection wird zum MDetail hinzugefügt.
		mDetail.addMSection(mSection);
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
		if (headOPOGWrapper.headOrPageOrGrid instanceof Grid) {
			return;
		}

		final ToolBar bar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL | SWT.RIGHT | SWT.NO_FOCUS);

		List<aero.minova.rcp.form.model.xsd.Button> buttons = new ArrayList<>();
		if (headOPOGWrapper.headOrPageOrGrid instanceof Page) {
			buttons = ((Page) headOPOGWrapper.headOrPageOrGrid).getButton();
		} else if (headOPOGWrapper.headOrPageOrGrid instanceof Head) {
			buttons = ((Head) headOPOGWrapper.headOrPageOrGrid).getButton();
		}

		for (aero.minova.rcp.form.model.xsd.Button btn : buttons) {
			final ToolItem item = new ToolItem(bar, SWT.PUSH);

			MButton mButton = new MButton(btn.getId());
			mButton.setIcon(btn.getIcon());
			mButton.setText(btn.getText());
			ButtonAccessor bA = new ButtonAccessor(mButton, item);
			mButton.setButtonAccessor(bA);
			mDetail.putButton(mButton);

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
						List<Object> binderOrProcedureOrInstances = onclick.getBinderOrProcedureOrInstance();

						for (Object o : binderOrProcedureOrInstances) {
							if (o instanceof Wizard) {
								Map<String, String> parameter = Map.of(Constants.CONTROL_WIZARD, ((Wizard) o).getWizardname());
								ParameterizedCommand command = commandService.createCommand("aero.minova.rcp.rcp.command.dynamicbuttoncommand", parameter);
								handlerService.executeHandler(command);
							} else if (o instanceof Procedure) {
								casRequestsUtil.callProcedure((Procedure) o);
							} else {
								System.err.println("Event vom Typ " + o.getClass() + " für Buttons noch nicht implementiert!");
							}
						}
					}
				});
			}

			if (btn.getIcon() != null && btn.getIcon().trim().length() > 0) {
				final ImageDescriptor buttonImageDescriptor = ImageUtil.getImageDescriptor(btn.getIcon().replace(".ico", ""), false);
				item.setImage(resManager.createImage(buttonImageDescriptor));
			}
		}
		section.setTextClient(bar);
	}

	private Object findEventForID(String id) {
		if (form.getEvents() != null) {
			for (Onclick onclick : form.getEvents().getOnclick()) {
				if (onclick.getRefid().equals(id)) {
					return onclick;
				}
			}
		}
		// TODO: Onbinder und ValueChange implementieren
		return null;
	}

	private MGrid createMGrid(Grid grid, MSection section) {

		if (grid.getId() == null) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Grid " + grid.getProcedureSuffix() + " has no ID!");
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

	/**
	 * Erstellt die Field einer Section.
	 *
	 * @param composite
	 *            der parent des Fields
	 * @param headOrPage
	 *            bestimmt ob die Fields nach den Regeln des Heads erstellt werden oder der einer Page.
	 * @param mSection
	 *            die Section deren Fields erstellt werden.
	 */
	private void createFields(Composite composite, HeadOrPageOrGridWrapper headOrPage, MSection mSection, Section section) {
		IEclipseContext context = mPerspective.getContext();
		List<MField> visibleMFields = new ArrayList<>();
		for (Object fieldOrGrid : headOrPage.getFieldOrGrid()) {
			if (fieldOrGrid instanceof Grid) {

				createGrid(composite, mSection, section, context, fieldOrGrid);

			} else {

				Field field = (Field) fieldOrGrid;

				String suffix = headOrPage.isOP ? headOrPage.formSuffix + "." : "";
				MField mField = createMField(field, mSection, suffix);
				if (field.isVisible()) {
					visibleMFields.add(mField);
				}

				if (mField instanceof MParamStringField) {
					for (Field f : ((MParamStringField) mField).getSubFields()) {
						MField subfield = createMField(f, mSection, suffix);
						((MParamStringField) mField).addSubMField(subfield);
						if (f.isVisible()) {
							visibleMFields.add(subfield);
						}
					}
				}
			}
		}
		createUIFields(visibleMFields, composite);
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
		}
		addBottonMargin(clientComposite, row + 1, column);
	}

	public MField createMField(Field field, MSection mSection, String suffix) {
		String fieldName = suffix + field.getName();
		try {
			MField f = ModelToViewModel.convert(field, locale);
			f.addValueChangeListener(this);
			f.setName(fieldName);

			getDetail().putField(f);

			if (field.isVisible()) {
				f.setMSection(mSection);
				mSection.addTabField(f);
			}

			return f;
		} catch (NullPointerException e) {
			showErrorMissingSQLIndex(field, fieldName, e);
		}
		return null;
	}

	private void createGrid(Composite composite, MSection mSection, Section section, IEclipseContext context, Object fieldOrGrid) {
		SectionGrid sg = new SectionGrid(composite, section, (Grid) fieldOrGrid, mDetail);
		MGrid mGrid = createMGrid((Grid) fieldOrGrid, mSection);
		mGrid.addGridChangeListener(this);
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
		} else {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", e.getMessage());
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
			BooleanField.create(composite, field, row, column, locale, mPerspective);
		} else if (field instanceof MNumberField) {
			NumberField.create(composite, (MNumberField) field, row, column, locale, mPerspective);
		} else if (field instanceof MDateTimeField) {
			DateTimeField.create(composite, field, row, column, locale, timezone, mPerspective, translationService);
		} else if (field instanceof MShortDateField) {
			ShortDateField.create(composite, field, row, column, locale, timezone, mPerspective, translationService);
		} else if (field instanceof MShortTimeField) {
			ShortTimeField.create(composite, field, row, column, locale, timezone, mPerspective, translationService);
		} else if (field instanceof MLookupField) {
			LookupField.create(composite, field, row, column, locale, mPerspective);
		} else if (field instanceof MTextField || field instanceof MParamStringField) {
			TextField.create(composite, field, row, column, mPerspective);
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

	private void setDirtyFlag(boolean dirtyFlag) {
		this.dirtyFlag = dirtyFlag;

		mpart.setDirty(dirtyFlag);
		@SuppressWarnings("unchecked")
		List<MPerspective> pList = (List<MPerspective>) appContext.get(Constants.DIRTY_PERSPECTIVES);

		if (dirtyFlag) {
			if (pList == null) {
				pList = new ArrayList<>();
				appContext.set(Constants.DIRTY_PERSPECTIVES, pList);
			}
			if (!pList.contains(mPerspective)) {
				pList.add(mPerspective);
				refreshToolbar();
			}
		} else {
			if (pList != null) {
				pList.remove(mPerspective);
				refreshToolbar();
			}
		}
	}

	public boolean getDirtyFlag() {
		return dirtyFlag;
	}

	public void refreshToolbar() {
		List<MTrimBar> findElements = eModelService.findElements(mwindow, "aero.minova.rcp.rcp.trimbar.0", MTrimBar.class);
		MTrimBar tBar = findElements.get(0);
		Composite c = (Composite) (tBar.getChildren().get(0)).getWidget();
		if (c == null) {
			return;
		}
		ToolBar tb = (ToolBar) c.getChildren()[0];

		String perspectiveLabel = translationService.translate(mPerspective.getLabel(), null);
		for (ToolItem item : tb.getItems()) {
			if (item.getText().replace("*", "").equals(perspectiveLabel)) {
				item.setText((dirtyFlag ? "*" : "") + perspectiveLabel);
			}
		}
		tb.requestLayout();
	}

	@Override
	public void gridChange(GridChangeEvent evt) {
		checkDirtyFlag();
	}

	@Override
	public void valueChange(ValueChangeEvent evt) {
		checkDirtyFlag();
	}

	@Inject
	@Optional
	public void checkDirtyFromBroker(@UIEventTopic(Constants.BROKER_CHECKDIRTY) String message) {
		MPerspective activePerspective = eModelService.getActivePerspective(mwindow);
		if (activePerspective.equals(mPerspective)) {
			checkDirtyFlag();
		}
	}

	private void checkDirtyFlag() {
		if (casRequestsUtil != null) {
			boolean setDirty = casRequestsUtil.checkDirty();
			if (this.dirtyFlag != setDirty) {
				setDirtyFlag(setDirty);
			}
		}
	}

	@PersistState
	public void persistState() {
		for (SectionGrid sg : sectionGrids) {
			sg.saveState();
		}
	}

	public FormToolkit getFormToolkit() {
		return formToolkit;
	}

	public Composite getComposite() {
		return composite;
	}

	public Locale getLocale() {
		return locale;
	}

	public boolean isSelectAllControls() {
		return selectAllControls;
	}

}