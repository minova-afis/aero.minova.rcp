package aero.minova.rcp.rcp.nattable;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.CellStyleUtil;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.style.VerticalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.nebula.widgets.nattable.widget.NatCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.util.WildcardMatcher;

public class MinovaNatCombo extends NatCombo {

	public MinovaNatCombo(Composite parent, IStyle cellStyle, int maxVisibleItems, int style, Image iconImage) {
		super(parent, cellStyle, maxVisibleItems, style, iconImage);
	}

	public MinovaNatCombo(Composite parent, IStyle cellStyle, int maxVisibleItems, int style, boolean showDropdownFilter) {
		super(parent, cellStyle, maxVisibleItems, style, showDropdownFilter);
	}

	public MinovaNatCombo(Composite parent, IStyle cellStyle, int maxVisibleItems, int style, Image iconImage, boolean showDropdownFilter) {
		super(parent, cellStyle, maxVisibleItems, style, iconImage, showDropdownFilter);
	}

	@Override
	protected void createTextControl(int style) {
		int widgetStyle = style | HorizontalAlignmentEnum.getSWTStyle(this.cellStyle);
		this.text = new Text(this, widgetStyle);
		this.text.setBackground(this.cellStyle.getAttributeValue(CellStyleAttributes.BACKGROUND_COLOR));
		this.text.setForeground(this.cellStyle.getAttributeValue(CellStyleAttributes.FOREGROUND_COLOR));
		this.text.setFont(this.cellStyle.getAttributeValue(CellStyleAttributes.FONT));

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		this.text.setLayoutData(gridData);

		this.text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				if ((event.keyCode == SWT.CR) || (event.keyCode == SWT.KEYPAD_CR)) {
					updateTextControl(true);
				} else if (event.keyCode == SWT.ARROW_DOWN || event.keyCode == SWT.ARROW_UP) {
					showDropdownControl();
					// ensure the arrow key events do not have any further
					// effect
					event.doit = false;
				}
			}

