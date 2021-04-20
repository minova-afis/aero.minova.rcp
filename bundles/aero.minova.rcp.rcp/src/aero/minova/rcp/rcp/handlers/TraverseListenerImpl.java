package aero.minova.rcp.rcp.handlers;

import java.util.List;
import java.util.Locale;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.nebula.widgets.opal.textassist.TextAssist;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Control;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MPage;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.preferencewindow.builder.DisplayType;
import aero.minova.rcp.preferencewindow.builder.InstancePreferenceAccessor;
import aero.minova.rcp.rcp.accessor.AbstractValueAccessor;
import aero.minova.rcp.rcp.widgets.Lookup;

public class TraverseListenerImpl implements TraverseListener {

	Logger logger;
	MDetail detail;
	Locale locale;
	EPartService partService;
	IEclipseContext context;

	public TraverseListenerImpl(Logger logger, MDetail detail, Locale locale, EPartService partService, IEclipseContext context) {
		this.logger = logger;
		this.detail = detail;
		this.locale = locale;
		this.partService = partService;
		this.context = context;
	}

	@Override
	public void keyTraversed(TraverseEvent e) {

		logger.info(
				"keyTraversed(detail=" + e.detail + ", stateMask=" + Integer.toHexString(e.stateMask) + ", keyCode= " + Integer.toHexString(e.keyCode) + ")");
		List<MPage> pageList;
		// if (!e.doit) return; // wir tun nichts, wenn ein anderer etwas getan hat

		Control focussedControl = (Control) e.widget;
		if (focussedControl.getParent() instanceof Lookup) {
			focussedControl = focussedControl.getParent();
		} else if (focussedControl.getParent() instanceof TextAssist) {
			focussedControl = focussedControl.getParent();
		}

		switch (e.detail) {
		case SWT.TRAVERSE_ARROW_NEXT:
			logger.info("SWT.TRAVERSE_ARROW_NEXT");
			break;
		case SWT.TRAVERSE_ARROW_PREVIOUS:
			logger.info("SWT.TRAVERSE_ARROW_PREVIOUS");
			break;
		case SWT.TRAVERSE_ESCAPE:
			logger.info("SWT.TRAVERSE_ESCAPE");
			break;
		case SWT.TRAVERSE_MNEMONIC:
			logger.info("SWT.TRAVERSE_MNEMONIC");
			break;
		case SWT.TRAVERSE_NONE:
			logger.info("SWT.TRAVERSE_NONE");
			break;
		case SWT.TRAVERSE_PAGE_NEXT:
			logger.info("SWT.TRAVERSE_PAGE_NEXT");
			break;
		case SWT.TRAVERSE_PAGE_PREVIOUS:
			logger.info("SWT.TRAVERSE_PAGE_PREVIOUS");
			break;
		case SWT.TRAVERSE_RETURN:
			logger.info("SWT.TRAVERSE_RETURN");
			getNextRequired(focussedControl);
			e.doit = false;
			break;
		case SWT.TRAVERSE_TAB_NEXT:
			logger.info("SWT.TRAVERSE_TAB_NEXT");
			if (e.keyCode == SWT.TAB) {
				getNextField(focussedControl);
			} else if (e.keyCode == SWT.CR) {
				getNextRequired(focussedControl);
			}
			e.doit = false;
			break;
		case SWT.TRAVERSE_TAB_PREVIOUS:
			logger.info("SWT.TRAVERSE_TAB_PREVIOUS");
			getPreviousField(focussedControl);
			e.doit = false;
			break;
		default:
			logger.info("UNKNOWN");
		}

	}

	private void getPreviousField(Control focussedControl) {
		Preferences preferences = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE);
		boolean selectAllControls = (boolean) InstancePreferenceAccessor.getValue(preferences, ApplicationPreferences.SELECT_ALL_CONTROLS, DisplayType.CHECK,
				true, locale);

