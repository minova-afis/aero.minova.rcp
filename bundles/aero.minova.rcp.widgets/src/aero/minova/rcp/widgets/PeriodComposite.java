package aero.minova.rcp.widgets;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.nebula.widgets.opal.textassist.TextAssist;
import org.eclipse.nebula.widgets.opal.textassist.TextAssistContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolTip;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.PeriodValue;
import aero.minova.rcp.model.form.MPeriodField;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.util.DateUtil;
import aero.minova.rcp.util.OSUtil;

public class PeriodComposite extends Composite {

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.DATE_UTIL)
	String dateUtil;

	@Inject
	private TranslationService translationService;

	Control baseDateControl;
	Control dueDateControl;
	String userInput;
	Instant baseDate;
	Instant dueDate;

	private MPeriodField field;

	private Locale locale;

	public PeriodComposite(Composite parent, MPeriodField field, Locale locale) {
		super(parent, SWT.NONE);
		this.field = field;
		this.locale = locale;
		this.setData(Constants.TRANSLATE_LOCALE, locale);

		// Ausgangsdatum
		TextAssistContentProvider baseContentProvider = new TextAssistContentProvider() {
			@Override
			public List<String> getContent(String entry) {
				ArrayList<String> result = new ArrayList<>();
				setBaseDate(DateUtil.getDate(entry, locale, dateUtil));
				calculateDueDate();
				if (baseDate == null) {
					result.add(translationService.translate("@msg.ErrorConverting", null));
				} else {
					result.add(DateUtil.getDateString(baseDate, locale, dateUtil));
					field.setValue(new PeriodValue(baseDate, userInput, dueDate), true);
				}
				return result;
			}

		};

		baseDateControl = createControl(parent, baseContentProvider, false);

		// Intervall / Fälligkeitsdatum
		TextAssistContentProvider dueContentProvider = new TextAssistContentProvider() {

			@Override
			public List<String> getContent(String entry) {
				userInput = entry;
				ArrayList<String> result = new ArrayList<>();
				calculateDueDate();
				if (dueDate == null) {
					result.add(translationService.translate("@msg.ErrorConverting", null));
					userInput = null;
				} else {
					result.add(DateUtil.getDateString(dueDate, locale, dateUtil));
					field.setValue(new PeriodValue(baseDate, userInput, dueDate), true);
				}
				return result;
			}
		};

		dueDateControl = createControl(parent, dueContentProvider, true);

	}

	private void calculateDueDate() {
		if (userInput == null) {
			return;
		}
		if (baseDate == null) {
			setDueDate(DateUtil.getDate(userInput, locale, dateUtil));
		} else {
			setDueDate(DateUtil.getDate(baseDate, userInput, locale, dateUtil));
		}
	}

	private Control createControl(Composite composite, TextAssistContentProvider contentProvider, boolean dueField) {
		Control text;
		LocalDateTime time = LocalDateTime.of(LocalDate.of(2000, 01, 01), LocalTime.of(11, 59));

		if (OSUtil.isLinux()) {
			text = createLinuxControl(composite, contentProvider, dueField, time);
		} else {
			text = createTextAssistControl(composite, contentProvider, dueField, time);
		}

		text.setData(Constants.CONTROL_FIELD, field);
		text.setData(Constants.TRANSLATE_LOCALE, locale);
		return text;
	}

	private TextAssist createTextAssistControl(Composite composite, TextAssistContentProvider contentProvider, boolean dueField, LocalDateTime time) {
		TextAssist text = new TextAssist(composite, SWT.BORDER, contentProvider);
		text.setMessage(DateUtil.getDateString(time.toInstant(ZoneOffset.UTC), locale, dateUtil));
		text.setNumberOfLines(1);
		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (dueField && userInput != null) {
					text.setText(userInput);
				}
				text.selectAll();
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (text.getText().isBlank()) {
					if (dueField) {
						userInput = null;
						setDueDate(null);
					} else {
						setBaseDate(null);
						calculateDueDate();
					}
					field.setValue(new PeriodValue(baseDate, userInput, dueDate), true);
				}
			}
		});
		return text;
	}

	// TODO: Diese Methode ist noch nicht getestet
	private Text createLinuxControl(Composite composite, TextAssistContentProvider contentProvider, boolean dueField, LocalDateTime time) {
		Text text = new Text(composite, SWT.BORDER);
		ToolTip tooltip = new ToolTip(text.getShell(), SWT.ICON_INFORMATION);
		text.setMessage(DateUtil.getDateString(time.toInstant(ZoneOffset.UTC), locale, dateUtil));
		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				tooltip.setAutoHide(false);
				if (dueField && userInput != null) {
					text.setText(userInput);
				}
				text.selectAll();
			}

			@Override
			public void focusLost(FocusEvent e) {
				tooltip.setAutoHide(true);
			}
		});
		text.addModifyListener(e -> {
			try {
				if (!tooltip.getAutoHide()) {
					List<String> values = contentProvider.getContent(((Text) e.widget).getText());
					if (!values.isEmpty()) {
						tooltip.setText(values.get(0));
						tooltip.setVisible(true);
					}
				} else {
					tooltip.setText("");
				}
			} catch (NullPointerException ex) {}
		});
		return text;
	}

	public Control getBaseDate() {
		return baseDateControl;
	}

	public void setBaseDate(Instant baseDate) {
		this.baseDate = baseDate;
	}

	public Control getDueDate() {
		return dueDateControl;
	}

	public void setDueDate(Instant dueDate) {
		this.dueDate = dueDate;
	}

	public String getUserInput() {
		return userInput;
	}

	public void setUserInput(String userInput) {
		this.userInput = userInput;
	}

	@Override
	public void setToolTipText(String tooltip) {
		if (baseDateControl instanceof TextAssist ta) {
			ta.getChildren()[0].setToolTipText(tooltip);
		} else {
			baseDateControl.setToolTipText(tooltip);
		}

		if (dueDateControl instanceof TextAssist ta) {
			ta.getChildren()[0].setToolTipText(tooltip);
		} else {
			dueDateControl.setToolTipText(tooltip);
		}
	}

}