			@Override
			public void keyReleased(KeyEvent event) {
				if (null != dropdownTableViewer && !dropdownTable.isDisposed()) {
					dropdownTableViewer.refresh();
					calculateBounds();
				}
			}
		});

		this.text.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				if (!freeEdit) {
					if (getDropdownTable().isDisposed() || !getDropdownTable().isVisible()) {
						showDropdownControl();
					} else {
						// if there is no free edit enabled, set the focus back
						// to the dropdownlist so it handles key strokes itself
						getDropdownTable().forceFocus();
					}
				}
			}
		});

		this.text.addControlListener(new ControlListener() {
			@Override
			public void controlResized(ControlEvent e) {
				calculateBounds();
			}

			@Override
			public void controlMoved(ControlEvent e) {
				calculateBounds();
			}
		});

		this.text.addFocusListener(new FocusListenerWrapper());

		final Canvas iconCanvas = new Canvas(this, SWT.NONE) {

			@Override
			public Point computeSize(int wHint, int hHint, boolean changed) {
				Rectangle iconImageBounds = iconImage.getBounds();
				return new Point(iconImageBounds.width + 2, iconImageBounds.height + 2);
			}

		};

		gridData = new GridData(GridData.BEGINNING, SWT.FILL, false, true);
		iconCanvas.setLayoutData(gridData);

		iconCanvas.addPaintListener(event -> {
			GC gc = event.gc;

			Rectangle iconCanvasBounds = iconCanvas.getBounds();
			Rectangle iconImageBounds = iconImage.getBounds();
			int horizontalAlignmentPadding = CellStyleUtil.getHorizontalAlignmentPadding(HorizontalAlignmentEnum.CENTER, iconCanvasBounds,
					iconImageBounds.width);
			int verticalAlignmentPadding = CellStyleUtil.getVerticalAlignmentPadding(VerticalAlignmentEnum.MIDDLE, iconCanvasBounds, iconImageBounds.height);
			gc.drawImage(iconImage, horizontalAlignmentPadding, verticalAlignmentPadding);

			Color originalFg = gc.getForeground();
			gc.setForeground(GUIHelper.COLOR_WIDGET_BORDER);
			gc.drawRectangle(0, 0, iconCanvasBounds.width - 1, iconCanvasBounds.height - 1);
			gc.setForeground(originalFg);
		});

		iconCanvas.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				if (dropdownShell != null && !dropdownShell.isDisposed()) {
					if (dropdownShell.isVisible()) {
						text.forceFocus();
						hideDropdownControl();
					} else {
						showDropdownControl();
					}
				} else {
					showDropdownControl();
				}
			}
		});

	}

	@Override
	protected void createDropdownControl(int style) {
		this.dropdownShell = new Shell(getShell(), SWT.MODELESS);

		// (SWT.V_SCROLL | SWT.NO_SCROLL) prevents appearance of unnecessary
		// horizontal scrollbar on mac
		// see: https://bugs.eclipse.org/bugs/show_bug.cgi?id=304128
		int scrollStyle = ((this.itemList != null && this.itemList.size() > this.maxVisibleItems) && this.maxVisibleItems > 0) ? (SWT.V_SCROLL | SWT.NO_SCROLL)
				: SWT.NO_SCROLL;
		int dropdownListStyle = style | scrollStyle | HorizontalAlignmentEnum.getSWTStyle(this.cellStyle) | SWT.FULL_SELECTION;

		this.dropdownTable = new Table(this.dropdownShell, dropdownListStyle);
		this.dropdownTableViewer = new TableViewer(this.dropdownTable);
		this.dropdownTable.setBackground(this.cellStyle.getAttributeValue(CellStyleAttributes.BACKGROUND_COLOR));
		this.dropdownTable.setForeground(this.cellStyle.getAttributeValue(CellStyleAttributes.FOREGROUND_COLOR));
		this.dropdownTable.setFont(this.cellStyle.getAttributeValue(CellStyleAttributes.FONT));

		// add a column to be able to resize the item width in the dropdown
		new TableColumn(this.dropdownTable, SWT.NONE);

		this.dropdownTableViewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

			@Override
			public void dispose() {}

			@Override
			public Object[] getElements(Object inputElement) {
				return (Object[]) inputElement;
			}
		});

		this.dropdownTableViewer.setLabelProvider(new ILabelProvider() {

			@Override
			public void removeListener(ILabelProviderListener listener) {}

			@Override
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			@Override
			public void dispose() {}

			@Override
			public void addListener(ILabelProviderListener listener) {}

			@Override
			public String getText(Object element) {
				return element.toString();
			}

			@Override
			public Image getImage(Object element) {
				return null;
			}
		});

		this.dropdownTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean selected = e.detail != SWT.CHECK;
				boolean isCtrlPressed = (e.stateMask & SWT.MODIFIER_MASK) == SWT.CTRL;
				TableItem chosenItem = (TableItem) e.item;

				// Given the ability to filter we need to find the item's
				// table index which may not match the index in the itemList
				int itemTableIndex = dropdownTable.indexOf(chosenItem);

				// This case handles check actions
				if (!selected) {
					if (!chosenItem.getChecked()) {
						selectionStateMap.put(chosenItem.getText(), Boolean.FALSE);
					} else {
						selectionStateMap.put(chosenItem.getText(), Boolean.TRUE);
					}
				} else if (!useCheckbox) {
					if (multiselect && isCtrlPressed) {
						boolean isSelected = dropdownTable.isSelected(itemTableIndex);
						selectionStateMap.put(chosenItem.getText(), isSelected);
					} else {
						// A single item was selected. Clear all previous state
						for (String item : itemList) {
							selectionStateMap.put(item, Boolean.FALSE);
						}

						// Set the state for the selected item
						selectionStateMap.put(chosenItem.getText(), Boolean.TRUE);
					}
				}

				updateTextControl(false);
			}
		});

		this.dropdownTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				if ((event.keyCode == SWT.CR) || (event.keyCode == SWT.KEYPAD_CR)) {
					updateTextControl(true);
				} else if (event.keyCode == SWT.F2 && freeEdit) {
					text.forceFocus();
					hideDropdownControl();
				}
			}
		});

		this.dropdownTable.addFocusListener(new FocusListenerWrapper());

		FormLayout layout = new FormLayout();
		layout.spacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		this.dropdownShell.setLayout(layout);

		FormData dropDownLayoutData = new FormData();
		dropDownLayoutData.left = new FormAttachment(0);
		dropDownLayoutData.right = new FormAttachment(100);
		dropDownLayoutData.bottom = new FormAttachment(100);

		FormData data = new FormData();
		data.top = new FormAttachment(0);
		data.left = new FormAttachment(0);
		data.right = new FormAttachment(100);

		dropDownLayoutData.top = new FormAttachment(this.dropdownShell, 0, SWT.TOP);
		this.dropdownTable.setLayoutData(dropDownLayoutData);

		ViewerFilter viewerFilter = new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof String) {
					WildcardMatcher matcher = new WildcardMatcher(text.getText().toLowerCase());
					return matcher.matches(((String) element).toLowerCase());
				}
				return false;
			}
		};
		this.dropdownTableViewer.addFilter(viewerFilter);

		if (this.itemList != null) {
			setItems(this.itemList.toArray(new String[] {}));
		}

		// apply the listeners that were registered before the creation of the dropdown control
		applyDropdownListener();

		setDropdownSelection(getTextAsArray());
	}

	public String getTextValue() {
		return text.getText();
	}
}
