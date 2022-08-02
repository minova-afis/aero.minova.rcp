package aero.minova.rcp.css.widgets;

import org.eclipse.swt.SWT;

public final class MinovaSectionData {
	private int left = 0;
	private int width = SWT.DEFAULT;
	private int top = 0;
	private int height = SWT.DEFAULT;
	private int column = 0;
	/**
	 * defines the position of the control.
	 * <ul>
	 * <li>-1: undefined, will be done by the layout process</li>
	 * <li>0: defined first element. This element can't be changed</li>
	 * <li>1 ... : ordered following elements</li>
	 * </ul>
	 * All elements which are not visible ({@link #visible} will be ignored and not layouted. All horizontalFill ({@link #horizontalFill} elements are sorted at
	 * the end of all not horizontalFilled controls.
	 */
	private int order = -1;
	/**
	 * true, if this control is visible and must be layouted
	 */
	private boolean visible = true;
	/**
	 * true, if this control should use the width of the parent control
	 */
	private boolean horizontalFill = false;

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isHorizontalFill() {
		return horizontalFill;
	}

	public void setHorizontalFill(boolean horizontalFill) {
		this.horizontalFill = horizontalFill;
	}

	@Override
	public String toString() {
		return "(c=" + column + ", l=" + left + ", w=" + width + ", t=" + top + ", h=" + height + ", visible=" + visible + ", horizontalFill=" + horizontalFill
				+ ")";
	}
}
