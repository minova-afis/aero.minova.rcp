package aero.minova.rcp.rcp.nattable;

import java.util.Map;

import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.CellStyleUtil;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.style.VerticalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.ui.matcher.LetterOrDigitKeyEventMatcher;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.nebula.widgets.nattable.widget.NatCombo;
import org.eclipse.nebula.widgets.nattable.widget.NatCombo.FocusListenerWrapper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class MinovaNatCombo extends NatCombo {

	public MinovaNatCombo(Composite parent, IStyle cellStyle, int style) {
		super(parent, cellStyle, style);
	}

	public MinovaNatCombo(Composite parent, IStyle cellStyle, int maxVisibleItems, int style, boolean showDropdownFilter) {
		super(parent, cellStyle, maxVisibleItems, style);
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
				if (event.keyCode == SWT.ARROW_DOWN || event.keyCode == SWT.ARROW_UP) {
					showDropdownControl();

//					int selectionIndex = getDropdownTable().getSelectionIndex();
//					if (selectionIndex < 0) {
//						select(0);
//					} else {
//						// only visualize the selection in the dropdown, do not
//						// perform a selection
//						getDropdownTable().select(selectionIndex);
//					}

					// ensure the arrow key events do not have any further
					// effect
//					event.doit = false;
				} else if (!LetterOrDigitKeyEventMatcher.isLetterOrDigit(event.character)) {
					if (freeEdit) {
						// simply clear the selection in dropdownlist so the
						// free value in text control will be used
						if (!getDropdownTable().isDisposed()) {
							getDropdownTable().deselectAll();
							for (Map.Entry<String, Boolean> entry : selectionStateMap.entrySet()) {
								entry.setValue(Boolean.FALSE);
							}
						}
					} else {
						showDropdownControl();
					}
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

		iconCanvas.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent event) {
				GC gc = event.gc;

				Rectangle iconCanvasBounds = iconCanvas.getBounds();
				Rectangle iconImageBounds = iconImage.getBounds();
				int horizontalAlignmentPadding = CellStyleUtil.getHorizontalAlignmentPadding(HorizontalAlignmentEnum.CENTER, iconCanvasBounds,
						iconImageBounds.width);
				int verticalAlignmentPadding = CellStyleUtil.getVerticalAlignmentPadding(VerticalAlignmentEnum.MIDDLE, iconCanvasBounds,
						iconImageBounds.height);
				gc.drawImage(iconImage, horizontalAlignmentPadding, verticalAlignmentPadding);

				Color originalFg = gc.getForeground();
				gc.setForeground(GUIHelper.COLOR_WIDGET_BORDER);
				gc.drawRectangle(0, 0, iconCanvasBounds.width - 1, iconCanvasBounds.height - 1);
				gc.setForeground(originalFg);
			}

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

}
