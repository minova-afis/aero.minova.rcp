package aero.minova.rcp.preferencewindow.builder;

import org.eclipse.nebula.widgets.opal.preferencewindow.PWRow;
import org.eclipse.nebula.widgets.opal.preferencewindow.PWTab;
import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWButton;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWCheckbox;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWCombo;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWDirectoryChooser;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWFileChooser;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWIntegerText;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWLabel;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWPasswordText;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWSeparator;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWStringText;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWTextarea;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWURLText;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;

import aero.minova.rcp.preferencewindow.control.CostumPWFloatText;
import aero.minova.rcp.preferencewindow.control.CostumPWIntegerText;

/**
 * Liefert Methoden zum Erstellen von Preference Window bezogenen Widgets
 * 
 * @author bauer
 *
 */
public class PreferenceWindowBuilder {

	PreferenceWindow window;

	public static PreferenceWindowBuilder newPWB() {
		return new PreferenceWindowBuilder();
	}

	/**
	 * Adds a label
	 * 
	 * @param tab
	 * @param label
	 * @return
	 */
	public PreferenceWindowBuilder addLabel(PWTab tab, String label) {
		tab.add(new PWLabel(label));
		return this;
	}

	/**
	 * Adds a label and a text box for string
	 * 
	 * @param tab
	 * @param label
	 * @param value
	 * @return
	 */
	public PreferenceWindowBuilder addStringBox(PWTab tab, String label, String value) {
		tab.add(new PWStringText(label, value).setAlignment(GridData.FILL));
		return this;
	}

	/**
	 * Adds a label and a text box for integer
	 * 
	 * @param tab
	 * @param label
	 * @param value
	 * @return
	 */
	public PreferenceWindowBuilder addIntegerBox(PWTab tab, String label, String value) {
		tab.add(new CostumPWIntegerText(label, value).setAlignment(GridData.FILL));
		return this;
	}

	/**
	 * Adds a label and a text box for float
	 * 
	 * @param tab
	 * @param label
	 * @param value
	 * @return
	 */
	public PreferenceWindowBuilder addFloatBox(PWTab tab, String label, String value) {
		tab.add(new CostumPWFloatText(label, value).setAlignment(GridData.FILL));
		return this;
	}

	/**
	 * Adds a label and a text box for url
	 * 
	 * @param tab
	 * @param label
	 * @param value
	 * @return
	 */
	public PreferenceWindowBuilder addURLBox(PWTab tab, String label, String value) {
		tab.add(new PWURLText(label, value).setAlignment(GridData.FILL));
		return this;
	}

	/**
	 * Adds a label and a text box for password
	 * 
	 * @param tab
	 * @param label
	 * @param value
	 * @return
	 */
	public PreferenceWindowBuilder addPasswordBox(PWTab tab, String label, String value) {
		tab.add(new PWPasswordText(label, value).setAlignment(GridData.FILL));
		return this;
	}

	/**
	 * Adds a label and a textarea(read-only) and a directory chooser
	 * 
	 * @param tab
	 * @param label
	 * @param value
	 * @return
	 */
	public PreferenceWindowBuilder addDirectoryChooser(PWTab tab, String label, String value) {
		tab.add(new PWDirectoryChooser(label, value).setAlignment(GridData.FILL));
		return this;
	}

	/**
	 * Adds a label and a textarea(read-only) and a directory chooser
	 * 
	 * @param tab
	 * @param label
	 * @param value
	 * @return
	 */
	public PreferenceWindowBuilder addFileChooser(PWTab tab, String label, String value) {
		tab.add(new PWFileChooser(label, value).setAlignment(GridData.FILL));
		return this;
	}

	/**
	 * Add a Label with Textarea
	 * 
	 * @param tab
	 * @param label
	 * @param value
	 * @return
	 */
	public PreferenceWindowBuilder addTextarea(PWTab tab, String label, String value) {
		tab.add(new PWTextarea(label, value).setAlignment(GridData.FILL));
		return this;
	}

	/**
	 * Adds separator
	 * 
	 * @param tab
	 * @return
	 */
	public PreferenceWindowBuilder addSeparator(PWTab tab) {
		tab.add(new PWSeparator());
		return this;
	}

	/**
	 * Adds a titled separator
	 * 
	 * @param tab
	 * @return
	 */
	public PreferenceWindowBuilder addTitledSeparator(PWTab tab, String label) {
		tab.add(new PWSeparator(label));
		return this;
	}

	/**
	 * Adds a label and a read-only combo box
	 * 
	 * @param tab
	 * @param label
	 * @param value
	 * @return
	 */
	public PreferenceWindowBuilder addComboBoxRO(PWTab tab, String label, String value, Object... values) {
		tab.add(new PWCombo(label, value, values).setAlignment(GridData.FILL));
		return this;
	}

	/**
	 * Adds a label and a editable combo box
	 * 
	 * @param tab
	 * @param label
	 * @param value
	 * @return
	 */
	public PreferenceWindowBuilder addComboBoxEA(PWTab tab, String label, String value, Object... values) {
		tab.add(new PWCombo(label, value, true, values).setAlignment(GridData.FILL));
		return this;
	}

	/**
	 * Adds a Button
	 * 
	 * @param tab
	 * @param label
	 * @param selectionListener
	 * @return
	 */
	public PreferenceWindowBuilder addButton(PWTab tab, String label, SelectionListener selectionListener) {
		tab.add(new PWButton(label, selectionListener).setAlignment(GridData.END).setGrabExcessSpace(true));
		return this;
	}

	/**
	 * Adds a checkbox
	 * 
	 * @param tab
	 * @param label
	 * @param value
	 * @return
	 */
	public PreferenceWindowBuilder addCheckbox(PWTab tab, String label, String value) {
		tab.add(new PWCheckbox(label, value).setAlignment(GridData.FILL).setIndent(40));
		return this;
	}
	
	public PreferenceWindowBuilder addTwoIntegerRow(PWTab tab, String label1, String value1, String label2, String value2) {
		tab.add(new PWRow().add(new CostumPWIntegerText(label1, value1)).add(new CostumPWIntegerText(label2, value2)));
		return this;
	}

}
