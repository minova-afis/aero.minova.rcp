package aero.minova.rcp.rcp.handlers;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.IResourceUtilities;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.osgi.service.event.Event;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.util.ErrorObject;
import aero.minova.rcp.perspectiveswitcher.commands.E4WorkbenchParameterConstants;
import aero.minova.rcp.rcp.util.ShowErrorDialogHandler;

@SuppressWarnings("restriction")
public class PerspectiveControl {

	@Inject
	private Logger logger;

	@Inject
	private EModelService modelService;

	@Inject
	private ECommandService commandService;

	@Inject
	private EHandlerService handlerService;

	@Inject
	private IResourceUtilities<?> resourceUtilities;

	@Inject
	MWindow window;

	@Inject
	MApplication application;

	@Inject
	private TranslationService translationService;

	Composite composite;
	ToolBar toolBar;
	ToolItem shortcut;

	Preferences prefs = InstanceScope.INSTANCE.getNode(Constants.PREFERENCES_KEPTPERSPECTIVES);

	/*
	 * Clear the Toolbar to prevent NullPointerExceptions
	 */
	@PreDestroy
	void cleanUp() {
		disposeToolBarImages();
	}

	/*
	 * Create the ToolControl with a Toolbar for the Perspective Shortcuts
	 */
	@PostConstruct
	public void createGui(Composite parent, MWindow window) {
		composite = new Composite(parent, SWT.BAR);
		RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
		composite.setLayout(rowLayout);

		toolBar = new ToolBar(composite, SWT.FLAT | SWT.WRAP | SWT.RIGHT);
		toolBar.addMenuDetectListener(event -> {
			ToolBar tb = (ToolBar) event.widget;
			Point p = new Point(event.x, event.y);

			p = toolBar.getDisplay().map(null, toolBar, p);
			ToolItem item = tb.getItem(p);

			if (item != null && item.getData() != null) {
				openMenuFor(item, (String) item.getData());
			} else {
				logger.debug("No item found or item is null");
			}
		});

		toolBar.addDisposeListener(event -> disposeToolBarImages());

		// The perspectives currently open
		List<MPerspective> perspectives = modelService.findElements(window, null, MPerspective.class);
		for (MPerspective perspective : perspectives) {
			if (perspective.isToBeRendered()) {
				addPerspectiveShortcut(perspective, true);
			}
			if (perspective == modelService.getActivePerspective(window)) {
				setSelectedElement(perspective);
			}
		}
		translate(translationService);
	}

	@Inject
	@Optional
	private void getNotified(@Named(TranslationService.LOCALE) Locale s) {
		translate(translationService);
	}

	@Inject
	private void translate(TranslationService translationService) {
		this.translationService = translationService;
		if (translationService != null && toolBar != null) {
			translate();
		}
	}

	private void translate() {
		for (ToolItem item : toolBar.getItems()) {
			List<MPerspective> perspectives = modelService.findElements(application, item.getData().toString(), MPerspective.class);
			MPerspective perspective = perspectives.get(0);
			String value = translationService.translate(perspective.getLocalizedLabel(), null);
			item.setText(value);
		}
		toolBar.pack(true);
	}

	ImageDescriptor getIconFor(String iconURI) {
		ImageDescriptor descriptor = null;
		try {
			URI uri = URI.createURI(iconURI);
			descriptor = (ImageDescriptor) resourceUtilities.imageDescriptorFromURI(uri);
		} catch (RuntimeException ex) {
			logger.debug(ex, "icon uri=" + iconURI);
		}
		return descriptor;
	}

