
package aero.minova.rcp.rcp.parts;

import javax.inject.Inject;
import javax.inject.Named;

import java.io.File;
import java.nio.file.Files;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.jface.widgets.LabelFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.form.model.xsd.Head;
import aero.minova.rcp.perspectiveswitcher.commands.E4WorkbenchParameterConstants;

public class WFCDetailPart {

	private static final String AERO_MINOVA_RCP_TRANSLATE_PROPERTY = "aero.minova.rcp.translate.property";
	private static final int COLUMN_WIDTH = 140;
	private static final int TEXT_WIDTH = COLUMN_WIDTH;
	private static final int MARGIN_LEFT = 5;
	private static final int MARGIN_TOP = 10;
	private static final int SECTION_WIDTH = 4 * COLUMN_WIDTH + 5 * MARGIN_LEFT; // 4 Spalten = 5 Zwischenräume
	private static final int COLUMN_HEIGHT = 30;

	@Inject
	private IDataFormService dataFormService;

	@Inject
	private IDataService dataService;

	private Form form;

	private FormToolkit formToolkit;

	private Composite composite;

	public WFCDetailPart() {

	}

	@Inject
	@Named(E4WorkbenchParameterConstants.FORM_NAME)
	String formName;

	@Inject
	MPerspective perspective;
	private TranslationService translationService;

	@PostConstruct
	public void postConstruct(Composite parent) {
		composite = parent;
		formToolkit = new FormToolkit(parent.getDisplay());
		form = perspective.getContext().get(Form.class);
		if (form == null) {
			dataService.getFileSynch(formName); // Datei ggf. vom Server holen
			form = dataFormService.getForm(formName);
		}
		if (form == null) {
			LabelFactory.newLabel(SWT.CENTER).align(SWT.CENTER).text(formName).create(parent);
			return;
		}
		perspective.getContext().set(Form.class, form); // Wir merken es uns im Context; so können andere es nutzen
		layoutForm(parent);
		translate(translationService);
	}

	private void layoutForm(Composite parent) {
		parent.setLayout(new RowLayout(SWT.VERTICAL));

		for (Object headOrPage : form.getDetail().getHeadAndPage()) {
			if (headOrPage instanceof Head) {
				layoutHead(parent, (Head) headOrPage);
			}
		}
	}

	private void layoutHead(Composite parent, Head head) {
		Section headSection = formToolkit.createSection(parent,
				ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		RowData headLayoutData = new RowData();
		headLayoutData.width = SECTION_WIDTH;
		headSection.setLayoutData(headLayoutData);
		headSection.setText("@Head");
		headSection.setData(AERO_MINOVA_RCP_TRANSLATE_PROPERTY, "@Head");
		headSection.setLayout(new FormLayout());

		// Client Area
		Composite composite = formToolkit.createComposite(headSection);
		composite.setLayout(new FormLayout());
		formToolkit.paintBordersFor(composite);
		headSection.setClient(composite);

		// Fields
	}

	@Inject
	@Optional
	private void getNotified1(@Named(TranslationService.LOCALE) Locale s) {
		translate(translationService);
	}

	@Inject
	private void translate(TranslationService translationService) {
		this.translationService = translationService;
		if (translationService != null && composite != null)
			translate(composite);
	}

	private void translate(Composite composite) {
		for (Control control : composite.getChildren()) {
			if (control.getData(AERO_MINOVA_RCP_TRANSLATE_PROPERTY) != null) {
				String property = (String) control.getData(AERO_MINOVA_RCP_TRANSLATE_PROPERTY);
				String value = translationService.translate(property, null);
				if (control instanceof ExpandableComposite) {
					((ExpandableComposite) control).setText(value);
				} else if (control instanceof Label) {
					((Label) control).setText(value);
				} else if (control instanceof Button) {
					((Button) control).setText(value);
				}
				if (control instanceof Composite) {
					translate((Composite) control);
				}
			}
		}
	}

}