package aero.minova.rcp.rcp.handlers;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.IResourceUtilities;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.perspectiveswitcher.commands.E4WorkbenchCommandConstants;
import org.eclipse.e4.ui.workbench.perspectiveswitcher.tools.E4PerspectiveSwitcherPreferences;
import org.eclipse.e4.ui.workbench.perspectiveswitcher.tools.E4Util;
import org.eclipse.e4.ui.workbench.perspectiveswitcher.tools.EPerspectiveSwitcher;
import org.eclipse.e4.ui.workbench.perspectiveswitcher.tools.IPerspectiveSwitcherControl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
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
	@Preference(value = E4PerspectiveSwitcherPreferences.SHOW_TEXT, nodePath = E4PerspectiveSwitcherPreferences.ROOT_PREFERENCES_NODE)
	private boolean showShortcutText;
	static String _null = ""; //$NON-NLS-1$

	//
	Composite composite;
	ToolBar toolBar;
	ToolItem shortcut;

	@PreDestroy
	void cleanUp() {
		if (perspectiveSwitcher != null)
			perspectiveSwitcher.setControlProvider(null);
		perspectiveSwitcher = null;

		disposeToolBarImages();
	}

	@PostConstruct
	public void createGui(Composite parent, MWindow window) {
		perspectiveSwitcher.setControlProvider(this);
		composite = new Composite(parent, SWT.BAR);
		RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
		composite.setLayout(rowLayout);

		toolBar = new ToolBar(composite, SWT.FLAT | SWT.BAR );
		toolBar.addMenuDetectListener(new MenuDetectListener() {

			@Override
			public void menuDetected(MenuDetectEvent event) {
				ToolBar tb = (ToolBar) event.widget;
				Point p = new Point(event.x, event.y);

				p = toolBar.getDisplay().map(null, toolBar, p);
				ToolItem item = tb.getItem(p);
				if (item != null && item.getData() != null)
					openMenuFor(item, (MPerspective) item.getData());
				else if (item == null)
					logger.debug("No item found");
				else
					logger.debug("Perspective not associated with item");

			}
		});
		
		toolBar.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent event) {
				disposeToolBarImages();
			}
		});

		List<MPerspectiveStack> appPerspectiveStacks = E4Util.getMatchingChildren(window, MPerspectiveStack.class);
		if (appPerspectiveStacks.size() > 0) {
			for (MPerspectiveStack stack : appPerspectiveStacks)
				for (MPerspective perspective : stack.getChildren()) {
					if (perspective.isToBeRendered())
						addPerspectiveShortcut(perspective);
				}
		}
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

	@Override
	public void addPerspectiveShortcut(MPerspective perspective) {
		shortcut = new ToolItem(toolBar, SWT.RADIO);
		shortcut.setData(perspective);

		ImageDescriptor descriptor = getIconFor(perspective.getIconURI());

		if (descriptor != null) {
			Image icon = descriptor.createImage();
			shortcut.setImage(icon);
		}

		if (descriptor == null || showShortcutText) {
			String label = perspective.getLocalizedLabel();
			shortcut.setText(label != null ? label : _null);
			shortcut.setToolTipText(perspective.getLocalizedTooltip());
		} else {
			shortcut.setText(_null);
			shortcut.setToolTipText(perspective.getLocalizedLabel());
		}

		shortcut.setSelection(E4Util.isSelectedElement(perspective));

		shortcut.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				MPerspective perspective = (MPerspective) event.widget.getData();
				E4Util.setWindowSelectedElement(perspective);
			}
		});

		// update the layout ???

	}

	void openMenuFor(ToolItem item, MPerspective perspective) {
		final Menu menu = new Menu(toolBar);
		menu.setData(perspective);

		if (perspective.isVisible()) {
			addCloseMenuItem(menu);
		}

		new MenuItem(menu, SWT.SEPARATOR);

		addKeepItMenuItem(menu);

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

	private void addCloseMenuItem(Menu menu) {
		final MenuItem menuItem = new MenuItem(menu, SWT.Activate);
		menuItem.setText(E4WorkbenchCommandConstants.PERSPECTIVES_CLOSE$_NAME);

		// TODO: Integrate into help system

		menuItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				ParameterizedCommand command = commandService
						.createCommand(E4WorkbenchCommandConstants.PERSPECTIVES_CLOSE, Collections.EMPTY_MAP);
				handlerService.executeHandler(command);
			}
		});
	}

	private void addKeepItMenuItem(Menu menu) {
		final MenuItem menuItem = new MenuItem(menu, SWT.Activate | SWT.CHECK);
		menuItem.setText("KeepIt");
		menuItem.setSelection(showShortcutText);

		// TODO: Integrate into help system

		menuItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				Map<String, Object> parameters = new HashMap<>(3);
				ParameterizedCommand command = commandService
						.createCommand(E4WorkbenchCommandConstants.PERSPECTIVES_SHOW_TEXT, parameters);
				handlerService.executeHandler(command);
			}
		});
	}
	
	@Override
	public void setSelectedElement(MPerspective perspective) {
		for (ToolItem item : toolBar.getItems())
			item.setSelection(item.getData() == perspective);
	}

	@Override
	public void removePerspectiveShortcut(MPerspective perspective) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAttributeFor(MPerspective perspective, String attributeName, Object newValue) {
		// TODO Auto-generated method stub

	}

}