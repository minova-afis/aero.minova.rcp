package aero.minova.rcp.rcp.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.nebula.widgets.opal.dialog.Dialog;
import org.eclipse.nebula.widgets.opal.preferencewindow.PWTab;
import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWButton;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWCheckbox;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWColorChooser;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWCombo;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWDirectoryChooser;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWFileChooser;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWFloatText;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWFontChooser;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWIntegerText;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWLabel;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWPasswordText;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWScale;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWSeparator;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWSpinner;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWStringText;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWTextarea;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWURLText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;

public class PreferenceWindowExample {
	
	@Execute
	public void execute() {
		
		openPreferenceWindow();
		
	}
	
	public void openPreferenceWindow() {
		final PreferenceWindow window = PreferenceWindow.create(fillData());
		
		createDocumentTab(window);
		createInfoTab(window);
		
		window.open();
	}

	public Map<String, Object> fillData() {

		final Map<String, Object> data = new HashMap<String, Object>();
		data.put("text", "A String");
		data.put("int", Integer.valueOf(42));
		data.put("float", Float.valueOf((float) 3.14));
		data.put("url", "http://www.google.de/");
		data.put("password", "password");
		data.put("directory", "");
		data.put("file", "");
		data.put("textarea", "long long/nlong long/nlong long/ntext...");
		data.put("comboReadOnly", "Value 1");
		data.put("combo", "Other Value");

		return data;

	}
	
	protected static void createDocumentTab(final PreferenceWindow window) {
		final PWTab documentTab = window.addTab(null, "Document");

		documentTab.add(new PWLabel("Let's start with Text, Separator, Combo and button")).//
				add(new PWStringText("String :", "text").setAlignment(GridData.FILL)).//
				add(new PWIntegerText("Integer :", "int"));
		documentTab.add(new PWFloatText("Float :", "float"));
		documentTab.add(new PWURLText("URL :", "url"));
		documentTab.add(new PWPasswordText("Password :", "password"));
		documentTab.add(new PWDirectoryChooser("Directory :", "directory"));
		documentTab.add(new PWFileChooser("File :", "file"));
		documentTab.add(new PWTextarea("Textarea :", "textarea"));

		documentTab.add(new PWSeparator());

		documentTab.add(new PWCombo("Combo (read-only):", "comboReadOnly", "Value 1", "Value 2", "Value 3"));
		documentTab.add(new PWCombo("Combo (editable):", "combo", true, new Object[] { "Value 1", "Value 2", "Value 3" }));

		documentTab.add(new PWSeparator("Titled separator"));
		documentTab.add(new PWButton("First button", new SelectionAdapter() {

			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(final SelectionEvent e) {
				Dialog.inform("Hi", "You pressed the first button");
			}

		}).setAlignment(GridData.END));
	}
	

	
	protected static void createInfoTab(final PreferenceWindow window) {
		final PWTab infoTab = window.addTab(null, "Info");

		infoTab.add(new PWLabel("Checkboxes, Slider,Spinner, Color chooser, Font chooser"));
		infoTab.add(new PWCheckbox("Checkbox 1", "cb1"));
		infoTab.add(new PWCheckbox("Checkbox 2", "cb2"));

		infoTab.add(new PWSeparator());

		infoTab.add(new PWScale("Slider : ", "slider", 0, 100, 10));
		infoTab.add(new PWSpinner("Spinner :", "spinner", 0, 100));

		infoTab.add(new PWSeparator());

		infoTab.add(new PWColorChooser("Color :", "color"));
		infoTab.add(new PWFontChooser("Font :", "font"));

	}
}
