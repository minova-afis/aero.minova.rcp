package aero.minova.rcp.rcp.fields;

import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_HEIGHT;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_TOP;
import static aero.minova.rcp.rcp.fields.FieldUtil.SHORT_DATE_WIDTH;

import java.util.Locale;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import aero.minova.rcp.css.CssData;
import aero.minova.rcp.css.CssType;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MPeriodField;
import aero.minova.rcp.model.form.MTextField;
import aero.minova.rcp.rcp.accessor.PeriodValueAccessor;
import aero.minova.rcp.widgets.PeriodComposite;

public class PeriodField {

	private PeriodField() {
		throw new IllegalStateException("Utility class");
	}

	public static Control create(Composite composite, MField field, int row, Locale locale, MPerspective perspective) {

		PeriodComposite periodComposite = new PeriodComposite(composite, (MPeriodField) field, locale);
		ContextInjectionFactory.inject(periodComposite, perspective.getContext()); // In Context injected, damit TranslationService genutzt werden kann

		// Erstes Feld für Ausgangsdatum
		Control baseDate = periodComposite.getBaseDate();

		FormData fd = new FormData();
		fd.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT);
		fd.left = new FormAttachment(25);
		fd.width = SHORT_DATE_WIDTH;
		baseDate.setLayoutData(fd);
		baseDate.setData(CssData.CSSDATA_KEY, new CssData(CssType.DATE_FIELD, 1, row, field.getNumberColumnsSpanned(), field.getNumberRowsSpanned(), false));

		Label label = FieldLabel.create(composite, field);
		FieldLabel.layout(label, baseDate, row, 0, field.getNumberRowsSpanned());

		// Zweites Feld für Intervall
		Control dueDate = periodComposite.getDueDate();

		fd = new FormData();
		fd.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT);
		fd.left = new FormAttachment(75);
		fd.width = SHORT_DATE_WIDTH;
		dueDate.setLayoutData(fd);
		dueDate.setData(CssData.CSSDATA_KEY, new CssData(CssType.DATE_FIELD, 4, row, field.getNumberColumnsSpanned(), field.getNumberRowsSpanned(), false));

		MField temp = new MTextField();
		temp.setLabel("@PeriodField.Interval");
		label = FieldLabel.create(composite, temp);
		FieldLabel.layout(label, dueDate, row, 3, field.getNumberRowsSpanned());

		// ValueAccessor in den Context injecten, damit IStylingEngine über @Inject verfügbar ist (in AbstractValueAccessor)
		IEclipseContext context = perspective.getContext();
		PeriodValueAccessor valueAccessor = new PeriodValueAccessor(field, periodComposite);
		ContextInjectionFactory.inject(valueAccessor, context);
		field.setValueAccessor(valueAccessor);

		return periodComposite;
	}

}
