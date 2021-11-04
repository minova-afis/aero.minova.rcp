
package aero.minova.rcp.rcp.parts;

import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_HEIGHT;
import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_WIDTH;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_LEFT;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_TOP;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_PROPERTY;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

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
import aero.minova.rcp.rcp.util.TabUtil;
import aero.minova.rcp.rcp.util.TranslateUtil;
import aero.minova.rcp.rcp.widgets.MinovaSection;

@SuppressWarnings("restriction")
public class WFCDetailPartStatistic {

	private static final int MARGIN_SECTION = 8;
	public static final int SECTION_WIDTH = 4 * COLUMN_WIDTH + 3 * MARGIN_LEFT + 2 * MARGIN_SECTION + 50; // 4 Spalten = 5 Zwischenräume
	private static final String STATISTIC = "Statistic";

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.TIMEZONE)
	String timezone;

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.SELECT_ALL_CONTROLS)
	boolean selectAllControls;

	private FormToolkit formToolkit;
	private Composite composite;
	private MDetail mDetail;
	private Locale locale;
	private LocalResourceManager resManager;
	private MinovaSection section;
	private MSection mSection;

	@Inject
	TranslationService translationService;

	@Inject
	EPartService partService;

	@Inject
	MWindow mwindow;

	@Inject
	EModelService eModelService;

	@Inject
	MApplication mApplication;

	@Inject
	MPerspective mPerspective;

	@PostConstruct
	public void postConstruct(Composite parent) {
		composite = parent;
		formToolkit = new FormToolkit(parent.getDisplay());
		resManager = new LocalResourceManager(JFaceResources.getResources(), parent);
		mDetail = new MDetail();
		mDetail.setDetailAccessor(new DetailAccessor(mDetail));
		layoutSection(parent, STATISTIC);
	}

	/**
	 * Initiales Erstellen der Section und MSection
	 * 
	 * @param parent
	 * @param title
	 */
	private void layoutSection(Composite parent, String title) {
		section = new MinovaSection(parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		section.setData(TRANSLATE_PROPERTY, title);
		RowData rowData = new RowData();
		rowData.width = SECTION_WIDTH;
		section.setLayoutData(rowData);

		ImageDescriptor imageDescriptor = ImageUtil.getImageDescriptor(STATISTIC, false);
		if (!imageDescriptor.equals(ImageDescriptor.getMissingImageDescriptor())) {
			section.setImage(resManager.createImage(imageDescriptor));
		}

		mSection = new MSection(true, "open", mDetail, STATISTIC, section.getText());
		mSection.setSectionAccessor(new SectionAccessor(mSection, section));
		mDetail.addMSection(mSection);

		// TabListe des Parts
		Composite cTabFolder = parent.getParent();
		cTabFolder.setTabList(TabUtil.getTabListForPart(cTabFolder, selectAllControls));
		cTabFolder.getParent().setTabList(new Control[0]);
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
			String displayFormat = "";
			boolean required = true;
			String fieldName = null;
			String label = null;
			String tableName = null;

			for (Entry e : n.getMap().getEntry()) {
				switch (e.getKey().toLowerCase()) {
				case "":
					label = e.getValue();
					break;
				case "displayformat":
					displayFormat = e.getValue();
					break;
				case "fieldname":
					fieldName = e.getValue();
					break;
				case "nullable":
					required = !e.getValue().equalsIgnoreCase("1");
					break;
				case "tablename":
					tableName = e.getValue();
					displayFormat = "lookup";
					break;
				default:
					break;
				}
			}

			MField mfield = getFieldFromDisplayFormat(displayFormat);
			mfield.setName(fieldName);
			mfield.setLabel(label);
			mfield.setLookupTable(tableName);
			mfield.setRequired(required);
			mfield.setSqlIndex(Integer.parseInt(n.getName().substring(5, n.getName().length())));
			mfield.setNumberColumnsSpanned(4);
			mfield.setNumberRowsSpanned(1);
			mfield.setDetail(mDetail);
			mfields.add(mfield);
		}

		// Auslesen des Titles
		section.setText(row.getValue(1).getStringValue());

		// Sortieren der Felder und Erstellen im UI
		mfields.sort((m1, m2) -> m1.getSqlIndex().compareTo(m2.getSqlIndex()));
		layoutSectionClient(mfields);
	}

	private MField getFieldFromDisplayFormat(String displayFormat) {
		MField mField;
		switch (displayFormat.toLowerCase()) {
		case "lookup":
			mField = new MLookupField();
			break;
		case "shortdate":
			mField = new MShortDateField();
			break;
		case "shorttime":
			mField = new MShortTimeField();
			break;
		case "datetime":
			mField = new MDateTimeField();
			break;
		default:
			throw new RuntimeException("Unknown displayFormat in Statistic: " + displayFormat + "!");
		}
		return mField;
	}

	private void layoutSectionClient(List<MField> mfields) {

		// Alten Felder löschen (in UI UND Model)
		if (section.getClient() != null) {
			section.getClient().dispose();
		}
		mDetail.getFields().clear();
		mSection.getTabList().clear();

		// Client Area neu erstellen
		Composite clientComposite = formToolkit.createComposite(section);
		clientComposite.setLayout(new FormLayout());
		formToolkit.paintBordersFor(clientComposite);
		section.setClient(clientComposite);

		createUIFields(mfields, clientComposite);

		// Tab-Liste
		TabUtil.sortTabList(mSection);
		clientComposite.setTabList(TabUtil.getTabListForSectionComposite(mSection, clientComposite));
		clientComposite.getParent().setTabList(TabUtil.getTabListForSection(section, mSection, selectAllControls));

		// Übersetzen und zeichen
		TranslateUtil.translate(composite, translationService, locale);
		section.requestLayout();
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

	private void addBottonMargin(Composite composite, int row, int column) {
		// Abstand nach unten
		Label spacing = new Label(composite, SWT.NONE);
		FormData spacingFormData = new FormData();
		spacingFormData.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT + MARGIN_TOP);
		spacingFormData.left = new FormAttachment(composite, MARGIN_LEFT * (column + 1) + (column + 1) * COLUMN_WIDTH);
		spacingFormData.height = 0;
		spacing.setLayoutData(spacingFormData);
	}

	@Inject
	@Optional
	private void getNotified(@Named(TranslationService.LOCALE) Locale s) {
		this.locale = s;
		if (translationService != null && composite != null) {
			TranslateUtil.translate(composite, translationService, locale);
		}
	}

}