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
	public static final int UNIT_WIDTH_HINT = 20;
	public static final int BIG_TEXT_WIDTH_HINT = 490;
	public static final int LOOKUP_DESCRIPTION_WIDTH_HINT = 320;
	
	public static final GridDataFactory gridDataFactory = GridDataFactory.fillDefaults().grab(true, false);
	public static final LabelFactory labelFactory = LabelFactory.newLabel(SWT.NONE);
	private static TextFactory textMultiFactory= TextFactory.newText(SWT.BORDER | SWT.MULTI);
	private static TextFactory textFactory = TextFactory.newText(SWT.BORDER).text("");

	public static void createField(Field field, Composite composite) {
		if (!field.isVisible()) {
			return;
		}
		// 2 Felder nebeneinander (Label + Textbox) --> (einspaltig) false
		// 4 Felder nebeneinander (label + Textbox + label + Textbox) --> (zweispaltig)
		// true
		boolean twoColumns = false;
		if (new BigInteger("4").equals(field.getNumberColumnsSpanned())) {
			twoColumns = true;
		}

		// Immer am Anfang ein Label
		labelFactory.text(field.getTextAttribute()).supplyLayoutData(gridDataFactory.align(SWT.RIGHT, SWT.TOP).hint(LABEL_WIDTH_HINT, SWT.DEFAULT)::create).create(composite);

		if (field.getLookup() != null) {
			buildLookupField(field, composite, twoColumns);
		} else if (field.getBoolean() == null) {
			buildMiddlePart(field, composite, twoColumns);
		} else if (field.getBoolean() != null) {
			throw new RuntimeException("Not yet supported");
//			Button button = btnFactory.create(composite);
//			button.setLayoutData(getGridDataFactory(twoColumns, field));
		}
		
		if (field.getUnitText() != null && field.getLookup() == null) {
			Label labelUnit = labelFactory.text(field.getUnitText()).create(composite);
			GridData data2 = gridDataFactory.align(SWT.LEFT, SWT.TOP).create();
			data2.widthHint = UNIT_WIDTH_HINT;
			labelUnit.setLayoutData(data2);
		}
	}

	private static void buildMiddlePart(Field field, Composite composite, boolean twoColumns) {
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
			GridData data = gridDataFactory.align(SWT.LEFT, SWT.TOP).create();
			data.horizontalSpan = 3;
			data.widthHint = LOOKUP_DESCRIPTION_WIDTH_HINT;
			l.setLayoutData(data);
		}
	}

	private static void buildLookupField(Field field, Composite composite, 	boolean twoColumns) {
		CCombo combo = new CCombo(composite, SWT.BORDER);
		combo.setLayoutData(getGridDataFactory(twoColumns, field));
		// Description für die LookUp
		if (twoColumns) {
			Label labelDescription = labelFactory.create(composite);
			labelDescription.setText("Description");
			GridData data = gridDataFactory.align(SWT.LEFT, SWT.TOP).create();
			data.horizontalSpan = 3;
			data.widthHint = LOOKUP_DESCRIPTION_WIDTH_HINT;
			labelDescription.setLayoutData(data);
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
			return TEXT_WIDTH_HINT;
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
	 * T
	 * @param twoColumns
	 * @param widthHint
	 * @return
	 */
	private static GridData getGridDataFactory(boolean twoColumns, Field field) {
		GridData data  = gridDataFactory.align(SWT.LEFT, SWT.TOP).create();
		data.horizontalSpan = getSpannedHintForElement(field, twoColumns);
		if (twoColumns && data.horizontalSpan > 2) {
//			data.grabExcessHorizontalSpace = true;
			data.horizontalAlignment = SWT.FILL;
		}
		data.widthHint = getWidthHintForElement(field);
		return data;
	}

	public static Composite createSection(FormToolkit formToolkit, Composite parent, Object ob) {
		Section section;
		if (ob instanceof Head) {
			section = formToolkit.createSection(parent, Section.TITLE_BAR | Section.NO_TITLE_FOCUS_BOX);
			section.setText("Kopfdaten");
		} else {
			section = formToolkit.createSection(parent,
					Section.TITLE_BAR | Section.NO_TITLE_FOCUS_BOX | Section.TWISTIE);
			section.setText(((Page) ob).getText());
		}
		section.setLayoutData(GridDataFactory.fillDefaults().create());
		formToolkit.paintBordersFor(section);
		section.setExpanded(true);
		Composite composite = formToolkit.createComposite(section, SWT.RIGHT);
		formToolkit.paintBordersFor(composite);
		section.setClient(composite);
		composite.setLayout(new GridLayout(6, false));

		return composite;
	}
}
