package aero.minova.rcp.rcp.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MParameter;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledItem;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.ImageUtil;
import aero.minova.rcp.form.model.xsd.Browser;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.form.model.xsd.Grid;
import aero.minova.rcp.form.model.xsd.Head;
import aero.minova.rcp.form.model.xsd.Onclick;
import aero.minova.rcp.form.model.xsd.Page;
import aero.minova.rcp.form.model.xsd.Procedure;
import aero.minova.rcp.form.model.xsd.Wizard;
import aero.minova.rcp.model.form.MButton;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.rcp.accessor.ButtonAccessor;
import aero.minova.rcp.rcp.fields.FieldUtil;

public class CreateButtonInDetailUtil {

	MDetail mDetail;
	Form form;

	@Inject
	private ECommandService commandService;

	@Inject
	private EHandlerService handlerService;

	@Inject
	EModelService eModelService;

	@Inject
	MApplication mApplication;

	@Inject
	MWindow mwindow;

	@Inject
	MPart mPart;

	@Inject
	@Optional
	private WFCDetailCASRequestsUtil casRequestsUtil;

	@Inject
	private TranslationService translationService;

	private LocalResourceManager resManager;

	/**
	 * Erstellt einen oder mehrere Button auf der übergebenen Section. Die Button werden in der ausgelesenen Reihelfolge erstellt und in eine Reihe gesetzt.
	 *
	 * @param composite2
	 * @param headOPOGWrapper
	 * @param mSection
	 * @param section
	 */
	public void createButton(SectionWrapper headOPOGWrapper, Section section) {
		if (headOPOGWrapper.getSection() instanceof Grid || headOPOGWrapper.getSection() instanceof Browser) {
			return;
		}

		resManager = new LocalResourceManager(JFaceResources.getResources(), section);

		boolean isHead = headOPOGWrapper.isHead() && !headOPOGWrapper.isOP();

		final ToolBar bar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL | SWT.RIGHT | SWT.NO_FOCUS);

		List<aero.minova.rcp.form.model.xsd.Button> buttons = null;
		if (headOPOGWrapper.getSection() instanceof Head head) {
			buttons = head.getButton();
		} else {
			buttons = ((Page) headOPOGWrapper.getSection()).getButton();
		}

		for (aero.minova.rcp.form.model.xsd.Button btn : buttons) {
			MButton mButton = new MButton(btn.getId());
			mButton.setText(btn.getText());
			mButton.setIcon(btn.getIcon());

			ButtonAccessor ba;
			if (isHead) {
				ba = createToolItemInPartToolbar(btn);
			} else {
				ba = createToolItemInSection(bar, btn);
			}

			mButton.setButtonAccessor(ba);
			ba.setmButton(mButton);
			mDetail.putButton(mButton);
		}

