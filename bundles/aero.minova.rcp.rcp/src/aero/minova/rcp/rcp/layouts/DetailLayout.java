package aero.minova.rcp.rcp.layouts;

import static java.lang.Math.max;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

public class DetailLayout extends Layout {
	/**
	 * spacing specifies the number of points between the edge of one cell and the edge of its neighbouring cell. The default value is 3.
	 */
	public int spacing = 5;
	/**
	 * marginLeft specifies the number of points of horizontal margin that will be placed along the left edge of the layout. The default value is 3.
	 */
	public int marginLeft = 3;

	/**
	 * marginTop specifies the number of points of vertical margin that will be placed along the top edge of the layout. The default value is 3.
	 */
	public int marginTop = 3;

	/**
	 * marginRight specifies the number of points of horizontal margin that will be placed along the right edge of the layout. The default value is 3.
	 */
	public int marginRight = 3;

	/**
	 * marginBottom specifies the number of points of vertical margin that will be placed along the bottom edge of the layout. The default value is 3.
	 */
	public int marginBottom = 3;

	@Override
	protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
		Composite parent = composite.getParent();
		Point extent = layout(composite, false, (parent instanceof ScrolledComposite) ? parent.getClientArea().width : composite.getClientArea().width,
				flushCache);
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
		layout(composite, true, clientArea.width, flushCache);
	}

	private Point layout(Composite composite, boolean move, int width, boolean flushCache) {
		Control[] children = composite.getChildren();
		Control[] columnChildren = new Control[children.length];
		Control[] horizontalFillChildren = new Control[children.length];
		DetailData[] columnData = new DetailData[children.length];
		DetailData[] horizontalFillData = new DetailData[children.length];
		int columnChildrenCount = 0;
		int horizontalFillChildrenCount = 0;
		int maxColumnWidth = 0;

		for (int i = 0; i < children.length; i++) {
			Control control = children[i];
			DetailData data = (DetailData) control.getLayoutData();
			if (data != null && data.visible && !data.horizontalFill) {
				Point size = control.computeSize(SWT.DEFAULT, SWT.DEFAULT, flushCache);
				initData(data, size);
				columnChildren[columnChildrenCount] = children[i];
				columnData[columnChildrenCount] = data;
				columnChildrenCount++;
				maxColumnWidth = Math.max(maxColumnWidth, size.x);
			} else {
				Point size = control.computeSize(SWT.DEFAULT, SWT.DEFAULT, flushCache);
				initData(data, size);
				horizontalFillChildren[horizontalFillChildrenCount] = children[i];
				horizontalFillData[horizontalFillChildrenCount] = data;
				horizontalFillChildrenCount++;
			}
		}

		Point size;
		size = layoutColumn(columnData, columnChildrenCount, width);
		size = layoutHorizontalFill(horizontalFillData, horizontalFillChildrenCount, size, width);

		if (move) {
			move(children);
		}

		return size;
	}

	private void initData(DetailData data, Point size) {
		data.column = 0;
		data.width = size.x;
		data.height = size.y;
		data.top = 0;
		data.left = 0;
	}

	private Point layoutColumn(DetailData[] detailData, int size, int width) {
		int totalHeight = 0;
		int maxWidth = 0;
		for (int i = 0; i < size; i++) {
			totalHeight += detailData[i].height;
			maxWidth = max(detailData[i].width, maxWidth);
		}

		// Spaltenbreite anhand der breitesten Spalte ermitteln
		int columns = max(1, (width - marginLeft - marginRight - spacing) / maxWidth);
		columns = max(1, (width - marginLeft - marginRight - spacing * (columns - 1)) / maxWidth);

		int averageHeight = totalHeight / columns + 1;
		int lastBottonPosition = 0;
		int column = 0;
		for (int i = 0; i < size; i++) {
			DetailData lv = detailData[i];
			if (lv.height / 2 + lastBottonPosition <= averageHeight || column == columns - 1) {
				lv.column = column;
				lv.top = lastBottonPosition;
				lastBottonPosition += lv.height;
			} else {
				column++;
				lv.column = column;
				lv.top = 0;
				lastBottonPosition = lv.height;
			}
			totalHeight += detailData[i].height;
		}
		maxWidth = 0;
		int left = marginLeft;
		int top = marginTop;
		int columnWidth = 0;
		int maxHeight = 0;
		int maxHeightElementCount = 0;
		int columnHeight = 0;
		int elementCount = 0;
		column = 0;
		for (int i = 0; i < size; i++) {
			DetailData lv = detailData[i];
			if (lv.column != column) {
				column++;
				left += columnWidth + spacing;
				top = marginTop;
				maxWidth += columnWidth;
				columnWidth = 0;
				columnHeight = 0;
				elementCount = 0;
			}
			lv.left = left;
			lv.top = top;
			top += lv.height + spacing;
			columnWidth = max(lv.width, columnWidth);
			columnHeight += lv.height;
			elementCount++;
			if (maxHeight < columnHeight || (maxHeight == columnHeight && elementCount > maxHeightElementCount)) {
				maxHeight = max(columnHeight, maxHeight);
				maxHeightElementCount = elementCount;
			}
		}
		maxWidth += columnWidth;
		maxWidth += marginLeft + (column) * spacing + marginRight;
		maxHeight += marginTop + (maxHeightElementCount - 1) * spacing + marginBottom;
		return new Point(maxWidth, maxHeight);
	}

	private Point layoutHorizontalFill(DetailData[] horizontalFillData, int horizontalFillChildrenCount, Point size, int parentWidth) {
		int width = max(size.x, parentWidth) - marginLeft - marginRight;
		int top = size.y - marginBottom;
		for (int i = 0; i < horizontalFillChildrenCount; i++) {
			DetailData dd = horizontalFillData[i];
			dd.left = marginLeft;
			dd.width = width;
			dd.top = top + spacing;
			top += dd.height + spacing;
		}
		return new Point(top + marginBottom, width + marginLeft + marginRight);
	}

	private void move(Control[] controls) {
		for (int i = 0; i < controls.length; i++) {
			Control control = controls[i];
			DetailData detailData = (DetailData) control.getLayoutData();
			control.setBounds(detailData.left, detailData.top, detailData.width, detailData.height);
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
		if (marginLeft != 0)
			string += "marginLeft=" + marginLeft + " ";
		if (marginTop != 0)
			string += "marginTop=" + marginTop + " ";
		if (marginRight != 0)
			string += "marginRight=" + marginRight + " ";
		if (marginBottom != 0)
			string += "marginBottom=" + marginBottom + " ";
		if (spacing != 0)
			string += "spacing=" + spacing + " ";
		string = string.trim();
		string += "}";
		return string;
	}
}
