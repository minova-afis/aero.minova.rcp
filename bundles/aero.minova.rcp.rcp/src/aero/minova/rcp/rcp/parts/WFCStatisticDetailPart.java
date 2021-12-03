
package aero.minova.rcp.rcp.parts;

import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_HEIGHT;
import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_WIDTH;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_LEFT;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_TOP;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_PROPERTY;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.ImageUtil;
import aero.minova.rcp.form.setup.util.XBSUtil;
import aero.minova.rcp.form.setup.xbs.Map.Entry;
import aero.minova.rcp.form.setup.xbs.Node;
import aero.minova.rcp.form.setup.xbs.Preferences;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
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
import aero.minova.rcp.model.util.ErrorObject;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.rcp.accessor.AbstractValueAccessor;
import aero.minova.rcp.rcp.accessor.DetailAccessor;
import aero.minova.rcp.rcp.accessor.SectionAccessor;
import aero.minova.rcp.rcp.fields.BooleanField;
import aero.minova.rcp.rcp.fields.DateTimeField;
import aero.minova.rcp.rcp.fields.LookupField;
import aero.minova.rcp.rcp.fields.NumberField;
import aero.minova.rcp.rcp.fields.ShortDateField;
import aero.minova.rcp.rcp.fields.ShortTimeField;
import aero.minova.rcp.rcp.fields.TextField;
import aero.minova.rcp.rcp.handlers.ShowErrorDialogHandler;
import aero.minova.rcp.rcp.util.TabUtil;
import aero.minova.rcp.rcp.util.TranslateUtil;
import aero.minova.rcp.rcp.widgets.MinovaSection;
import aero.minova.rcp.widgets.MinovaNotifier;

@SuppressWarnings("restriction")
public class WFCStatisticDetailPart {

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
	private Composite parent;
	private MDetail mDetail;
	private Locale locale;
	private LocalResourceManager resManager;
	private MinovaSection section;
	private MSection mSection;
	private Row currentRow;

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

	@Inject
	EModelService model;

	@Inject
	IEclipseContext partContext;

	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell shell;

	@PostConstruct
	public void postConstruct(Composite parent) {
		this.parent = parent;
		formToolkit = new FormToolkit(parent.getDisplay());
		resManager = new LocalResourceManager(JFaceResources.getResources(), parent);
		mDetail = new MDetail();
		mDetail.setDetailAccessor(new DetailAccessor(mDetail));
		parent.setLayout(new RowLayout(SWT.VERTICAL));
		layoutSection();
	}

