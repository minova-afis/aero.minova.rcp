
package aero.minova.rcp.rcp.parts;

import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_HEIGHT;
import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_WIDTH;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_LEFT;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_TOP;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_LOCALE;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_PROPERTY;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.opal.textassist.TextAssist;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Twistie;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.ImageUtil;
import aero.minova.rcp.form.setup.util.XBSUtil;
import aero.minova.rcp.form.setup.xbs.Map.Entry;
import aero.minova.rcp.form.setup.xbs.Node;
import aero.minova.rcp.form.setup.xbs.Preferences;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.form.MBooleanField;
import aero.minova.rcp.model.form.MDateTimeField;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MLookupField;
import aero.minova.rcp.model.form.MNumberField;
import aero.minova.rcp.model.form.MParamStringField;
import aero.minova.rcp.model.form.MSection;
import aero.minova.rcp.model.form.MShortDateField;
import aero.minova.rcp.model.form.MShortTimeField;
import aero.minova.rcp.model.form.MTextField;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.rcp.accessor.DetailAccessor;
import aero.minova.rcp.rcp.accessor.SectionAccessor;
import aero.minova.rcp.rcp.fields.BooleanField;
import aero.minova.rcp.rcp.fields.DateTimeField;
import aero.minova.rcp.rcp.fields.LookupField;
import aero.minova.rcp.rcp.fields.NumberField;
import aero.minova.rcp.rcp.fields.ShortDateField;
import aero.minova.rcp.rcp.fields.ShortTimeField;
import aero.minova.rcp.rcp.fields.TextField;
import aero.minova.rcp.rcp.util.WFCDetailCASRequestsUtil;
import aero.minova.rcp.rcp.widgets.MinovaSection;
import aero.minova.rcp.rcp.widgets.SectionGrid;
import aero.minova.rcp.widgets.LookupComposite;

@SuppressWarnings("restriction")
public class WFCDetailPartStatistic {

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
	MPerspective mPerspective;
	MinovaSection section;
	private List<SectionGrid> sectionGrids = new ArrayList<>();
	private MSection mSection;

	@PostConstruct
	public void postConstruct(Composite parent, MWindow window, MApplication mApp, MPerspective mPerspective) {
		resManager = new LocalResourceManager(JFaceResources.getResources(), parent);
		composite = parent;
		formToolkit = new FormToolkit(parent.getDisplay());
		appContext = mApp.getContext();
		this.mPerspective = mPerspective;
		mApplication = mApp;
		mDetail.setDetailAccessor(new DetailAccessor(mDetail));
		layoutSection(parent, "Statistic");

	}

	/**
	 * Sobald eine Zeile aus dem IndexPart selektiert wird erstellen wir das Detail neu. Aus dem übergebenen String lesen wir die entsprechende Statistik aus
	 * und erstellen dann die Felder.
	 *
	 * @param obj
	 */
	@Inject
	@Optional
	public void createStatisticDetail(@UIEventTopic(Constants.BROKER_SELECTSTATISTIC) Row row) {
		Preferences preferences = (Preferences) mApplication.getTransientData().get(Constants.XBS_FILE_NAME);
		Node statisticNode = XBSUtil.getNodeWithName(preferences, row.getValue(0).getStringValue());
		List<MField> mfields = new ArrayList<>();
		for (Node n : statisticNode.getNode()) {
			// Nun lesen wir die Felder aus (field0,field1,field2...) Reihenfolge muss beachtet werden (0,1,2,3...)
			String displayFormat = null;
			boolean required = true;
			String fieldName = null;
			String label = null;
			String tableName = null;

			for (Entry e : n.getMap().getEntry()) {
				switch (e.getKey().toLowerCase()) {
				case "":
					label = e.getValue();
					// Übersetzbarer Name
					break;
				case "displayformat":
					// Type
					displayFormat = e.getValue();
					break;
				case "fieldname":
					// Name für die Prozedur
					fieldName = e.getValue();
					break;
				case "nullable":
					// wenn 1, dann kann es leer sein
					required = !e.getValue().equalsIgnoreCase("1");
					break;
				case "tablename":
					// LookUp
					tableName = e.getValue();
					displayFormat = "lookUp";
					break;
				default:
					break;
				}
			}
			// SQL Index raussuchen, der Name ist immer gleich "field"...
			String number = (String) n.getName().subSequence(5, n.getName().length());
			Integer index = Integer.getInteger(number);

			MField mfield = getFieldFromDisplayFormat(fieldName, label, tableName, required, displayFormat, index);
			mfields.add(mfield);
		}

		// Auslesen des Titles
		section.setText(row.getValue(1).getStringValue());
		layoutSectionClient(mfields);
	}


	private MField getFieldFromDisplayFormat(String fieldName, String label, String tableName, boolean reqired, String displayFromat, Integer index) {
		MField mfield;

		switch (displayFromat.toLowerCase()) {
		case "lookup":
			mfield = new MLookupField();
			mfield.setLookupTable(tableName);
			break;
		case "shortdate":
			mfield = new MShortDateField();
			break;
		case "shorttime":
			mfield = new MShortTimeField();
			break;
		case "datetime":
			mfield = new MDateTimeField();
			break;
		default:
			throw new RuntimeException("Unknown displayFormat in Statistic: " + displayFromat + "!");
		}
		mfield.setName(fieldName);
		mfield.setLabel(label);
		mfield.setRequired(reqired);
		mfield.setSqlIndex(index);
		mfield.setNumberColumnsSpanned(4);
		mfield.setNumberRowsSpanned(1);
		mfield.setDetail(mDetail);
		return mfield;
	}

