package aero.minova.rcp.rcp.fields;

import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_HEIGHT;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_LEFT;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_TOP;
import static aero.minova.rcp.rcp.fields.FieldUtil.TEXT_WIDTH;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_LOCALE;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_PROPERTY;

import java.util.Locale;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.css.swt.CSSSWTConstants;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.jface.widgets.ButtonFactory;
import org.eclipse.jface.widgets.LabelFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.css.CssData;
import aero.minova.rcp.css.CssType;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.rcp.accessor.BooleanValueAccessor;

@SuppressWarnings("restriction")
public class RadioBooleanField {

	private RadioBooleanField() {
		throw new IllegalStateException("Utility class");
	}

	public static Control create(Composite composite, MField field, int row, int column, Locale locale, MPerspective perspective) {
		// Alle Labels, die mit # zusammengeschrieben sind, von einander trennen
		// Label#box1#box2#box3#box4
		String[] labels = field.getLabel().split("#");

		Group group = new Group(composite, SWT.NONE | SWT.NO);
		group.setLayout(new RowLayout(SWT.HORIZONTAL));

		// Radiobox Label erstellen, dass über die gesamte breite geht
		String labelText = labels[0] == null ? "" : labels[0];
		Label label = LabelFactory.newLabel(SWT.RIGHT).text(labelText).create(group);
		label.setData(TRANSLATE_PROPERTY, labelText);
		label.setData(CSSSWTConstants.CSS_CLASS_NAME_KEY, "Description");

		FormData fdL = new FormData();
		fdL.right = new FormAttachment(group, MARGIN_LEFT * -1, SWT.LEFT);
		fdL.left = new FormAttachment((column == 0) ? 0 : 50);
		fdL.top = new FormAttachment(group, 0, SWT.CENTER);
		label.setLayoutData(fdL);

		CssData cssData = new CssData(CssType.LABEL_TEXT_FIELD, column, row, 1, 4, true);
		label.setData(CssData.CSSDATA_KEY, cssData);

		// Erstellen der RadioBoxen (Anzahl = # Anzahl)
		for (int i = 1; i < labels.length; i++) {
			String optionLabel = labels[i] == null ? "" : labels[i];

			Button button = ButtonFactory.newButton(SWT.RADIO).text(optionLabel).create(group);

			// ValueAccessor in den Context injecten, damit IStylingEngine über @Inject verfügbar ist (in AbstractValueAccessor)
			IEclipseContext context = perspective.getContext();
			BooleanValueAccessor valueAccessor = new BooleanValueAccessor(field, button);
			ContextInjectionFactory.inject(valueAccessor, context);
			field.setValueAccessor(valueAccessor);

			FormData fd = new FormData();
			fd.top = new FormAttachment(group, MARGIN_TOP + row * COLUMN_HEIGHT);
			fd.width = TEXT_WIDTH;
			button.setLayoutData(fd);

			button.setData(CssData.CSSDATA_KEY, new CssData(CssType.TEXT_FIELD, column, row, 1, 1, false));
			button.setData(CSSSWTConstants.CSS_CLASS_NAME_KEY, "Description");

			button.setData(TRANSLATE_PROPERTY, optionLabel);
			button.setData(TRANSLATE_LOCALE, locale);
			button.setData(Constants.CONTROL_FIELD, field);
			// Gleich wie TRANSLATE_PROPERTY, da hier die Übersetzung direkt vom Anwender eingetragen wird
			button.setData(Constants.CONTROL_VALUE, optionLabel);
			button.setData(Constants.CONTROL_DATATYPE, DataType.BOOLEAN);

			button.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					field.setValue(new Value(button.getData(Constants.CONTROL_VALUE)), true);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					field.setValue(null, true);
				}
			});

		}

		return label;
	}

}
