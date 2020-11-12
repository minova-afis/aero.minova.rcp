package aero.minova.rcp.rcp.util;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.rcp.widgets.LookupControl;

public class WFCDetailUtil {

	@Inject
	protected UISynchronize sync;

	@Inject
	private IEventBroker broker;

	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	Shell shell;

	@Inject
	@Preference(nodePath = "aero.minova.rcp.preferencewindow", value = "timezone")
	String timezone;

	@Inject
	protected IDataService dataService;

	private Map<String, Control> controls = new HashMap<>();

	private WFCDetailsLookupUtil lookupUtil = null;

	@Inject
	public WFCDetailUtil() {
	}

	public void bindValues(Map<String, Control> controls, MPerspective perspective) {
		this.controls = controls;

		this.lookupUtil = new WFCDetailsLookupUtil(controls, perspective, dataService, sync);

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
					text.addFocusListener(tfv);
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
				LookupFieldFocusListener lfl = new LookupFieldFocusListener(broker, dataService, sync);
				LookupControl lc = (LookupControl) c;
				lc.addFocusListener(lfl);
				// Timer timer = new Timer();
				// Hinzufügen von Keylistenern, sodass die Felder bei Eingaben
				// ihre Optionen auflisten können und ihren Wert bei einem Treffer übernehmen
				lc.addKeyListener(new KeyListener() {
					boolean controlPressed = false;

					@Override
					public void keyPressed(KeyEvent e) {
						// TODO Auto-generated method stub
						if (e.keyCode == SWT.CONTROL) {
							controlPressed = true;
						}
					}

					@Override
					public void keyReleased(KeyEvent e) {
						if (e.keyCode == SWT.CONTROL) {
							controlPressed = false;
						} else
						// PFeiltastenangaben, Enter und TAB sollen nicht den Suchprozess auslösen
						if (e.keyCode != SWT.ARROW_DOWN && e.keyCode != SWT.ARROW_LEFT && e.keyCode != SWT.ARROW_RIGHT
								&& e.keyCode != SWT.ARROW_UP && e.keyCode != SWT.TAB && e.keyCode != SWT.CR
								&& e.keyCode != SWT.SPACE) {
							if (lc.getData(Constants.CONTROL_OPTIONS) == null || lc.getText().equals("")) {
								lookupUtil.requestOptionsFromCAS(lc);
							} else {
								lookupUtil.changeSelectionBoxList(lc, false);
							}
						} else if (e.keyCode == SWT.SPACE && controlPressed == true) {
							Field field = (Field) lc.getData(Constants.CONTROL_FIELD);
							Map<MPerspective, String> brokerObject = new HashMap<>();
							brokerObject.put(perspective, field.getName());
							broker.post("WFCLoadAllLookUpValues", brokerObject);
						} else if (e.keyCode == SWT.ARROW_DOWN && lc.isProposalPopupOpen() == false) {
							if (lc.getData(Constants.CONTROL_OPTIONS) != null) {
								lookupUtil.changeSelectionBoxList(c, false);
							} else {
								Field field = (Field) lc.getData(Constants.CONTROL_FIELD);
								Map<MPerspective, String> brokerObject = new HashMap<>();
								brokerObject.put(perspective, field.getName());
								broker.post("WFCLoadAllLookUpValues", brokerObject);
							}
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
				String chargedFormat = "%1."
						+ ((Field) chargedField.getData(Constants.CONTROL_FIELD)).getNumber().getDecimals() + "f";
				String renderedFormat = "%1."
						+ ((Field) renderedField.getData(Constants.CONTROL_FIELD)).getNumber().getDecimals() + "f";
				chargedValue = String.format(chargedFormat, half);
				chargedValue = chargedValue.replace(',', '.');
				renderedValue = String.format(renderedFormat, quarter);
				renderedValue = renderedValue.replace(',', '.');
			} else {
				renderedValue = "0.00";
				chargedValue = "0.00";
			}
			chargedField.setText(chargedValue);
			renderedField.setText(renderedValue);
		}
	}

	public String getTimeZone() {
		return timezone;
	}

	public WFCDetailsLookupUtil getLookupUtil() {
		return lookupUtil;
	}

	public void setLookupUtil(WFCDetailsLookupUtil lookupUtil) {
		this.lookupUtil = lookupUtil;
	}

}