	private Control[] getTabListForPart(Composite composite) {
		if (selectAllControls) {
			return composite.getChildren();
		}
		return new Control[0];
	}

	/**
	 * Diese Methode bekommt einen Composite übergeben, und erstellt aus dem übergenen Objekt ein Section. Diese Sektion ist entweder der Head (Kopfdaten) oder
	 * eine OptionPage die sich unterhalb der Kopfdaten eingliedert. Zusätzlich wird ein TraverseListener übergeben, der das Verhalten für TAB und Enter
	 * festlegt.
	 *
	 * @param parent
	 */
	private void layoutSection(Composite parent, String title) {
		section = new MinovaSection(parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);

		section.setData(TRANSLATE_PROPERTY, title);
		section.setLayoutData(new RowData());

		ImageDescriptor imageDescriptor = ImageUtil.getImageDescriptor("Statistic", false);
		if (!imageDescriptor.equals(ImageDescriptor.getMissingImageDescriptor())) {
			section.setImage(resManager.createImage(imageDescriptor));
		}

		// Wir erstellen die Section des Details.
		mSection = new MSection(true, "open", mDetail, "Statistic", section.getText());
		mSection.setSectionAccessor(new SectionAccessor(mSection, section));

		Composite part = parent.getParent();
		// Setzen der TabListe des Parts. Dabei bestimmt SelectAllControls, ob die Toolbar mit selektiert wird.
		part.setTabList(getTabListForPart(part));
		// Wir setzen eine leere TabListe für die Perspektive, damit nicht durch die Anwendung mit Tab navigiert werden kann.
		List<Control> tabList = new ArrayList<>();
		part.getParent().setTabList(tabList.toArray(new Control[0]));

		// layoutSectionClient(headOrPageOrGrid, section, mSection);
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

	/**
	 * Gibt einen Array mit den Controls für die TabListe des Composites der Section zurück.
	 *
	 * @param mSection
	 *            der Section
	 * @param composite
	 *            der Section
	 * @return Array mit Controls
	 */
	public Control[] getTabListForSectionComposite(MSection mSection, Composite composite) {

		List<Control> tabList = new ArrayList<>();

		Control[] compositeChilds = composite.getChildren();
		for (Control control : compositeChilds) {
			if (control instanceof LookupComposite || control instanceof TextAssist || control instanceof Text) {
				MField field = (MField) control.getData(Constants.CONTROL_FIELD);
				if (!field.isReadOnly()) {
					tabList.add(control);
				}
			} else if (control instanceof NatTable) {
				tabList.add(control);
			}
		}

		return tabList.toArray(new Control[0]);
	}

	public FormToolkit getFormToolkit() {
		return formToolkit;
	}

	/**
	 * Sortiert die Tab Reihenfolge der Fields in der Section(Page)
	 *
	 * @param mSection
	 *            die Section in der die Fields sortiert werden müssen
	 * @param traverseListener
	 *            der zuzuweisende TraverseListener für die Fields
	 */
	public void sortTabList(MSection mSection) {
		List<MField> tabList = mSection.getTabList();
		Collections.sort(tabList, (f1, f2) -> {
			if (f1.getTabIndex() == f2.getTabIndex()) {
				return 0;
			} else if (f1.getTabIndex() < f2.getTabIndex()) {
				return -1;
			} else {
				return 1;
			}
		});
		mSection.setTabList(tabList);
	}

	/**
	 * Gibt einen Array mit den Controls für die TabListe der Section zurück. Wenn SelectAllControls gesetzt ist, wird das SectionControl(der Twistie) mit in
	 * den Array gesetzt.
	 *
	 * @param composite
	 *            die Setion, von der die TabListe gesetzt werden soll.
	 * @param mSection
	 * @return Array mit Controls
	 */
	public Control[] getTabListForSection(Composite composite, MSection mSection) {
		List<Control> tabList = new ArrayList<>();
		for (Control child : composite.getChildren()) {
			if (child instanceof ToolBar && selectAllControls && !mSection.isHead()) {
				tabList.add(1, child);
			} else if ((child instanceof Twistie && !selectAllControls) || (child instanceof ImageHyperlink && !selectAllControls) || child instanceof Label) {
				// Die sollen nicht in die Tabliste
			} else {
				tabList.add(child);
			}
		}
		return tabList.toArray(new Control[0]);
	}

	private void layoutSectionClient(List<MField> mfields) {
		// Client Area
		Composite clientComposite = getFormToolkit().createComposite(section);
		clientComposite.setLayout(new FormLayout());
		getFormToolkit().paintBordersFor(clientComposite);
		section.setClient(clientComposite);

		// Erstellen der Field des Section.
		createUIFields(mfields, clientComposite);
		// Sortieren der Fields nach Tab-Index.
		sortTabList(mSection);
		// Setzen der TabListe für die einzelnen Sections.
		clientComposite.setTabList(getTabListForSectionComposite(mSection, clientComposite));
		// Setzen der TabListe der Sections im Part.
		clientComposite.getParent().setTabList(getTabListForSection(section, mSection));

		// MSection wird zum MDetail hinzugefügt.
		mDetail.addMSection(mSection);
		section.requestLayout();
	}

	@Inject
	@Optional
	private void getNotified(@Named(TranslationService.LOCALE) Locale s) {
		this.locale = s;
		if (translationService != null && composite != null) {
			translate(composite);
		}
	}

	public void translate(Composite composite) {
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

}