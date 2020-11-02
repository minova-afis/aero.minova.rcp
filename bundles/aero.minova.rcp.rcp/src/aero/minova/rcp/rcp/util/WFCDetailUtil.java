package aero.minova.rcp.rcp.util;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.rcp.widgets.LookupControl;

public class WFCDetailUtil {

	@Inject
	protected UISynchronize sync;

	@Inject
	private IEventBroker broker;

	@Inject
	private TranslationService translationService;

	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	Shell shell;

	@Inject
	@Preference(nodePath = "aero.minova.rcp.preferencewindow", value = "timezone")
	String timezone;

	@Inject
	protected IDataService dataService;

	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private Composite parent;

	private Map<String, Control> controls = new HashMap<>();
	private List<ArrayList> keys = null;
	private Table selectedTable;
	@Inject
	private Form form;
	private WFCDetailsLookupUtil lookupUtil = null;

	private WFCDetailCASRequestsUtil casRequests = null;

	@Inject
	public WFCDetailUtil() {
	}

	public void bindValues(Map<String, Control> controls) {
		this.controls = controls;

		this.lookupUtil = new WFCDetailsLookupUtil(controls);

		for (Control c : controls.values()) {
			// Automatische anpassung der Quantitys, sobald sich die Zeiteinträge verändern
			if ((c.getData(Constants.CONTROL_FIELD) == controls.get(Constants.FORM_STARTDATE)
					.getData(Constants.CONTROL_FIELD))
					|| (c.getData(Constants.CONTROL_FIELD) == controls.get(Constants.FORM_ENDDATE)
							.getData(Constants.CONTROL_FIELD))) {
				c.addKeyListener(new KeyListener() {

					@Override
					public void keyPressed(KeyEvent e) {
					}

					@Override
					public void keyReleased(KeyEvent e) {
						updateQuantitys();
					}

				});
			}
			// Hinzufügen der Listener, um die Verification der Werte zu gewährleisten
			if (c instanceof Text) {
				TextfieldVerifier tfv = new TextfieldVerifier();
				Text text = (Text) c;
				Field field = (Field) c.getData(Constants.CONTROL_FIELD);
				if (field.getNumber() != null) {
					text.addVerifyListener(e -> {
						if (e.character != '\b') {
							final String oldString = ((Text) e.getSource()).getText();
							String newString = oldString.substring(0, e.start) + e.text + oldString.substring(e.end);
							e.doit = tfv.verifyDouble(newString);
						}
					});
				}
				if (field.getShortDate() != null || field.getLongDate() != null || field.getDateTime() != null
						|| field.getShortTime() != null) {
					text.setData(Constants.FOCUSED_ORIGIN, this);
					text.addFocusListener(tfv);
				}
				if (field.getText() != null) {
					text.addVerifyListener(e -> {
						if (e.character != '\b') {
							final String oldString = ((Text) e.getSource()).getText();
							String newString = oldString.substring(0, e.start) + e.text + oldString.substring(e.end);
							e.doit = tfv.verifyText(newString, field.getText().getLength());
						}
					});
				}
			}
			if (c instanceof LookupControl) {
				LookupFieldFocusListener lfl = new LookupFieldFocusListener(broker, dataService);
				LookupControl lc = (LookupControl) c;
				lc.addFocusListener(lfl);
				// Timer timer = new Timer();
				// Hinzufügen von Keylistenern, sodass die Felder bei Eingaben
				// ihre Optionen auflisten können und ihren Wert bei einem Treffer übernehmen
				lc.addKeyListener(new KeyListener() {

					@Override
					public void keyPressed(KeyEvent e) {
						// TODO Auto-generated method stub

					}

					@Override
					public void keyReleased(KeyEvent e) {
						// PFeiltastenangaben, Enter und TAB sollen nicht den Suchprozess auslösen
						if (e.keyCode != SWT.ARROW_DOWN && e.keyCode != SWT.ARROW_LEFT && e.keyCode != SWT.ARROW_RIGHT
								&& e.keyCode != SWT.ARROW_UP && e.keyCode != SWT.TAB && e.keyCode != SWT.CR) {
							if (lc.getData(Constants.CONTROL_OPTIONS) == null || lc.getText().equals("")) {
								lookupUtil.requestOptionsFromCAS(lc);
							} else {
								lookupUtil.changeSelectionBoxList(lc, false);
							}
							// Wird die untere Pfeiltaste eingeben, so sollen sämtliche Optionen,
							// wie auch bei einem Klick auf das Twiste, angezeigt werden
							// PROBLEM: durch die Optionen wechseln via pfeiltasten so nicht möglich
						} else if (e.keyCode == SWT.ARROW_DOWN && lc.getData(Constants.CONTROL_OPTIONS) == null) {
							Field field = (Field) lc.getData(Constants.CONTROL_FIELD);
							broker.post("LoadAllLookUpValues", field.getName());
						}
					}

				});
			}
		}
	}

	/**
	 * Aktuellisiert die Quantityvalues, sobald sich einer der beiden Zeiteinträge
	 * verändert
	 */
	public void updateQuantitys() {
		Text endDate = (Text) controls.get(Constants.FORM_ENDDATE);
		Text startDate = (Text) controls.get(Constants.FORM_STARTDATE);
		if (endDate.getText().matches("..:..") && startDate.getText().matches("..:..")) {
			LocalTime timeEndDate = LocalTime.parse(endDate.getText());
			LocalTime timeStartDate = LocalTime.parse(startDate.getText());
			float timeDifference = ((timeEndDate.getHour() * 60) + timeEndDate.getMinute())
					- ((timeStartDate.getHour() * 60) + timeStartDate.getMinute());
			timeDifference = timeDifference / 60;
			Text renderedField = (Text) controls.get(Constants.FORM_RENDEREDQUANTITY);
			Text chargedField = (Text) controls.get(Constants.FORM_CHARGEDQUANTITY);
			String renderedValue;
			String chargedValue;
			if (timeDifference >= 0) {
				Double quarter = (double) Math.round(timeDifference * 4) / 4f;
				Double half = (double) Math.round(timeDifference * 2) / 2f;
				chargedValue = String.format("%1.2f", half);
				chargedValue = chargedValue.replace(',', '.');
				renderedValue = String.format("%1.2f", quarter);
				renderedValue = renderedValue.replace(',', '.');
			} else {
				renderedValue = "0";
				chargedValue = "0";
			}
			chargedField.setText(chargedValue);
			renderedField.setText(renderedValue);
		}
	}

	public String getTimeZone() {
		return timezone;
	}
}
