package aero.minova.rcp.rcp.handlers;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.IResourceUtilities;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.perspectiveswitcher.tools.E4PerspectiveSwitcherPreferences;
import org.eclipse.e4.ui.workbench.perspectiveswitcher.tools.E4Util;
import org.eclipse.e4.ui.workbench.perspectiveswitcher.tools.EPerspectiveSwitcher;
import org.eclipse.e4.ui.workbench.perspectiveswitcher.tools.IPerspectiveSwitcherControl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
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
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import aero.minova.rcp.perspectiveswitcher.commands.E4WorkbenchParameterConstants;

@SuppressWarnings("restriction")
public class PerspectiveControl implements IPerspectiveSwitcherControl {

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
	EPerspectiveSwitcher perspectiveSwitcher;

	@Inject
	MWindow window;

	@Inject
	MApplication application;

	@Inject
	private TranslationService translationService;

	/*
	 * Set preferences for showShortcutText
	 */
	@Inject
	@Preference(value = E4PerspectiveSwitcherPreferences.SHOW_TEXT, nodePath = E4PerspectiveSwitcherPreferences.ROOT_PREFERENCES_NODE)
	private boolean showShortcutText;
	static String _null = ""; //$NON-NLS-1$

	//
	Composite composite;
	ToolBar toolBar;
	ToolItem shortcut;

	/*
	 * Clear the Toolbar to prevent NullPointerExceptions
	 */
	@PreDestroy
	void cleanUp() {
		if (perspectiveSwitcher != null)
			perspectiveSwitcher.setControlProvider(null);
		perspectiveSwitcher = null;

		disposeToolBarImages();
	}

