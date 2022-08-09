package aero.minova.rcp.rcp.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.opal.textassist.TextAssist;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Twistie;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.css.widgets.MinovaSectionData;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MSection;
import aero.minova.rcp.widgets.LookupComposite;

public class TabUtil {

	private TabUtil() {}

	public static Control[] getTabListForPart(Composite composite, boolean selectAllControls) {
		if (selectAllControls) {
			return composite.getChildren();
		}
		return new Control[0];
	}

	/**
	 * Updated die Tab-Liste eines SectionComposites.
	 *
	 * @param composite
	 *            der Section
	 * @return Array mit Controls
	 */
	public static void updateTabListOfSectionComposite(Composite composite) {

		List<Control> tabList = new ArrayList<>();

		Control[] compositeChilds = composite.getChildren();
		for (Control control : compositeChilds) {
			if (control instanceof LookupComposite || control instanceof TextAssist || control instanceof Text) {
				MField field = (MField) control.getData(Constants.CONTROL_FIELD);
				if (!field.isReadOnly()) {
					tabList.add(control);
				}
			} else if (control instanceof NatTable) {
				tabList.add(control);
			}
		}

		composite.setTabList(tabList.toArray(new Control[0]));
	}

	/**
	 * Liefert ein nach der Order sortierten Array von Sections zurück.
	 * 
	 * @param parent
	 *            DetailPart
	 * @return nach der Order sortierte TabList
	 */
	public static Control[] getSortedSectionTabList(Composite parent) {
		List<Control> tabList = new ArrayList<>(parent.getChildren().length);

		for (Control section : parent.getChildren()) {
			tabList.add(section);
		}

		Collections.sort(tabList, (f1, f2) -> {
			int order1 = ((MinovaSectionData) f1.getLayoutData()).getOrder();
			int order2 = ((MinovaSectionData) f2.getLayoutData()).getOrder();
			boolean hFill1 = ((MinovaSectionData) f1.getLayoutData()).isHorizontalFill();
			boolean hFill2 = ((MinovaSectionData) f2.getLayoutData()).isHorizontalFill();

			if (hFill1 && !hFill2) {
				return 1;
			} else if (!hFill1 && hFill2) {
				return -1;
			} else if (order1 == order2) {
				return 0;
			} else if (order1 < order2) {
				return -1;
			} else {
				return 1;
			}
		});

		return tabList.toArray(new Control[0]);
	}

	/**
	 * Gibt einen Array mit den Controls für die TabListe der Section zurück. Wenn SelectAllControls gesetzt ist, wird das SectionControl(der Twistie) mit in
	 * den Array gesetzt.
	 *
	 * @param composite
	 *            die Setion, von der die TabListe gesetzt werden soll.
	 * @param mSection
	 * @return Array mit Controls
	 */
	public static Control[] getTabListForSection(Composite composite, MSection mSection, boolean selectAllControls) {
		List<Control> tabList = new ArrayList<>();
		for (Control child : composite.getChildren()) {
			if (child instanceof ToolBar && selectAllControls && !mSection.isHead()) {
				tabList.add(1, child);
			} else if (child instanceof Twistie || (child instanceof ImageHyperlink && !selectAllControls) || child instanceof Label) {
				// Die sollen nicht in die Tabliste
			} else {
				tabList.add(child);
			}
		}
		return tabList.toArray(new Control[0]);
	}

}