		List<MPage> pageList = detail.getPageList();
		for (MPage page : pageList) {
			List<MField> tabList = page.getTabList();

			if (context.get("PreviousField") instanceof MField && ((MField) context.get("PreviousField")).getmPage().equals(page)) {
				if (focussedControl.equals(((AbstractValueAccessor) ((MField) context.get("PreviousField")).getValueAccessor()).getControl())) {
					MField field = (MField) context.get("PreviousField");
					System.out.println(context.get("PreviousField"));
					if (tabList.indexOf(field) == 0) {
						// Die Preference SelectAllControls ist gesetzt.
						if (selectAllControls == true) {
							// Wir sind in der ersten Section
							if (pageList.indexOf(page) == 0) {
								MToolBar toolbarElements = partService.getActivePart().getToolbar();
								focussedControl = (Control) toolbarElements.getWidget();
								context.set("PreviousField", (Control) toolbarElements.getWidget());
								// Wir prüfen nach dem Twistie der Section
							} else if (null != pageList.get(pageList.indexOf(page)).getSection()) {
								focussedControl = pageList.get(pageList.indexOf(page)).getSection();
								context.set("PreviousField", pageList.get(pageList.indexOf(page)).getSection());
							} else {
								List<MField> previousTabList = pageList.get(pageList.indexOf(page) - 1).getTabList();
								focussedControl = ((AbstractValueAccessor) previousTabList.get(previousTabList.size() - 1).getValueAccessor()).getControl();
								context.set("PreviousField", previousTabList.get(previousTabList.size() - 1));
							}
							// Wir sind in der ersten Section
						} else if (pageList.indexOf(page) == 0) {
							List<MField> lastTabList = pageList.get(pageList.size() - 1).getTabList();
							focussedControl = ((AbstractValueAccessor) lastTabList.get(lastTabList.size() - 1).getValueAccessor()).getControl();
							context.set("PreviousField", lastTabList.get(lastTabList.size() - 1));
						} else {
							List<MField> previousTabList = pageList.get(pageList.indexOf(page) - 1).getTabList();
							focussedControl = ((AbstractValueAccessor) previousTabList.get(previousTabList.size() - 1).getValueAccessor()).getControl();
							context.set("PreviousField", previousTabList.get(previousTabList.size() - 1));
						}
					} else {
						focussedControl = ((AbstractValueAccessor) tabList.get(tabList.indexOf(field) - 1).getValueAccessor()).getControl();
						context.set("PreviousField", tabList.get(tabList.indexOf(field) - 1));
					}
					focussedControl.setFocus();
					System.out.println(context.get("PreviousField"));
					return;
				}
			}

			for (MField f : tabList) {
				if (((AbstractValueAccessor) f.getValueAccessor()).getControl() == focussedControl) {
					// Wir sind an der ersten Stelle der Section
					if (tabList.indexOf(f) == 0) {
						// Die Preference SelectAllControls ist gesetzt.
						if (selectAllControls == true) {
							// Wir sind in der ersten Section
							if (pageList.indexOf(page) == 0) {
								MToolBar toolbarElements = partService.getActivePart().getToolbar();
								focussedControl = (Control) toolbarElements.getWidget();
								context.set("PreviousField", (Control) toolbarElements.getWidget());
								// Wir prüfen nach dem Twistie der Section
							} else if (null != pageList.get(pageList.indexOf(page)).getSection()) {
								focussedControl = pageList.get(pageList.indexOf(page)).getSection();
								context.set("PreviousField", pageList.get(pageList.indexOf(page)).getSection());
							} else {
								List<MField> previousTabList = pageList.get(pageList.indexOf(page) - 1).getTabList();
								focussedControl = ((AbstractValueAccessor) previousTabList.get(previousTabList.size() - 1).getValueAccessor()).getControl();
								context.set("PreviousField", previousTabList.get(previousTabList.size() - 1));
							}
							// Wir sind in der ersten Section
						} else if (pageList.indexOf(page) == 0) {
							List<MField> lastTabList = pageList.get(pageList.size() - 1).getTabList();
							focussedControl = ((AbstractValueAccessor) lastTabList.get(lastTabList.size() - 1).getValueAccessor()).getControl();
							context.set("PreviousField", lastTabList.get(lastTabList.size() - 1));
						} else {
							List<MField> previousTabList = pageList.get(pageList.indexOf(page) - 1).getTabList();
							focussedControl = ((AbstractValueAccessor) previousTabList.get(previousTabList.size() - 1).getValueAccessor()).getControl();
							context.set("PreviousField", previousTabList.get(previousTabList.size() - 1));
						}
					} else {
						focussedControl = ((AbstractValueAccessor) tabList.get(tabList.indexOf(f) - 1).getValueAccessor()).getControl();
						context.set("PreviousField", tabList.get(tabList.indexOf(f) - 1));
					}
					focussedControl.setFocus();
					return;
				}
			}
		}

	}

	private void getNextField(Control focussedControl) {
		Preferences preferences = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE);
		boolean selectAllControls = (boolean) InstancePreferenceAccessor.getValue(preferences, ApplicationPreferences.SELECT_ALL_CONTROLS, DisplayType.CHECK,
				true, locale);

		List<MPage> pageList = detail.getPageList();
		for (MPage page : pageList) {
			List<MField> tabList = page.getTabList();

			if (context.get("PreviousField") instanceof MField && ((MField) context.get("PreviousField")).getmPage().equals(page)) {
				// Wir prüfen, ob das ausgewählte Feld als vorheriges Feld gespeichert wurde oder ob ein vorheriges Feld gesetzt ist.
				if (focussedControl.equals(((AbstractValueAccessor) ((MField) context.get("PreviousField")).getValueAccessor()).getControl())) {
					// Wir holen uns das vorherige Feld aus dem Context.
					MField field = (MField) context.get("PreviousField");
					// Wir sind im letzten Feld der Page.
					if (tabList.indexOf(field) == tabList.size() - 1) {
						// Die Preference SelectAllControls is gesetzt.
						if (selectAllControls == true) {
							// Wir sind in der Letzten Section
							if (pageList.indexOf(page) == pageList.size() - 1) {
								MToolBar toolbarElements = partService.getActivePart().getToolbar();
								focussedControl = (Control) toolbarElements.getWidget();
								context.set("PreviousField", (Control) toolbarElements.getWidget());
								// Wir prüfen nach dem Twistie der Section
							} else if (null != pageList.get(pageList.indexOf(page) + 1).getSection()) {
								focussedControl = pageList.get(pageList.indexOf(page) + 1).getSection();
								context.set("PreviousField", pageList.get(pageList.indexOf(page) + 1).getSection());
							} else {
								focussedControl = ((AbstractValueAccessor) pageList.get(pageList.indexOf(page) + 1).getTabList().get(0).getValueAccessor())
										.getControl();
								context.set("PreviousField", pageList.get(pageList.indexOf(page) + 1).getTabList().get(0));
							}
							// Wir sind in der Letzten Section
						} else if (pageList.indexOf(page) == pageList.size() - 1) {
							focussedControl = ((AbstractValueAccessor) pageList.get(0).getTabList().get(0).getValueAccessor()).getControl();
							context.set("PreviousField", pageList.get(0).getTabList().get(0));
						} else {
							focussedControl = ((AbstractValueAccessor) pageList.get(pageList.indexOf(page) + 1).getTabList().get(0).getValueAccessor())
									.getControl();
							context.set("PreviousField", pageList.get(pageList.indexOf(page) + 1).getTabList().get(0));
						}
					} else {
						focussedControl = ((AbstractValueAccessor) tabList.get(tabList.indexOf(field) + 1).getValueAccessor()).getControl();
						context.set("PreviousField", tabList.get(tabList.indexOf(field) + 1));
					}
					focussedControl.setFocus();
					return;
				}
			}

			for (MField f : tabList) {
				if (((AbstractValueAccessor) f.getValueAccessor()).getControl() == focussedControl) {
					if (tabList.indexOf(f) == tabList.size() - 1) {
						// Wir sind an der Letzen Stelle der Section
						if (selectAllControls == true) {
							// Wir sind in der Letzten Section
							if (pageList.indexOf(page) == pageList.size() - 1) {
								MToolBar toolbarElements = partService.getActivePart().getToolbar();
								focussedControl = (Control) toolbarElements.getWidget();
								context.set("PreviousField", toolbarElements.getWidget());
							} else if (null != pageList.get(pageList.indexOf(page) + 1).getSection()) {
								focussedControl = pageList.get(pageList.indexOf(page) + 1).getSection();
								context.set("PreviousField", pageList.get(pageList.indexOf(page) + 1).getSection());

							} else {
								focussedControl = ((AbstractValueAccessor) pageList.get(pageList.indexOf(page) + 1).getTabList().get(0).getValueAccessor())
										.getControl();
								context.set("PreviousField", pageList.get(pageList.indexOf(page) + 1).getTabList().get(0));
							}
							// Wir sind in der Letzten Section
						} else if (pageList.indexOf(page) == pageList.size() - 1) {
							focussedControl = ((AbstractValueAccessor) pageList.get(0).getTabList().get(0).getValueAccessor()).getControl();
							context.set("PreviousField", pageList.get(0).getTabList().get(0));
						} else {
							focussedControl = ((AbstractValueAccessor) pageList.get(pageList.indexOf(page) + 1).getTabList().get(0).getValueAccessor())
									.getControl();
							context.set("PreviousField", pageList.get(pageList.indexOf(page) + 1).getTabList().get(0));
						}
					} else {
						focussedControl = ((AbstractValueAccessor) tabList.get(tabList.indexOf(f) + 1).getValueAccessor()).getControl();
						context.set("PreviousField", tabList.get(tabList.indexOf(f) + 1));
					}
					focussedControl.setFocus();
					return;
				}
			}
		}

	}

	private void getNextRequired(Control focussedControl) {

		Preferences preferences = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE);
		boolean lookupEnterSelectsNextRequired = (boolean) InstancePreferenceAccessor.getValue(preferences,
				ApplicationPreferences.LOOKUP_ENTER_SELECTS_NEXT_REQUIRED, DisplayType.CHECK, true, locale);
		boolean enterSelectsFirstRequired = (boolean) InstancePreferenceAccessor.getValue(preferences, ApplicationPreferences.ENTER_SELECTS_FIRST_REQUIRED,
				DisplayType.CHECK, true, locale);

		boolean popupOpen = false;
		if (focussedControl instanceof Lookup) {
			Lookup lookup = (Lookup) focussedControl;
			popupOpen = lookup.popupIsOpen();
		}
		if (focussedControl instanceof TextAssist) {
			popupOpen = false;
		}

		// Wir holen uns das selektierte Feld.
		MField selectedField = null;
		List<MPage> pageList = detail.getPageList();
		for (MPage page : pageList) {
			List<MField> tabList = page.getTabList();
			for (MField field : tabList) {
				if (((AbstractValueAccessor) field.getValueAccessor()).getControl() == focussedControl) {
					selectedField = field;
				}
			}
		}

		// Die Preference LookupEnterSelectsNextRequired ist gesetzt.
		if (lookupEnterSelectsNextRequired == false && popupOpen) {
			focussedControl = ((AbstractValueAccessor) selectedField.getValueAccessor()).getControl();
			focussedControl.setFocus();
			return;
		}

		for (MPage page : pageList) {
			if (pageList.indexOf(page) >= pageList.indexOf(focussedControl)) {
				List<MField> tabList = page.getTabList();

				// Die Preference EnterSelectsFirstRequired ist gesetzt.
				if (enterSelectsFirstRequired == false && !popupOpen) {
					for (MField field : tabList) {
						if ((selectedField.getmPage() == page && tabList.indexOf(field) > tabList.indexOf(selectedField))
								|| (pageList.indexOf(selectedField.getmPage()) < pageList.indexOf(page))) {
							if (field.isRequired() == true) {
								focussedControl = ((AbstractValueAccessor) field.getValueAccessor()).getControl();
								focussedControl.setFocus();
								return;
							}
						}
					}
				} else {
					for (MField field : tabList) {
						if (field.isRequired() && null == field.getValue()) {
							focussedControl = ((AbstractValueAccessor) field.getValueAccessor()).getControl();
							focussedControl.setFocus();
							return;
						}
					}
				}

			}
		}

	}

}