		section.setTextClient(bar);
	}

	private ButtonAccessor createToolItemInSection(final ToolBar bar, aero.minova.rcp.form.model.xsd.Button btn) {

		// Kein Gruppenname: Element nur in Toolbar, kein Menü
		if (btn.getGroup() == null) {
			ToolItem item = new ToolItem(bar, SWT.PUSH);
			fillItemWithValues(item, btn);
			return new ButtonAccessor(item);
		}

		// Menü für Gruppennamen finden
		Menu groupMenu = null;
		if (btn.getGroup() != null) {
			for (ToolItem c : bar.getItems()) {
				if (btn.getGroup().equalsIgnoreCase((String) c.getData(Constants.GROUP_NAME))) {
					groupMenu = (Menu) c.getData(Constants.GROUP_MENU);
					break;
				}
			}
		}

		ToolItem toolItem = null;

		// Erstes Vorkommen des Gruppennamens: Element in Toolbar, Menü muss noch erstellt werden
		if (groupMenu == null) {
			final ToolItem item = new ToolItem(bar, SWT.DROP_DOWN);
			fillItemWithValues(item, btn);

			Menu menu = new Menu(new Shell(Display.getCurrent()), SWT.POP_UP);
			item.setData(Constants.GROUP_MENU, menu);

			item.addListener(SWT.Selection, event -> {
				if (event.detail == SWT.ARROW) {
					Rectangle rect = item.getBounds();
					Point pt = new Point(rect.x, rect.y + rect.height);
					pt = bar.toDisplay(pt);
					menu.setLocation(pt.x, pt.y);
					menu.setVisible(true);
				}
			});

			toolItem = item;
			groupMenu = menu;
		}

		// Wenn Gruppenname gegeben ist soll der Button immer auch in das Dropdown-Menü
		MenuItem menuEntry = new MenuItem(groupMenu, SWT.PUSH);
		fillItemWithValues(menuEntry, btn);

		return new ButtonAccessor(toolItem, menuEntry);
	}

	/**
	 * Füllt das Item mit den Werten aus dem Knopf (Text, Tooptip, Icon) und fügt den Onclick Listener hinzu, wenn in der Maske definiert
	 *
	 * @param item
	 * @param btn
	 */
	private void fillItemWithValues(Item item, aero.minova.rcp.form.model.xsd.Button btn) {
		item.setData(btn);
		item.setData(Constants.GROUP_NAME, btn.getGroup());

		if (item instanceof MenuItem mi) {
			mi.setEnabled(btn.isEnabled());
		} else if (item instanceof ToolItem ti) {
			ti.setEnabled(btn.isEnabled());
		}

		if (btn.getText() != null) {
			if (item instanceof MenuItem mi) {
				mi.setText(translationService.translate(btn.getText(), null));
				mi.setToolTipText(translationService.translate(btn.getText(), null));
			} else if (item instanceof ToolItem ti) {
				ti.setToolTipText(translationService.translate(btn.getText(), null));
			}
			item.setData(FieldUtil.TRANSLATE_PROPERTY, btn.getText());
		}
		if (btn.getIcon() != null && btn.getIcon().trim().length() > 0) {
			final ImageDescriptor buttonImageDescriptor = ImageUtil.getImageDescriptor(btn.getIcon().replace(".ico", ""), false);
			item.setImage(resManager.createImage(buttonImageDescriptor));
		}

		Object event = findEventForID(btn.getId());
		if (event instanceof Onclick onclick) {
			if (item instanceof MenuItem mi) {
				mi.addSelectionListener(getSelectionAdapterForItem(onclick, item));
			} else if (item instanceof ToolItem ti) {
				ti.addSelectionListener(getSelectionAdapterForItem(onclick, item));
			}
		}
	}

	private SelectionAdapter getSelectionAdapterForItem(Onclick onclick, Item item) {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean isEnabled = false;
				if (item instanceof MenuItem mi) {
					isEnabled = mi.isEnabled();
				} else if (item instanceof ToolItem ti) {
					isEnabled = ti.isEnabled();
				}
				if (e.detail != SWT.ARROW && isEnabled) {
					// TODO: Andere procedures/bindings/instances auswerten
					List<Object> binderOrProcedureOrInstances = onclick.getBinderOrProcedureOrInstance();

					for (Object o : binderOrProcedureOrInstances) {
						if (o instanceof Wizard wizard) {
							Map<String, String> parameter = Map.of(Constants.CLAZZ, Constants.WIZARD, Constants.PARAMETER, wizard.getWizardname());
							ParameterizedCommand command = commandService.createCommand(Constants.AERO_MINOVA_RCP_RCP_COMMAND_DYNAMIC_BUTTON, parameter);
							handlerService.executeHandler(command);
						} else if (o instanceof Procedure procedure) {
							casRequestsUtil.callProcedure(procedure);
						} else {
							// Auch in Methode createParameters() anpassen!!
							System.err.println("Event vom Typ " + o.getClass() + " für Buttons noch nicht implementiert!");
						}
					}
				}
			}
		};
	}

	private ButtonAccessor createToolItemInPartToolbar(aero.minova.rcp.form.model.xsd.Button btn) {

		// Kein Gruppenname: Element nur in Toolbar, kein Menü
		if (btn.getGroup() == null) {
			MHandledToolItem handledToolItem = eModelService.createModelElement(MHandledToolItem.class);
			fillMHandledItemWithValues(handledToolItem, btn);
			mPart.getToolbar().getChildren().add(handledToolItem);
			return new ButtonAccessor(handledToolItem);
		}

		// Menü für Gruppennamen finden
		MMenu groupMenu = null;
		if (btn.getGroup() != null) {
			for (MToolBarElement element : mPart.getToolbar().getChildren()) {
				if (btn.getGroup().equalsIgnoreCase(element.getPersistedState().get(Constants.GROUP_NAME))) {
					groupMenu = ((MHandledToolItem) element).getMenu();
					break;
				}
			}
		}

		MHandledToolItem handledToolItem = null;

		// Erstes Vorkommen des Gruppennamens: Element in Toolbar, Menü muss noch erstellt werden
		if (groupMenu == null) {
			handledToolItem = eModelService.createModelElement(MHandledToolItem.class);
			fillMHandledItemWithValues(handledToolItem, btn);
			mPart.getToolbar().getChildren().add(handledToolItem);

			groupMenu = eModelService.createModelElement(MMenu.class);
			handledToolItem.getPersistedState().put(Constants.GROUP_NAME, btn.getGroup());
			handledToolItem.setMenu(groupMenu);
		}

		// Wenn Gruppenname gegeben ist soll der Button immer auch in das Dropdown-Menü
		MHandledMenuItem menuEntry = eModelService.createModelElement(MHandledMenuItem.class);
		fillMHandledItemWithValues(menuEntry, btn);
		groupMenu.getChildren().add(menuEntry);

		return new ButtonAccessor(handledToolItem, menuEntry);
	}

	/**
	 * Füllt das handledItem mit den Werten aus dem Knopf (Text, Tooptip, Icon), fügt den Command (und damit den Handler) sowie die benötigten Parameter hinzu
	 *
	 * @param handledItem
	 * @param btn
	 */
	private void fillMHandledItemWithValues(MHandledItem handledItem, aero.minova.rcp.form.model.xsd.Button btn) {
		handledItem.getPersistedState().put(IWorkbench.PERSIST_STATE, String.valueOf(false));
		handledItem.getPersistedState().put(Constants.CONTROL_ID, btn.getId());
		handledItem.setLabel(btn.getText());
		handledItem.setTooltip(btn.getText());
		if (btn.getIcon() != null && btn.getIcon().trim().length() > 0) {
			handledItem.setIconURI(ImageUtil.retrieveIcon(btn.getIcon(), false));
		}

		MCommand command = mApplication.getCommand(Constants.AERO_MINOVA_RCP_RCP_COMMAND_DYNAMIC_BUTTON);
		handledItem.setCommand(command);

		Object event = findEventForID(btn.getId());
		if (event instanceof Onclick onclick) {
			List<Object> binderOrProcedureOrInstances = onclick.getBinderOrProcedureOrInstance();
			handledItem.getParameters().addAll(createParameters(binderOrProcedureOrInstances));
		} else {
			MParameter mParameterForm = eModelService.createModelElement(MParameter.class);
			mParameterForm.setName(Constants.PARAMETER);
			mParameterForm.setValue(btn.getId());
			handledItem.getParameters().add(mParameterForm);
		}
	}

	private List<MParameter> createParameters(List<Object> binderOrProcedureOrInstances) {
		List<MParameter> parameter = new ArrayList<>();
		MParameter mParameterForm = null;
		for (Object o : binderOrProcedureOrInstances) {
			if (o instanceof Wizard wizard) {
				mParameterForm = eModelService.createModelElement(MParameter.class);
				mParameterForm.setName(Constants.CLAZZ);
				mParameterForm.setValue(Constants.WIZARD);
				parameter.add(mParameterForm);

				mParameterForm = eModelService.createModelElement(MParameter.class);
				mParameterForm.setName(Constants.PARAMETER);
				mParameterForm.setValue(wizard.getWizardname());
				parameter.add(mParameterForm);
			} else if (o instanceof Procedure p) {
				String procedureID = p.getName() + p.getParam().hashCode();
				mPart.getContext().set(procedureID, p);

				mParameterForm = eModelService.createModelElement(MParameter.class);

				mParameterForm.setName(Constants.CLAZZ);
				mParameterForm.setValue(Constants.PROCEDURE);
				parameter.add(mParameterForm);

				mParameterForm = eModelService.createModelElement(MParameter.class);
				mParameterForm.setName(Constants.PARAMETER);
				mParameterForm.setValue(procedureID);
				parameter.add(mParameterForm);
			} else {
				// Auch in Methode getSelectionAdapterForItem() anpassen!!
				System.err.println("Event vom Typ " + o.getClass() + " für Buttons noch nicht implementiert!");
			}
		}
		return parameter;
	}

	private Object findEventForID(String id) {
		if (form.getEvents() != null) {
			for (Onclick onclick : form.getEvents().getOnclick()) {
				if (onclick.getRefid().equals(id)) {
					return onclick;
				}
			}
		}
		// TODO: Onbinder und ValueChange implementieren
		return null;
	}

	public void setMDetail(MDetail mDetail) {
		this.mDetail = mDetail;
	}

	public void setForm(Form form) {
		this.form = form;

	}
}
