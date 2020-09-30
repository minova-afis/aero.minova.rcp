package aero.minova.rcp.preferencewindow.builder;

import org.eclipse.nebula.widgets.opal.preferencewindow.PWTab;
import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWButton;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWCheckbox;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWCombo;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWDirectoryChooser;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWFileChooser;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWFontChooser;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWLabel;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWPasswordText;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWSeparator;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWTextarea;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWURLText;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;

import aero.minova.rcp.preferencewindow.control.CustomPWFloatText;
import aero.minova.rcp.preferencewindow.control.CustomPWIntegerText;
import aero.minova.rcp.preferencewindow.control.CustomPWStringText;

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
		tab.add(new CustomPWStringText(label, value).setAlignment(GridData.FILL).setIndent(25));
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
		tab.add(new CustomPWIntegerText(label, value).setIndent(25));
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
		tab.add(new CustomPWFloatText(label, value));
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
		tab.add(new PWURLText(label, value));
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
		tab.add(new PWPasswordText(label, value));
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
		tab.add(new PWDirectoryChooser(label, value).setIndent(25));
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
		tab.add(new PWFileChooser(label, value).setIndent(25));
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
		tab.add(new PWTextarea(label, value));
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
		tab.add(new PWCombo(label, value, values));
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
		tab.add(new PWCombo(label, value, true, values));
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
		tab.add(new PWButton(label, selectionListener));
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
		tab.add(new PWCheckbox(label, value).setAlignment(GridData.FILL).setIndent(25));
		return this;
	}
	
	/**
	 * Adds a fontchooser widget
	 * 
	 * @param tab
	 * @param label
	 * @param value
	 * @return
	 */
	public PreferenceWindowBuilder addFontChooser(PWTab tab, String label, String value) {
		tab.add(new PWFontChooser(label, value));
		return this;
	}

}
