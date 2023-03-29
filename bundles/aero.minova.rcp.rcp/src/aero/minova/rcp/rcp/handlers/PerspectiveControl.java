package aero.minova.rcp.rcp.handlers;

import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_PROPERTY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.osgi.service.event.Event;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.ImageUtil;
import aero.minova.rcp.perspectiveswitcher.commands.E4WorkbenchParameterConstants;

public class PerspectiveControl {

	ILog logger = Platform.getLog(this.getClass());

	@Inject
	private EModelService modelService;

	@Inject
	private ECommandService commandService;

	@Inject
	private EHandlerService handlerService;

	@Inject
	MWindow window;

	@Inject
	MApplication application;

	@Inject
	private TranslationService translationService;

	Composite composite;
	ToolBar toolBar;
	ToolItem shortcut;

	private LocalResourceManager resManager;

	Preferences prefsKeptPerspectives = InstanceScope.INSTANCE.getNode(Constants.PREFERENCES_KEPTPERSPECTIVES);
	Preferences prefsToolbarOrder = InstanceScope.INSTANCE.getNode(Constants.PREFERENCES_TOOLBARORDER);
	List<String> openToolbarItems;

	/*
	 * Create the ToolControl with a Toolbar for the Perspective Shortcuts
	 */
	@PostConstruct
	public void createGui(Composite parent, MWindow window) {

		composite = new Composite(parent, SWT.BAR);
		RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
		composite.setLayout(rowLayout);

		resManager = new LocalResourceManager(JFaceResources.getResources(), composite);

		toolBar = new ToolBar(composite, SWT.FLAT | SWT.WRAP | SWT.RIGHT);
		toolBar.addMenuDetectListener(event -> {
			ToolBar tb = (ToolBar) event.widget;
			Point p = new Point(event.x, event.y);

			p = toolBar.getDisplay().map(null, toolBar, p);
			ToolItem item = tb.getItem(p);

			if (item != null && item.getData() != null) {
				openMenuFor(item, (String) item.getData());
			} else {
				logger.info("No item found or item is null");
			}
		});

		List<String> oldToolbarOrder = readOldToolbarOrder();
		openToolbarItems = new ArrayList<>();

		// Perspektiven der vorherigen Session wiederherstellen
		for (String id : oldToolbarOrder) {
			if (id.isBlank()) {
				continue;
			}

			List<MPerspective> perspectives = modelService.findElements(window, id, MPerspective.class);
			if (!perspectives.isEmpty()) { // Perspektive war geöffnet
				MPerspective perspective = perspectives.get(0);
				if (perspective.isToBeRendered()) {
					addPerspectiveShortcut(perspective.getElementId(), //
							perspective.getPersistedState().get(Constants.FORM_NAME), //
							perspective.getLabel(), //
							perspective.getIconURI(), //
							true);
				}
				if (perspective == modelService.getActivePerspective(window)) {
					setSelectedElement(perspective);
				}

			} else if (!prefsKeptPerspectives.get(id + Constants.KEPT_PERSPECTIVE_FORMNAME, "").equals("")) { // Perspektive war angeheftet
				addPerspectiveShortcut(id, //
						prefsKeptPerspectives.get(id + Constants.KEPT_PERSPECTIVE_FORMNAME, ""), //
						prefsKeptPerspectives.get(id + Constants.KEPT_PERSPECTIVE_FORMLABEL, ""), //
						prefsKeptPerspectives.get(id + Constants.KEPT_PERSPECTIVE_ICONURI, ""), //
						true);
			}
		}

		translate(translationService);
	}

	@Inject
	@Optional
	private void getNotified(@Named(TranslationService.LOCALE) Locale s) {
		translate();
	}

	@Inject
	private void translate(TranslationService translationService) {
		this.translationService = translationService;
		if (translationService != null && toolBar != null) {
			translate();
		}
	}

	private void translate() {
		if (toolBar == null) {
			return;
		}

		for (ToolItem item : toolBar.getItems()) {
			String property = (String) item.getData(TRANSLATE_PROPERTY);
			if (property == null) {
				continue;
			}
			String value = translationService.translate(property, null);

			if (item.getImage() == null) {
				item.setText(value);
			}
			item.setToolTipText(value);
		}
		toolBar.pack(true);
	}