	/*
	 * Create the ToolControl with a Toolbar for the Perspective Shortcuts
	 */
	@PostConstruct
	public void createGui(Composite parent, MWindow window,
			@Optional @Named(E4WorkbenchParameterConstants.FORM_NAME) String perspectiveId) {
		perspectiveSwitcher.setControlProvider(this);
		composite = new Composite(parent, SWT.BAR);
		RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
		composite.setLayout(rowLayout);

		toolBar = new ToolBar(composite, SWT.FLAT | SWT.WRAP | SWT.RIGHT);
		toolBar.addMenuDetectListener(event -> {
			ToolBar tb = (ToolBar) event.widget;
			Point p = new Point(event.x, event.y);

			p = toolBar.getDisplay().map(null, toolBar, p);
			ToolItem item = tb.getItem(p);
			if (!item.getData().equals(null)) {
				if (item != null && item.getData() != null) {
					openMenuFor(item, (String) item.getData());
				} else if (item == null || item.getData().equals(null))
					logger.debug("No item found");
			} else
				logger.debug("Perspective not associated with item");

		});

		toolBar.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent event) {
				disposeToolBarImages();
			}
		});

		// The perspectives currently open
		List<MPerspectiveStack> appPerspectiveStacks = E4Util.getMatchingChildren(window, MPerspectiveStack.class);
		if (appPerspectiveStacks.size() >= 0) {
			for (MPerspectiveStack stack : appPerspectiveStacks)
				for (MPerspective perspective : stack.getChildren()) {
					if (perspective.isToBeRendered())
						addPerspectiveShortcut(perspective);
					if (perspective == modelService.getActivePerspective(window)) {
						setSelectedElement(perspective);
					}
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
		if (translationService != null && toolBar != null)
			translate();
	}

	private void translate() {
		for (ToolItem item : toolBar.getItems()) {
			List<MPerspective> perspectives = modelService.findElements(application, item.getData().toString(),
					MPerspective.class);
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
	@Override
	public void addPerspectiveShortcut(MPerspective perspective) {
		@SuppressWarnings("unchecked")
		List<String> keepit = (List<String>) application.getContext().get("perspectivetoolbar");

		if (!(keepit != null && keepit.contains(perspective.getElementId()))) {
			shortcut = new ToolItem(toolBar, SWT.RADIO);
			shortcut.setData(perspective.getElementId());
			ImageDescriptor descriptor = getIconFor(perspective.getIconURI());

			if (descriptor != null) {
				Image icon = descriptor.createImage();
				shortcut.setImage(icon);
			}

			if (descriptor == null || showShortcutText) {
				// Kein Icon, oder explizit gewünscht, Label wird als Text übernommen
				shortcut.setText(perspective.getLocalizedLabel() != null ? perspective.getLocalizedLabel() : _null);
			}
			shortcut.setToolTipText(perspective.getLocalizedTooltip());

			shortcut.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent event) {
					Map<String, String> parameter = Map.of("aero.minova.rcp.perspectiveswitcher.parameters.formName",
							perspective.getElementId());
					ParameterizedCommand command = commandService.createCommand("aero.minova.rcp.rcp.command.openform",
							parameter);
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
//				if (!"aero.minova.rcp.rcp.perspective.sis".equals(perspective.getElementId())) {
				addCloseMenuItem(menu, perspectiveId);
//				}
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
				toolBar.getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {
						menu.dispose();

					}
				});
			}
		});

	}

	ToolItem getToolItemFor(String perspectiveId) {
		if (toolBar == null || toolBar.isDisposed())
			return null;
		ToolItem toolItem = null;
		for (int i = 0; i < toolBar.getItems().length && toolItem == null; i++) {
			if (toolBar.getItem(i).getData().equals(perspectiveId))
				toolItem = toolBar.getItem(i);
		}

		return toolItem;

	}

	private void disposeToolBarImages() {
		if (toolBar == null || toolBar.isDisposed())
			return;

		for (ToolItem item : toolBar.getItems()) {
			Image icon = item.getImage();
			if (icon != null) {
				item.setImage(null);
				icon.dispose();
				icon = null;
			}
		}
	}

	@Override
	public void setSelectedElement(MPerspective perspective) {
		if (perspective == null) {
			return;
		}
		for (ToolItem item : toolBar.getItems())
			item.setSelection(item.getData().equals(perspective.getElementId()));
	}

	@Override
	public void removePerspectiveShortcut(MPerspective perspective) {
		@SuppressWarnings("unchecked")
		List<String> keepit = (List<String>) application.getContext().get("perspectivetoolbar");

		if (keepit == null || !keepit.contains(perspective.getElementId())) {
			ToolItem item = getToolItemFor(perspective.getElementId());
			removeToolItem(item);
		}
		// update the layout

	}

	private void removeToolItem(ToolItem item) {
		if (item == null || item.isDisposed())
			return;

		Image icon = item.getImage();
		if (icon != null) {
			item.setImage(null);
			icon.dispose();
			icon = null;
		}

		item.dispose();
	}

	@Override
	public void updateAttributeFor(MPerspective perspective, String attName, Object newValue) {
		ToolItem item = getToolItemFor(perspective.getElementId());

		if (showShortcutText && UIEvents.UILabel.LABEL.equals(attName)) {
			String newName = (String) newValue;
			item.setText(newName != null ? newName : _null);
		} else if (UIEvents.UILabel.TOOLTIP.equals(attName)) {
			String newTip = (String) newValue;
			item.setToolTipText(newTip);
		} else if (UIEvents.UILabel.ICONURI.equals(attName)) {
			ImageDescriptor descriptor = getIconFor((String) newValue);
			Image newIcon = null;
			Image oldIcon = item.getImage();

			if (descriptor != null)
				newIcon = descriptor.createImage();
			item.setImage(newIcon);

			if (oldIcon != null) {
				oldIcon.dispose();
				oldIcon = null;
			}

			if (!showShortcutText) {
				String label = null;
				if (item.getData() instanceof MPerspective)
					label = ((MPerspective) item.getData()).getLocalizedLabel();
				else
					label = item.getText();

				item.setText(item.getImage() == null ? label : _null);
			}
		}
	}

	//////////////////////////////////
	// Menu Items
	//////////////////////////////////

	private void addCloseMenuItem(Menu menu, String perspectiveId) {
		final MenuItem menuItem = new MenuItem(menu, SWT.Activate);
		menuItem.setText(translationService.translate("@Close", null));

		menuItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				Map<String, String> parameter = Map.of(E4WorkbenchParameterConstants.FORM_NAME, perspectiveId);
				ParameterizedCommand command = commandService
						.createCommand("aero.minova.rcp.rcp.command.closeperspective", parameter);
				handlerService.executeHandler(command);
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void addKeepItMenuItem(Menu menu, String perspectiveId, MPerspective perspective) {
		final MenuItem menuItem = new MenuItem(menu, SWT.CHECK);
		final List<String> keepItToolitems = (List<String>) application.getContext().get("perspectivetoolbar");

		menuItem.setText("KeepIt");
		menuItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				Map<String, String> parameter = Map.of(E4WorkbenchParameterConstants.FORM_NAME, perspectiveId);
				ParameterizedCommand command = commandService
						.createCommand("aero.minova.rcp.rcp.command.keepperspectivecommand", parameter);
				handlerService.executeHandler(command);

				// Entfernt das Toolitem wenn die Perspektive geschlossen ist und das KeepIt
				// Kennzeichen gelöscht wird.
				if (!(keepItToolitems != null && keepItToolitems.contains(perspectiveId)) && perspective == null) {
					ToolItem toolitem = getToolItemFor(perspectiveId);
					removeToolItem(toolitem);
				}
			}
		});
		menuItem.setSelection(keepItToolitems != null && keepItToolitems.contains(perspectiveId));

	}
}