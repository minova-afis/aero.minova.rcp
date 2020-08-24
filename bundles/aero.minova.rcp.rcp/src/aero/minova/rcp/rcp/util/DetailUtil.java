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
		GridData data1 = GridDataFactory.fillDefaults().grab(true, false).align(SWT.LEFT, SWT.TOP).create();
		data1.horizontalSpan = 1;
		label.setLayoutData(data1);

		if (field.getLookup() != null) {
			CCombo combo = new CCombo(composite, SWT.BORDER);
			combo.setLayoutData(getGridDataFactory(twoColumns, field));
			// Description für die LookUp
			if (twoColumns) {
				Label labelDescription = labelFactory.create(composite);
				labelDescription.setText("Description");
				GridDataFactory gridDataFactoryLabelDescription = GridDataFactory.fillDefaults().grab(true, false)
						.align(SWT.LEFT, SWT.TOP);
				GridData data = gridDataFactoryLabelDescription.create();
				data.horizontalSpan = 3;
				labelDescription.setLayoutData(data);
			}
		} else if (field.getBoolean() != null) {
			Button button = btnFactory.create(composite);
			button.setLayoutData(getGridDataFactory(twoColumns, field));
		} else {
			Text text;
			GridData gd;
			Integer numberRowSpand = null;
			if (!twoColumns && field.getText() != null) {
				gd = getGridDataFactory(twoColumns, field);
			} else {
				gd = getGridDataFactory(twoColumns, field);
			}
			if (field.getUnitText() == null) {
				// Wenn es keine Einheit gibt, muss das Feld 2 Spaltenbreiten einnehmen
				gd.horizontalSpan = gd.horizontalSpan + 1;
			}

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
		}

		if (field.getUnitText() != null & field.getLookup() == null) {
			Field fielddummy = new Field();
			fielddummy.setUnitText("Test");
			Label labelUnit = new Label(composite, SWT.None);

			labelUnit.setText(field.getUnitText());
			labelUnit.setLayoutData(getGridDataFactory(twoColumns, fielddummy));
		}
	}

	private static Integer getSpannedHintForElement(Field field, boolean twoColumns) {
		if (field.getText() != null && field.getUnitText() != null) {
			return 1;
		} else if (field.getText() != null && field.getUnitText() == null && twoColumns) {
			return 5;
		} else {
			return 2;
		}
	}

	private static Integer getWidthHintForElement(Field field, boolean twoColumns) {
		if (field.getDateTime() != null || field.getShortDate() != null
				|| field.getShortTime() != null) {
			return 150;
		} else if ((field.getText() != null || field.getNumber() != null || field.getMoney() != null)
				&& field.getUnitText() != null) {
			return 150;
		}
		else if ((field.getText() != null || field.getNumber() != null || field.getMoney() != null || field.getLookup() != null)
				&& field.getUnitText() == null) {
			return 150 + 20;
		} else if (field.getBoolean() != null) {
			return 20;
		} else {
			if (twoColumns) {
				return ((3 * 150) + (20));
			}
			return 150 * 3;
		}
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
