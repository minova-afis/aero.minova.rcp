package aero.minova.rcp.css.widgets;

import static java.lang.Math.max;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

import aero.minova.rcp.css.ICssStyler;

public class DetailLayout extends Layout {
	/**
	 * spacing specifies the number of points between the edge of one cell and the edge of its neighbouring cell. The default value is 5.
	 */
	public static final int SPACING = 5;
	/**
	 * marginLeft specifies the number of points of horizontal margin that will be placed along the left edge of the layout. The default value is 3.
	 */
	public static final int MARGIN_LEFT = 3;

	/**
	 * marginTop specifies the number of points of vertical margin that will be placed along the top edge of the layout. The default value is 3.
	 */
	public static final int MARGIN_TOP = 3;

	/**
	 * marginRight specifies the number of points of horizontal margin that will be placed along the right edge of the layout. The default value is 3.
	 */
	public static final int MARGIN_RIGHT = 3;

	/**
	 * marginBottom specifies the number of points of vertical margin that will be placed along the bottom edge of the layout. The default value is 3.
	 */
	public static final int MARGIN_BOTTOM = 3;

	@Override
	protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
		Composite parent = composite.getParent();
		Point extent = layout(composite, false, (parent instanceof ScrolledComposite) ? parent.getClientArea().width : composite.getClientArea().width,
				flushCache, true);
		if (wHint != SWT.DEFAULT)
			extent.x = wHint;
		if (hHint != SWT.DEFAULT)
			extent.y = hHint;
		return extent;
	}

	@Override
	protected boolean flushCache(Control control) {
		return true;
	}

	@Override
	protected void layout(Composite composite, boolean flushCache) {
		Rectangle clientArea = composite.getClientArea();
		layout(composite, true, clientArea.width, flushCache, true);
	}

	public Point layout(Composite composite, boolean move, int width, boolean flushCache, boolean calculateHorizontal) {
		Control[] children = composite.getChildren();
		Control[] columnChildren = new Control[children.length];
		Control[] horizontalFillChildren = new Control[children.length];
		MinovaSectionData[] columnData = new MinovaSectionData[children.length];
		MinovaSectionData[] horizontalFillData = new MinovaSectionData[children.length];
		int columnChildrenCount = 0;
		int horizontalFillChildrenCount = 0;
		int maxColumnWidth = 0;

		for (int i = 0; i < children.length; i++) {
			Control control = children[i];
			ICssStyler styler = ((MinovaSection) control).getCssStyler();
			MinovaSectionData data = (MinovaSectionData) control.getLayoutData();
			if (data != null && data.isVisible() && !data.isHorizontalFill()) {
				Point size = control.computeSize(styler.getSectionWidth(), SWT.DEFAULT, flushCache);
				initData(data, size);
				columnChildren[columnChildrenCount] = children[i];
				columnData[columnChildrenCount] = data;
				columnChildrenCount++;
				maxColumnWidth = Math.max(maxColumnWidth, size.x);
			} else if (data != null && data.isVisible()) {
				Point size = control.computeSize(SWT.DEFAULT, SWT.DEFAULT, flushCache);
				initData(data, size);
				horizontalFillChildren[horizontalFillChildrenCount] = children[i];
				horizontalFillData[horizontalFillChildrenCount] = data;
				horizontalFillChildrenCount++;
			}
		}

		Point size;
		size = layoutColumn(columnData, columnChildrenCount, width);
		if (calculateHorizontal) {
			size = layoutHorizontalFill(horizontalFillData, horizontalFillChildrenCount, size, width);
		}

		if (move) {
			move(children);
		}

		return size;
	}

	private void initData(MinovaSectionData data, Point size) {
		data.setColumn(0);
		data.setWidth(size.x);
		data.setHeight(size.y);
		data.setTop(0);
		data.setLeft(0);
	}

	private Point layoutColumn(MinovaSectionData[] detailData, int size, int width) {
		// Spaltenbreite anhand der breitesten Spalte ermitteln
		int totalHeight = 0;
		int maxWidth = 0;
		for (int i = 0; i < size; i++) {
			totalHeight += detailData[i].getHeight();
			maxWidth = max(detailData[i].getWidth(), maxWidth);
		}

		// Spaltenanzahl ermitteln
		int columns = max(1, (width - MARGIN_LEFT - MARGIN_RIGHT - SPACING) / maxWidth);
		columns = max(1, (width - MARGIN_LEFT - MARGIN_RIGHT - SPACING * (columns - 1)) / maxWidth);

		int averageHeight = totalHeight / columns + 1;
		int lastBottomPosition = 0;
		int column = 0;
		for (int i = 0; i < size; i++) {
			MinovaSectionData lv = detailData[i];
			if (lv.getHeight() / 2 + lastBottomPosition <= averageHeight || column == columns - 1) {
				lv.setColumn(column);
				lv.setTop(lastBottomPosition);
				lastBottomPosition += lv.getHeight();
			} else {
				column++;
				lv.setColumn(column);
				lv.setTop(0);
				lastBottomPosition = lv.getHeight();
			}
		}
		maxWidth = 0;
		int left = MARGIN_LEFT;
		int top = MARGIN_TOP;
		int columnWidth = 0;
		int maxHeight = 0;
		int maxHeightElementCount = 0;
		int columnHeight = 0;
		int elementCount = 0;
		column = 0;
		for (int i = 0; i < size; i++) {
			MinovaSectionData lv = detailData[i];
			if (lv.getColumn() != column) {
				column++;
				left += columnWidth + SPACING;
				top = MARGIN_TOP;
				maxWidth += columnWidth;
				columnWidth = 0;
				columnHeight = 0;
				elementCount = 0;
			}
			lv.setLeft(left);
			lv.setTop(top);
			top += lv.getHeight() + SPACING;
			columnWidth = max(lv.getWidth(), columnWidth);
			columnHeight += lv.getHeight();
			elementCount++;
			if (maxHeight < columnHeight || (maxHeight == columnHeight && elementCount > maxHeightElementCount)) {
				maxHeight = max(columnHeight, maxHeight);
				maxHeightElementCount = elementCount;
			}
		}
		maxWidth += columnWidth;
		maxWidth += MARGIN_LEFT + (column) * SPACING + MARGIN_RIGHT;
		maxHeight += MARGIN_TOP + (maxHeightElementCount - 1) * SPACING + MARGIN_BOTTOM;
		return new Point(maxWidth, maxHeight);
	}

	private Point layoutHorizontalFill(MinovaSectionData[] horizontalFillData, int horizontalFillChildrenCount, Point size, int parentWidth) {
		int width = max(size.x, parentWidth) - MARGIN_LEFT - MARGIN_RIGHT;
		int top = size.y - MARGIN_BOTTOM;
		for (int i = 0; i < horizontalFillChildrenCount; i++) {
			MinovaSectionData dd = horizontalFillData[i];
			dd.setLeft(MARGIN_LEFT);
			dd.setWidth(width);
			dd.setTop(top + SPACING);
			top += dd.getHeight() + SPACING;
		}
		return new Point(width + MARGIN_LEFT + MARGIN_RIGHT, top + MARGIN_BOTTOM);
	}

	private void move(Control[] controls) {
		for (int i = 0; i < controls.length; i++) {
			Control control = controls[i];
			MinovaSectionData detailData = (MinovaSectionData) control.getLayoutData();
			control.setBounds(detailData.getLeft(), detailData.getTop(), detailData.getWidth(), detailData.getHeight());
		}
	}

	/**
	 * Returns a string containing a concise, human-readable description of the receiver.
	 *
	 * @return a string representation of the layout
	 */
	@Override
	public String toString() {
		String string = "DetailLayout {";
		if (MARGIN_LEFT != 0)
			string += "marginLeft=" + MARGIN_LEFT + " ";
		if (MARGIN_TOP != 0)
			string += "marginTop=" + MARGIN_TOP + " ";
		if (MARGIN_RIGHT != 0)
			string += "marginRight=" + MARGIN_RIGHT + " ";
		if (MARGIN_BOTTOM != 0)
			string += "marginBottom=" + MARGIN_BOTTOM + " ";
		if (SPACING != 0)
			string += "spacing=" + SPACING + " ";
		string = string.trim();
		string += "}";
		return string;
	}
}