	/*
	 * Add shortcut for the perspective in the toolbar
	 */
	public void addPerspectiveShortcut(String perspectiveId, String formName, String formLable, String iconURI, boolean openAll) {
		String keptPerspective = prefsKeptPerspectives.get(perspectiveId + Constants.KEPT_PERSPECTIVE_FORMNAME, "");

		shortcut = getToolItemFor(perspectiveId);

		// Wenn der Shortcut schon existiert soll er nicht nochmal erstellt werden
		if (shortcut == null && (keptPerspective.isBlank() || openAll)) {
			openToolbarItems.add(perspectiveId);
			saveToolbarOrder();

			shortcut = new ToolItem(toolBar, SWT.RADIO);
			shortcut.setData(perspectiveId);
			shortcut.setData(TRANSLATE_PROPERTY, formLable);

			ImageDescriptor descriptor = ImageUtil.getImageDescriptor(iconURI, true);
			if (descriptor != null && !descriptor.equals(ImageDescriptor.getMissingImageDescriptor())) {
				shortcut.setImage(resManager.createImage(descriptor));
			}

			shortcut.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					Map<String, String> parameter = Map.of(//
							Constants.FORM_NAME, formName, //
							Constants.FORM_ID, perspectiveId, //
							Constants.FORM_LABEL, formLable, //
							Constants.FORM_ICON, iconURI);

					ParameterizedCommand command = commandService.createCommand("aero.minova.rcp.rcp.command.openform", parameter);
					handlerService.executeHandler(command);
				}
			});
		}
		translate();
	}

	/*
	 * Create a popupmenu for the toolbar
	 */
	void openMenuFor(ToolItem item, String perspectiveId) {
		MPerspective perspective = (MPerspective) modelService.find(perspectiveId, window);
		final Menu menu = new Menu(toolBar);

		if (perspective != null) {
			menu.setData(perspective.getElementId());

			if (perspective.isVisible()) {
				addCloseMenuItem(menu, perspectiveId);
			}
		}

		new MenuItem(menu, SWT.SEPARATOR);
		addKeepItMenuItem(menu, perspectiveId, perspective);

		Rectangle bounds = item.getBounds();
		Point point = toolBar.toDisplay(bounds.x, bounds.y + bounds.height);
		menu.setLocation(point);
		menu.setVisible(true);
		menu.addMenuListener(new MenuListener() {

			@Override
			public void menuShown(MenuEvent e) {
				// do nothing
			}

			@Override
			public void menuHidden(MenuEvent e) {
				toolBar.getDisplay().asyncExec(menu::dispose);
			}
		});
	}

	ToolItem getToolItemFor(String perspectiveId) {
		if (toolBar == null || toolBar.isDisposed()) {
			return null;
		}
		ToolItem toolItem = null;
		for (int i = 0; i < toolBar.getItems().length && toolItem == null; i++) {
			if (toolBar.getItem(i).getData().equals(perspectiveId)) {
				toolItem = toolBar.getItem(i);
			}
		}

		return toolItem;
	}

	public void setSelectedElement(MPerspective perspective) {
		if (perspective == null) {
			return;
		}
		for (ToolItem item : toolBar.getItems()) {
			item.setSelection(item.getData().equals(perspective.getElementId()));
		}
	}

	public void removePerspectiveShortcut(MPerspective perspective) {
		String keptPerspective = prefsKeptPerspectives.get(perspective.getElementId() + Constants.KEPT_PERSPECTIVE_FORMNAME, "");
		if (keptPerspective.isBlank()) {
			ToolItem item = getToolItemFor(perspective.getElementId());
			removeToolItem(item);
		}
	}

	private void removeToolItem(ToolItem item) {
		if (item == null || item.isDisposed()) {
			return;
		}

		openToolbarItems.remove(item.getData());
		saveToolbarOrder();
		item.dispose();
	}

	private void saveToolbarOrder() {
		StringBuilder list = new StringBuilder();

		for (String s : openToolbarItems) {
			if (!s.isBlank()) {
				list.append(s + ",");
			}
		}

		prefsToolbarOrder.put("order", list.toString());
		try {
			prefsToolbarOrder.flush();
		} catch (BackingStoreException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private List<String> readOldToolbarOrder() {
		ArrayList<String> oldToolbarOrder = new ArrayList<>();
		String orderString = prefsToolbarOrder.get("order", "");
		oldToolbarOrder.addAll(Arrays.asList(orderString.split(",")));
		return oldToolbarOrder;
	}

	//////////////////////////////////
	// Menu Items
	//////////////////////////////////

	private void addCloseMenuItem(Menu menu, String perspectiveId) {
		final MenuItem menuItem = new MenuItem(menu, SWT.Activate);
		menuItem.setText(translationService.translate("@Action.Close", null));

		menuItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				Map<String, String> parameter = Map.of(E4WorkbenchParameterConstants.FORM_NAME, perspectiveId);
				ParameterizedCommand command = commandService.createCommand("aero.minova.rcp.rcp.command.closeperspective", parameter);
				handlerService.executeHandler(command);
			}
		});
	}

	private void addKeepItMenuItem(Menu menu, String perspectiveId, MPerspective perspective) {
		final MenuItem menuItem = new MenuItem(menu, SWT.CHECK);
		String keptPerspective = prefsKeptPerspectives.get(perspectiveId + Constants.KEPT_PERSPECTIVE_FORMNAME, "");

		menuItem.setText(translationService.translate("@Action.KeepIt", null));
		menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				Map<String, String> parameter = Map.of(E4WorkbenchParameterConstants.FORM_NAME, perspectiveId);
				ParameterizedCommand command = commandService.createCommand("aero.minova.rcp.rcp.command.keepperspectivecommand", parameter);
				handlerService.executeHandler(command);

				String newKeptPerspective = prefsKeptPerspectives.get(perspectiveId + Constants.KEPT_PERSPECTIVE_FORMNAME, "");

				// Entfernt das Toolitem wenn die Perspektive geschlossen ist und das KeepIt Kennzeichen gelöscht wird.
				if (newKeptPerspective.isBlank() && perspective == null) {
					ToolItem toolitem = getToolItemFor(perspectiveId);
					removeToolItem(toolitem);
				}
			}
		});
		menuItem.setSelection(!keptPerspective.isBlank());
	}

	@Inject
	@Optional
	private void subscribeSelectionEvent(@UIEventTopic(UIEvents.ElementContainer.TOPIC_SELECTEDELEMENT) Event event) {
		if (window == null) {
			return;
		}
		MUIElement changedElement = (MUIElement) event.getProperty(UIEvents.EventTags.ELEMENT);
		if (!(changedElement instanceof MPerspectiveStack)) {
			return;
		}

		MPerspectiveStack perspectiveStack = (MPerspectiveStack) changedElement;
		if (!perspectiveStack.isToBeRendered()) {
			return;
		}

		MWindow stackWindow = modelService.getContainingContext(perspectiveStack).get(MWindow.class);
		if (window != stackWindow) {
			return;
		}

		MPerspective selectedElement = perspectiveStack.getSelectedElement();
		this.setSelectedElement(selectedElement);
	}

	@Inject
	@Optional
	private void subscribeChildrenEvent(@UIEventTopic(UIEvents.ElementContainer.TOPIC_CHILDREN) Event event) {
		if (window == null) {
			return;
		}

		MUIElement changedElement = (MUIElement) event.getProperty(UIEvents.EventTags.ELEMENT);
		if (!(changedElement instanceof MPerspectiveStack)) {
			return;
		}

		MPerspectiveStack perspectiveStack = (MPerspectiveStack) changedElement;
		if (!perspectiveStack.isToBeRendered()) {
			return;
		}

		MWindow stackWindow = modelService.getContainingContext(perspectiveStack).get(MWindow.class);
		if (window != stackWindow) {
			return;
		}

		if (UIEvents.isADD(event)) {
			for (Object o : UIEvents.asIterable(event, UIEvents.EventTags.NEW_VALUE)) {
				MPerspective added = (MPerspective) o;
				// Adding invisible elements is a NO-OP
				if (!added.isToBeRendered()) {
					continue;
				}

				this.addPerspectiveShortcut(added.getElementId(), //
						added.getPersistedState().get(Constants.FORM_NAME), //
						added.getLabel(), //
						added.getIconURI(), //
						false);
			}
		} else if (UIEvents.isREMOVE(event)) {
			for (Object o : UIEvents.asIterable(event, UIEvents.EventTags.OLD_VALUE)) {
				MPerspective removed = (MPerspective) o;
				this.removePerspectiveShortcut(removed);
			}
		}
	}
}