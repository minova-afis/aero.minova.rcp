package aero.minova.rcp.rcp.util;

import java.math.BigInteger;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.widgets.ButtonFactory;
import org.eclipse.jface.widgets.LabelFactory;
import org.eclipse.jface.widgets.TextFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Head;
import aero.minova.rcp.form.model.xsd.Page;

public class DetailUtil {

	public static final int LABEL_WIDTH_HINT = 150;
	public static final int TEXT_WIDTH_HINT = 170;
	public static final int LOOKUP_WIDTH_HINT = 170;
	public static final int UNIT_WIDTH_HINT = 20;
	public static final int BIG_TEXT_WIDTH_HINT = 490;
	public static final int LOOKUP_DESCRIPTION_WIDTH_HINT = 320;

	public static void createField(Field field, Composite composite) {
		if (!field.isVisible()) {
			return;
		}
		LabelFactory labelFactory = LabelFactory.newLabel(SWT.NONE);
		TextFactory textFactory = TextFactory.newText(SWT.BORDER).text("");
		ButtonFactory btnFactory = ButtonFactory.newButton(SWT.CHECK);
		TextFactory textMultiFactory = TextFactory.newText(SWT.BORDER | SWT.MULTI);

		// 2 Felder nebeneinander (Label + Textbox) --> (einspaltig) false
		// 4 Felder nebeneinander (label + Textbox + label + Textbox) --> (zweispaltig)
		// true
		boolean twoColumns = false;
		if (new BigInteger("4").equals(field.getNumberColumnsSpanned())) {
			twoColumns = true;
		}

		Label label = labelFactory.create(composite);
		label.setText(field.getTextAttribute());
		GridData data1 = GridDataFactory.fillDefaults().grab(true, false).align(SWT.RIGHT, SWT.TOP).create();
		data1.horizontalSpan = 1;
		data1.widthHint = LABEL_WIDTH_HINT;
		label.setLayoutData(data1);

		if (field.getLookup() != null) {
			CCombo combo = new CCombo(composite, SWT.BORDER);
			combo.setLayoutData(getGridDataFactory(twoColumns, field));
			// Description für die LookUp
			if (twoColumns) {
				Label labelDescription = labelFactory.create(composite);
				labelDescription.setText("Description");
				GridData data = GridDataFactory.fillDefaults().grab(true, false).align(SWT.LEFT, SWT.TOP).create();
				data.horizontalSpan = 3;
				data.widthHint = LOOKUP_DESCRIPTION_WIDTH_HINT;
				labelDescription.setLayoutData(data);
			}
		} else if (field.getBoolean() != null) {
			Button button = btnFactory.create(composite);
			button.setLayoutData(getGridDataFactory(twoColumns, field));
		} else {
			Text text;
			GridData gd;
			Integer numberRowSpand = null;
			gd = getGridDataFactory(twoColumns, field);

			if (field.getNumberRowsSpanned() != null) {
				numberRowSpand = Integer.valueOf(field.getNumberRowsSpanned());
				text = textMultiFactory.create(composite);
				if (numberRowSpand != null) {
					int hight = text.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
					gd.heightHint = hight * numberRowSpand;
				}
			} else {
				text = textFactory.create(composite);
			}
			text.setLayoutData(gd);
			if (twoColumns && gd.horizontalSpan != 5) {
				Label l = new Label(composite, SWT.None);
				GridData data = GridDataFactory.fillDefaults().grab(true, false).align(SWT.LEFT, SWT.TOP).create();
				data.horizontalSpan = 3;
				data.widthHint = LOOKUP_DESCRIPTION_WIDTH_HINT;
				l.setLayoutData(data);
			}
		}

		if (field.getUnitText() != null && field.getLookup() == null) {
			Label labelUnit = new Label(composite, SWT.None);
			labelUnit.setText(field.getUnitText());
			GridData data2 = GridDataFactory.fillDefaults().grab(true, false).align(SWT.LEFT, SWT.TOP).create();
			data2.horizontalSpan = 1;
			data2.widthHint = UNIT_WIDTH_HINT;
			labelUnit.setLayoutData(data2);
		}
	}

	public static Integer getSpannedHintForElement(Field field, boolean twoColumns) {
		if (field.getUnitText() != null) {
			return 1;
		} else if (field.getText() != null && twoColumns) {
			return 5;
		} else {
			return 2;
		}
	}

	public static int getWidthHintForElement(Field field, boolean twoColumns) {
		if (field.getDateTime() != null || field.getShortDate() != null || field.getShortTime() != null) {
			return TEXT_WIDTH_HINT;
		} else if ((field.getText() != null || field.getNumber() != null || field.getMoney() != null)
				&& field.getUnitText() != null) {
			return LABEL_WIDTH_HINT;
		} else if ((field.getText() != null || field.getNumber() != null || field.getMoney() != null
				|| field.getLookup() != null) && field.getUnitText() == null) {
			return LOOKUP_WIDTH_HINT;
		} else if (field.getBoolean() != null) {
			return UNIT_WIDTH_HINT;
		} else {
			if (twoColumns) {
				return BIG_TEXT_WIDTH_HINT;
			}
		}
		return -1;
	}

	private static Integer getWidthHintForElement(Field field) {
		return getWidthHintForElement(field, false);
	}

	/**
	 * 
	 * @param twoColumns
	 * @param widthHint
	 * @return
	 */
	private static GridData getGridDataFactory(boolean twoColumns, Field field) {
		GridDataFactory gridDataFactory = GridDataFactory.fillDefaults().grab(true, false).align(SWT.LEFT, SWT.TOP);
		GridData data = gridDataFactory.create();
		data.horizontalSpan = getSpannedHintForElement(field, twoColumns);
		if (twoColumns && data.horizontalSpan > 2) {
			data.grabExcessHorizontalSpace = true;
			data.horizontalAlignment = SWT.FILL;
		}
		data.widthHint = getWidthHintForElement(field);
		return data;
	}

	public static Composite createSection(FormToolkit formToolkit, Composite parent, Head head) {
		Section section = formToolkit.createSection(parent, Section.TITLE_BAR | Section.NO_TITLE_FOCUS_BOX);
		section.setLayoutData(GridDataFactory.fillDefaults().create());
		formToolkit.paintBordersFor(section);
		section.setText("Kopfdaten");
		section.setExpanded(true);
		Composite composite = formToolkit.createComposite(section, SWT.RIGHT);
		formToolkit.paintBordersFor(composite);
		section.setClient(composite);
		composite.setLayout(new GridLayout(6, false));

		return composite;
	}

	public static Composite createSection(FormToolkit formToolkit, Composite parent, Page page) {
		Section section = formToolkit.createSection(parent,
				Section.TITLE_BAR | Section.NO_TITLE_FOCUS_BOX | Section.TWISTIE);
		section.setLayoutData(GridDataFactory.fillDefaults().create());
		formToolkit.paintBordersFor(section);
		section.setText(page.getText());
		section.setExpanded(true);
		Composite composite = formToolkit.createComposite(section, SWT.RIGHT);
		formToolkit.paintBordersFor(composite);
		section.setClient(composite);
		composite.setLayout(new GridLayout(6, false));

		return composite;
	}
}
