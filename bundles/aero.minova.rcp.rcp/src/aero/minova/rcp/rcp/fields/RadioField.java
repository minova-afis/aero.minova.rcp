package aero.minova.rcp.rcp.fields;

import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_BORDER;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_LOCALE;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_PROPERTY;

import java.util.Locale;
import java.util.Objects;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.css.swt.CSSSWTConstants;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.jface.widgets.ButtonFactory;
import org.eclipse.jface.widgets.LabelFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.css.CssData;
import aero.minova.rcp.css.CssType;
import aero.minova.rcp.css.ICssStyler;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MBooleanField;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MRadioField;
import aero.minova.rcp.rcp.accessor.BooleanValueAccessor;
import aero.minova.rcp.rcp.accessor.RadioValueAccessor;

@SuppressWarnings("restriction")
public class RadioField {

	private RadioField() {
		throw new IllegalStateException("Utility class");
	}

	public static Control create(Composite composite, MField field, int row, int column, Locale locale, MPerspective perspective) {
		MRadioField radioField = (MRadioField) field;

		// Radiobox Label erstellen, dass über die gesamte breite geht
		Label label = LabelFactory.newLabel(SWT.LEFT).text(field.getLabel()).create(composite);
		label.setData(TRANSLATE_PROPERTY, field.getLabel());
		label.setData(CSSSWTConstants.CSS_CLASS_NAME_KEY, "Description");

		FormData fdL = new FormData();
		fdL.right = new FormAttachment(100, -MARGIN_BORDER);
		fdL.left = new FormAttachment(0);
		fdL.top = new FormAttachment(composite, FieldUtil.MARGIN_TOP + row * FieldUtil.COLUMN_HEIGHT);
		label.setLayoutData(fdL);

		CssData cssDataLabel = new CssData(CssType.LABEL_TEXT_FIELD, column, row, 4, 1, true);
		label.setData(CssData.CSSDATA_KEY, cssDataLabel);

		RadioValueAccessor rva = new RadioValueAccessor(radioField, label);
		ContextInjectionFactory.inject(rva, perspective.getContext());
		radioField.setValueAccessor(rva);

		// Composite für die Buttons
		Composite comp = new Composite(composite, SWT.NONE);
		comp.setLayout(new FormLayout());

		FormData fdG = new FormData();
		fdG.right = new FormAttachment(100, -MARGIN_BORDER);
		fdG.left = new FormAttachment(0);
		fdG.top = new FormAttachment(composite, FieldUtil.MARGIN_TOP + row * FieldUtil.COLUMN_HEIGHT);
		comp.setLayoutData(fdG);

		CssData cssDataComposite = new CssData(CssType.LABEL_TEXT_FIELD, column, row, 4, (int) Math.ceil((double) radioField.getRadiobuttons().size() / 3),
				true);
		comp.setData(CssData.CSSDATA_KEY, cssDataComposite);

		for (MBooleanField b : radioField.getRadiobuttons()) {
			String optionLabel = b.getLabel();
			double indexInList = radioField.getRadiobuttons().indexOf(b);
			Button button = ButtonFactory.newButton(SWT.RADIO).text(optionLabel).create(comp);

			b.setDetail(field.getDetail());

			// ValueAccessor in den Context injecten, damit IStylingEngine über @Inject verfügbar ist (in AbstractValueAccessor)
			IEclipseContext context = perspective.getContext();
			BooleanValueAccessor valueAccessor = new BooleanValueAccessor(b, button);
			ContextInjectionFactory.inject(valueAccessor, context);
			b.setValueAccessor(valueAccessor);

			FormData fd = new FormData();
			fd.top = new FormAttachment(comp, (int) (FieldUtil.MARGIN_TOP + (row + (Math.floor(indexInList / 3)) * ICssStyler.CSS_ROW_HEIGHT)));
			fd.left = new FormAttachment((int) (25 * (indexInList % 3 + 1)));
			button.setLayoutData(fd);

			button.setData(TRANSLATE_PROPERTY, optionLabel);
			button.setData(TRANSLATE_LOCALE, locale);
			button.setData(Constants.CONTROL_FIELD, b);
			button.setData(Constants.CONTROL_VALUE, b.getName());
			button.setData(Constants.CONTROL_DATATYPE, DataType.BOOLEAN);

			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					String name = (String) button.getData(Constants.CONTROL_VALUE);
					if (Objects.equals(field.getValue(), new Value(name))) {
						field.setValue(null, true);
					} else {
						field.setValue(new Value(name), true);
					}
				}
			});
		}

		return label;
	}

}
