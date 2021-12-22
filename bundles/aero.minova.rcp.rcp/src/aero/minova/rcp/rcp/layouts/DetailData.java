package aero.minova.rcp.rcp.layouts;

import org.eclipse.swt.SWT;

public final class DetailData {
	protected int left = 0;
	protected int width = SWT.DEFAULT;
	protected int top = 0;
	protected int height = SWT.DEFAULT;
	protected int column = 0;
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
	public int order = -1;
	/**
	 * true, if this control is visible and must be layouted
	 */
	public boolean visible = true;
	/**
	 * true, if this control should use the width of the parent control
	 */
	public boolean horizontalFill = false;

	@Override
	public String toString() {
		return "(c=" + column + ", l=" + left + ", w=" + width + ", t=" + top + ", h=" + height + ", exclude=" + visible + ", expand=" + horizontalFill + ")";
	}
}