	/*
	 * Add shortcut for the perspective in the toolbar
	 */
	public void addPerspectiveShortcut(MPerspective perspective, boolean openAll) {
		String keptPerspective = prefs.get(perspective.getElementId(), "");

		if (keptPerspective.isBlank() || openAll) {
			shortcut = new ToolItem(toolBar, SWT.RADIO);
			shortcut.setData(perspective.getElementId());
			ImageDescriptor descriptor = getIconFor(perspective.getIconURI());

			if (descriptor != null) {
				Image icon = descriptor.createImage();
				shortcut.setImage(icon);
			}

			if (descriptor == null) {
				// Kein Icon, oder explizit gewünscht, Label wird als Text übernommen
				shortcut.setText(perspective.getLocalizedLabel() != null ? perspective.getLocalizedLabel() : "");
			}
			shortcut.setToolTipText(perspective.getLocalizedTooltip());

			shortcut.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent event) {
					Map<String, String> parameter = Map.of(//
							Constants.FORM_NAME, perspective.getPersistedState().get(Constants.FORM_NAME), //
							Constants.FORM_ID, perspective.getElementId(), //
							Constants.FORM_LABEL, perspective.getLabel());

					ParameterizedCommand command = commandService.createCommand("aero.minova.rcp.rcp.command.openform", parameter);
					handlerService.executeHandler(command);
				}
			});
		} else {
			shortcut = getToolItemFor(perspective.getElementId());
		}
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

	private void disposeToolBarImages() {
		if (toolBar == null || toolBar.isDisposed()) {
			return;
		}

		for (ToolItem item : toolBar.getItems()) {
			Image icon = item.getImage();
			if (icon != null) {
				item.setImage(null);
				icon.dispose();
			}
		}
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
		String keptPerspective = prefs.get(perspective.getElementId(), "");

		if (keptPerspective.isBlank()) {
			ToolItem item = getToolItemFor(perspective.getElementId());
			removeToolItem(item);
		}
		// update the layout

	}

	private void removeToolItem(ToolItem item) {
		if (item == null || item.isDisposed()) {
			return;
		}

		Image icon = item.getImage();
		if (icon != null) {
			item.setImage(null);
			icon.dispose();
		}

		item.dispose();
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

	@SuppressWarnings("unchecked")
	private void addKeepItMenuItem(Menu menu, String perspectiveId, MPerspective perspective) {
		final MenuItem menuItem = new MenuItem(menu, SWT.CHECK);
		String keptPerspective = prefs.get(perspectiveId, "");

		menuItem.setText(translationService.translate("@Action.KeepIt", null));
		menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				Map<String, String> parameter = Map.of(E4WorkbenchParameterConstants.FORM_NAME, perspectiveId);
				ParameterizedCommand command = commandService.createCommand("aero.minova.rcp.rcp.command.keepperspectivecommand", parameter);
				handlerService.executeHandler(command);

				String newKeptPerspective = prefs.get(perspectiveId, "");

				// Entfernt das Toolitem wenn die Perspektive geschlossen ist und das KeepIt Kennzeichen gelöscht wird.
				if (keptPerspective.isBlank() && perspective == null) {
					ToolItem toolitem = getToolItemFor(perspectiveId);
					removeToolItem(toolitem);
				}
			}
		});
		menuItem.setSelection(!keptPerspective.isBlank());
	}

	@Inject
	@Optional
	public void showConnectionErrorMessage(EPartService partService, EModelService model, Shell shell,
			@UIEventTopic(Constants.BROKER_SHOWCONNECTIONERRORMESSAGE) ErrorObject et) {
		String translate = translationService.translate("@" + et.getMessage(), null);
		if (et.getT() == null) {
			MessageDialog.openError(shell, "Error", translate);
		} else {
			ShowErrorDialogHandler.execute(shell, "Error", translate, et.getT());
		}
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

				this.addPerspectiveShortcut(added, false);
			}
		} else if (UIEvents.isREMOVE(event)) {
			for (Object o : UIEvents.asIterable(event, UIEvents.EventTags.OLD_VALUE)) {
				MPerspective removed = (MPerspective) o;
				this.removePerspectiveShortcut(removed);
			}
		}
	}
}