	/**
	 * Initiales Erstellen der Section und MSection
	 * 
	 * @param parent
	 * @param title
	 */
	private void layoutSection() {
		section = new MinovaSection(parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		section.setData(TRANSLATE_PROPERTY, "@" + STATISTIC);
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

		TranslateUtil.translate(parent, translationService, locale);
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
		currentRow = row;
		mDetail.getFields().clear();
		mSection.getTabList().clear();

		Preferences preferences = (Preferences) mApplication.getTransientData().get(Constants.XBS_FILE_NAME);
		Node statisticNode = XBSUtil.getNodeWithName(preferences, row.getValue(0).getStringValue());
		List<MField> mFields = new ArrayList<>();
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

			MField mField = getFieldFromDisplayFormat(displayFormat);
			mField.setName(fieldName);
			mField.setLabel(label);
			mField.setLookupTable(tableName);
			mField.setRequired(required);
			mField.setSqlIndex(Integer.parseInt(n.getName().substring(5, n.getName().length())));
			mField.setNumberColumnsSpanned(4);
			mField.setNumberRowsSpanned(1);
			mField.setDetail(mDetail);
			mField.setMSection(mSection);
			mFields.add(mField);
		}

		// Auslesen des Titles
		section.setText(row.getValue(1).getStringValue());
		section.setData(TRANSLATE_PROPERTY, row.getValue(1).getStringValue());

		// Sortieren der Felder und Erstellen im UI
		mFields.sort((m1, m2) -> m1.getSqlIndex().compareTo(m2.getSqlIndex()));
		for (MField f : mFields) {
			mDetail.putField(f);
			mSection.addTabField(f);
		}
		layoutSectionClient(mFields);
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

	private void layoutSectionClient(List<MField> mFields) {

		// Alten Felder im UI löschen
		if (section.getClient() != null) {
			section.getClient().dispose();
		}

		// Client Area neu erstellen
		Composite clientComposite = formToolkit.createComposite(section);
		clientComposite.setLayout(new FormLayout());
		formToolkit.paintBordersFor(clientComposite);
		section.setClient(clientComposite);

		createUIFields(mFields, clientComposite);

		// Tab-Liste
		TabUtil.sortTabList(mSection);
		clientComposite.setTabList(TabUtil.getTabListForSectionComposite(mSection, clientComposite));
		clientComposite.getParent().setTabList(TabUtil.getTabListForSection(section, mSection, selectAllControls));

		// Übersetzen und zeichen
		TranslateUtil.translate(parent, translationService, locale);
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

	private void createField(Composite clientComposite, MField field, int row, int column) {
		if (field instanceof MBooleanField) {
			BooleanField.create(clientComposite, field, row, column, locale, mPerspective);
		} else if (field instanceof MNumberField) {
			NumberField.create(clientComposite, (MNumberField) field, row, column, locale, mPerspective);
		} else if (field instanceof MDateTimeField) {
			DateTimeField.create(clientComposite, field, row, column, locale, timezone, mPerspective, translationService);
		} else if (field instanceof MShortDateField) {
			ShortDateField.create(clientComposite, field, row, column, locale, timezone, mPerspective, translationService);
		} else if (field instanceof MShortTimeField) {
			ShortTimeField.create(clientComposite, field, row, column, locale, timezone, mPerspective, translationService);
		} else if (field instanceof MLookupField) {
			LookupField.create(clientComposite, field, row, column, locale, mPerspective);
		} else if (field instanceof MTextField || field instanceof MParamStringField) {
			TextField.create(clientComposite, field, row, column, mPerspective);
		}
	}

	private void addBottonMargin(Composite clientComposite, int row, int column) {
		// Abstand nach unten
		Label spacing = new Label(clientComposite, SWT.NONE);
		FormData spacingFormData = new FormData();
		spacingFormData.top = new FormAttachment(clientComposite, MARGIN_TOP + row * COLUMN_HEIGHT + MARGIN_TOP);
		spacingFormData.left = new FormAttachment(clientComposite, MARGIN_LEFT * (column + 1) + (column + 1) * COLUMN_WIDTH);
		spacingFormData.height = 0;
		spacing.setLayoutData(spacingFormData);
	}

	@Inject
	@Optional
	private void getNotified(@Named(TranslationService.LOCALE) Locale s) {
		this.locale = s;
		if (translationService != null && parent != null) {
			TranslateUtil.translate(parent, translationService, locale);
		}
	}

	@Optional
	@Inject
	public void newFields(@UIEventTopic(Constants.BROKER_NEWENTRY) Map<MPerspective, String> map) {
		if (map.keySet().iterator().next() != mPerspective) {
			return;
		}

		for (MField f : mDetail.getFields()) {
			f.setValue(null, false);
		}
		((AbstractValueAccessor) mSection.getTabList().get(0).getValueAccessor()).getControl().setFocus();
	}

	@Inject
	@Optional
	public void showErrorMessage(@UIEventTopic(Constants.BROKER_SHOWERRORMESSAGE) String message) {
		MPerspective activePerspective = model.getActivePerspective(partContext.get(MWindow.class));
		if (activePerspective.equals(mPerspective)) {
			MessageDialog.openError(shell, getTranslation("Error"), getTranslation(message));
		}
	}

	@Inject
	@Optional
	public void showErrorMessage(@UIEventTopic(Constants.BROKER_SHOWERROR) ErrorObject et) {
		MPerspective activePerspective = model.getActivePerspective(partContext.get(MWindow.class));
		if (activePerspective.equals(mPerspective)) {
			Table errorTable = et.getErrorTable();
			Value vMessageProperty = errorTable.getRows().get(0).getValue(0);
			String messageproperty = "@" + vMessageProperty.getStringValue();
			String value = translationService.translate(messageproperty, null);
			// Ticket number {0} is not numeric
			if (errorTable.getColumnCount() > 1) {
				List<String> params = new ArrayList<>();
				for (int i = 1; i < errorTable.getColumnCount(); i++) {
					Value v = errorTable.getRows().get(0).getValue(i);
					params.add(v.getStringValue());
				}
				value = MessageFormat.format(value, params.toArray(new String[0]));
			}
			value += "\n\nUser : " + et.getUser();
			value += "\nProcedure/View: " + et.getProcedureOrView();

			if (et.getT() == null) {
				MessageDialog.openError(shell, getTranslation("Error"), value);
			} else {
				ShowErrorDialogHandler.execute(shell, getTranslation("Error"), value, et.getT());
			}
		}
	}

	@Inject
	@Optional
	public void showNotification(@UIEventTopic(Constants.BROKER_SHOWNOTIFICATION) String message) {
		MPerspective activePerspective = model.getActivePerspective(partContext.get(MWindow.class));
		if (activePerspective.equals(mPerspective)) {
			openNotificationPopup(message);
		}
	}

	public void openNotificationPopup(String message) {
		if (!shell.getDisplay().isDisposed()) {
			MinovaNotifier.show(shell, getTranslation(message), getTranslation("Notification"));
		}
	}

	private String getTranslation(String translate) {
		if (!translate.startsWith("@")) {
			translate = "@" + translate;
		}
		return translationService.translate(translate, null);
	}

	public Composite getComposite() {
		return parent;
	}

	public Row getCurrentRow() {
		return currentRow;
	}

	public MDetail getMDetail() {
		return mDetail;
	}

}