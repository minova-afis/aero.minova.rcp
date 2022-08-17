package aero.minova.rcp.rcp.fields;

import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_BORDER;

import java.util.Locale;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.css.swt.CSSSWTConstants;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.jface.widgets.LabelFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.css.CssData;
import aero.minova.rcp.css.CssType;
import aero.minova.rcp.css.ICssStyler;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.rcp.accessor.LookupValueAccessor;
import aero.minova.rcp.widgets.LookupComposite;
import aero.minova.rcp.widgets.LookupContentProvider;

public class LookupField {

	public static final String AERO_MINOVA_RCP_LOOKUP = "LookUp";

	public static Control create(Composite composite, MField field, int row, int column, Locale locale, MPerspective perspective) {
		Label label = FieldLabel.create(composite, field);

		IEclipseContext context = perspective.getContext();

		LookupComposite lookupControl = new LookupComposite(composite, SWT.BORDER | SWT.LEFT);
		lookupControl.setMessage("...");
		lookupControl.setLabel(label);
		ContextInjectionFactory.inject(lookupControl, context); // In Context injected, damit TranslationService genutzt werden kann

		LookupContentProvider contentProvider = new LookupContentProvider(field.getLookupTable());
		contentProvider.setLookup(lookupControl);
		lookupControl.setContentProvider(contentProvider);
		ContextInjectionFactory.inject(contentProvider, context); // In Context injected, damit TranslationService genutzt werden kann

		Label descriptionLabel = LabelFactory.newLabel(SWT.LEFT).create(composite);

		FormData lookupFormData = new FormData();
		FormData labelFormData = new FormData();
		FormData descriptionLabelFormData = new FormData();

		LookupValueAccessor lookupValueAccessor = new LookupValueAccessor(field, lookupControl);
		ContextInjectionFactory.inject(lookupValueAccessor, context);
		field.setValueAccessor(lookupValueAccessor);
		lookupControl.setData(Constants.CONTROL_FIELD, field);

		lookupFormData.top = new FormAttachment(composite, FieldUtil.MARGIN_TOP + row * FieldUtil.COLUMN_HEIGHT);
		lookupFormData.left = new FormAttachment((column == 0) ? 25 : 75);
		if (column >= 2) {
			lookupFormData.right = new FormAttachment(100, -MARGIN_BORDER);
		} else {
			lookupFormData.right = new FormAttachment(50, -ICssStyler.CSS_SECTION_SPACING);
		}

		// Lookup-Felder sollen immer genau eine Zeile hoch sein
		CssData cssData = new CssData(CssType.TEXT_FIELD, column, row, field.getNumberColumnsSpanned(), 1, field.isFillToRight() || field.isFillHorizontal());
		lookupControl.setData(CssData.CSSDATA_KEY, cssData);

		labelFormData.top = new FormAttachment(lookupControl, 0, SWT.CENTER);
		labelFormData.right = new FormAttachment(lookupControl, FieldUtil.MARGIN_LEFT * -1, SWT.LEFT);
		labelFormData.width = FieldUtil.COLUMN_WIDTH;

		descriptionLabelFormData.top = new FormAttachment(lookupControl, 0, SWT.CENTER);
		descriptionLabelFormData.left = new FormAttachment(lookupControl, FieldUtil.UNIT_GAP, SWT.RIGHT); // etwas Abstand zw. LookupWidget und Text
		if (field.getNumberColumnsSpanned() == 4) {
			descriptionLabelFormData.width = FieldUtil.MARGIN_LEFT * 2 + FieldUtil.COLUMN_WIDTH * 2;
		} else {
			descriptionLabelFormData.width = 0;
		}
		if (field.getNumberRowsSpanned() > 1) { // ZB für Contact Lookups
			descriptionLabelFormData.height = field.getNumberRowsSpanned() * FieldUtil.COLUMN_HEIGHT;
			descriptionLabelFormData.top = new FormAttachment(lookupControl, 0, SWT.TOP);
		}

		label.setData(AERO_MINOVA_RCP_LOOKUP, lookupControl);
		FieldLabel.layout(label, lookupControl, row, column, field.getNumberRowsSpanned());

		lookupControl.setLayoutData(lookupFormData);
		lookupControl.setDescription(descriptionLabel);

		descriptionLabel.setLayoutData(descriptionLabelFormData);
		descriptionLabel.setData(CSSSWTConstants.CSS_CLASS_NAME_KEY, "DescriptionLabel");

		lookupControl.addTraverseListener(e -> {
			Text text = ((Text) e.getSource());
			if (text.isDisposed()) {
				return;
			}

			LookupComposite t = (LookupComposite) text.getParent();
			switch (e.detail) {
			case SWT.TRAVERSE_TAB_PREVIOUS:
			case SWT.TRAVERSE_TAB_NEXT:
			case SWT.TRAVERSE_RETURN:
				t.fillSelectedValue();
				e.doit = true;
				break;
			}
		});
		return lookupControl;
	}
}
