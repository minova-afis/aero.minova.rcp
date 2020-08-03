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

		LabelFactory labelFactory = LabelFactory.newLabel(SWT.NONE);
		TextFactory textFactory = TextFactory.newText(SWT.BORDER).text("");
		ButtonFactory btnFactory = ButtonFactory.newButton(SWT.CHECK);
		TextFactory textMultiFactory = TextFactory.newText(SWT.BORDER | SWT.MULTI);

		Integer numberOfColumns = null;
		if (field.getNumberColumnsSpanned() == null
				|| field.getNumberColumnsSpanned().compareTo(new BigInteger("2")) == 0) {
			numberOfColumns = 1;
		} else {
			numberOfColumns = 4;
		}

		if (field.isVisible()) {
			Label label = labelFactory.create(composite);
			label.setText(field.getTextAttribute());
			label.setLayoutData(getGridDataFactory(null, getWidthHintForElement(field)));

			if (field.getLookup() != null) {
				CCombo combo = new CCombo(composite, SWT.BORDER);
				combo.setLayoutData(getGridDataFactory(numberOfColumns, getWidthHintForElement(field)));
				// Description für die LookUp
				Label labelDescription = labelFactory.create(composite);
				labelDescription.setText("Description");
				labelDescription.setLayoutData(getGridDataFactory(null, getWidthHintForElement(field)));
			} else if (field.getBoolean() != null) {
				Button button = btnFactory.create(composite);
				button.setLayoutData(getGridDataFactory(null, getWidthHintForElement(field)));
			} else {
				Text text;
				GridData gd;
				Integer numberRowSpand = null;

				if (numberOfColumns == 4) {
					gd = getGridDataFactory(numberOfColumns, getWidthHintForElement(field, 4));
				} else {
					gd = getGridDataFactory(numberOfColumns, getWidthHintForElement(field));
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
		}

		if (field.getUnitText() != null & field.getLookup() == null) {
			Label labelUnit = new Label(composite, SWT.None);
			labelUnit.setText(field.getUnitText());
			labelUnit.setLayoutData(getGridDataFactory(null, getWidthHintForElement(field)));
		}
	}

	private static Integer getWidthHintForElement(Field field, Integer numberOfColumns) {
		if (numberOfColumns == null) {
			numberOfColumns = 1;
		}
		if (field.getDateTime() != null || field.getLookup() != null || field.getShortDate() != null
				|| field.getShortTime() != null) {
			return 150;
		} else if (field.getUnitText() != null) {
			return 20;
		} else if (field.getBoolean() != null) {
			return 20;
		} else {
			return 150 * numberOfColumns;
		}
	}

	private static Integer getWidthHintForElement(Field field) {
		return getWidthHintForElement(field, 1);
	}

	/**
	 * 
	 * @param numberColumns
	 * @param widthHint
	 * @param numberRowSpand
	 * @return
	 */
	private static GridData getGridDataFactory(Integer numberColumns, Integer widthHint) {
		GridDataFactory gridDataFactory = GridDataFactory.fillDefaults().grab(true, false).align(SWT.LEFT, SWT.TOP);
		GridData data = gridDataFactory.create();
		if (widthHint != null) {
			data.widthHint = widthHint;
		}
		if (numberColumns == null || numberColumns <= 2) {
			data.horizontalSpan = 1;
		} else {
			data.horizontalSpan = 4;
		}
		return data;
	}

	public static Composite createSection(FormToolkit formToolkit, Composite parent, Head head) {
		Section section = formToolkit.createSection(parent, Section.TITLE_BAR | Section.NO_TITLE_FOCUS_BOX);

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
