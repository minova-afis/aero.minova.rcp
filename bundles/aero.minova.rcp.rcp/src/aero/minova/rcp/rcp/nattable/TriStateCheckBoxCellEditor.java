package aero.minova.rcp.rcp.nattable;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.edit.editor.AbstractCellEditor;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.MoveDirectionEnum;
import org.eclipse.nebula.widgets.nattable.widget.EditModeEnum;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class TriStateCheckBoxCellEditor extends AbstractCellEditor {

	private Boolean checked;
	private Canvas canvas;

	@Override
	public Boolean getEditorValue() {
		return this.checked;
	}

	@Override
	public void setEditorValue(Object value) {
		if (value instanceof Boolean) {
			this.checked = (Boolean) value;
		} else {
			this.checked = null;
		}
	}

	@Override
	public Control getEditorControl() {
		return this.canvas;
	}

	@Override
	public Canvas createEditorControl(Composite parent) {
		final Canvas canvas = new Canvas(parent, SWT.NONE);

		canvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (TriStateCheckBoxCellEditor.this.checked == null) {
					TriStateCheckBoxCellEditor.this.checked = true;
				} else if (TriStateCheckBoxCellEditor.this.checked) {
					TriStateCheckBoxCellEditor.this.checked = false;
				} else {
					TriStateCheckBoxCellEditor.this.checked = null;
				}
				canvas.redraw();
			}
		});

		return canvas;
	}

	@Override
	protected Control activateCell(Composite parent, Object originalCanonicalValue) {
		// if this editor was activated by clicking a letter or digit key, do
		// nothing
		if (originalCanonicalValue instanceof Character) {
			return null;
		}
		if ("".equals(originalCanonicalValue)) {
			originalCanonicalValue = null;
		}
		setCanonicalValue(originalCanonicalValue);

		// null --> ja --> nein --> null --> ...
		if (checked == null) {
			checked = true;
		} else if (checked) {
			checked = false;
		} else {
			checked = null;
		}
		this.canvas = createEditorControl(parent);
		commit(MoveDirectionEnum.NONE, false);
		if (this.editMode == EditModeEnum.INLINE) {
			// Close editor so it will react to subsequent clicks on the cell
			if (this.canvas != null && !this.canvas.isDisposed()) {
				close();
			}
		}
		return this.canvas;
	}

	@Override
	public boolean openMultiEditDialog() {
		// as it doesn't make sense to open a subdialog for checkbox multi
		// editing, this is not supported
		return false;
	}

	@Override
	public boolean activateAtAnyPosition() {
		// as the checkbox should only change its value if the icon that
		// represents the checkbox is clicked, this method needs to return
		// false so the IMouseEventMatcher can react on that.
		// Note that on return false here creates the need to add a special
		// matcher for this editor
		// to be activated.
		return false;
	}

	@Override
	public boolean activateOnTraversal(IConfigRegistry configRegistry, List<String> configLabels) {
		// the checkbox editor is immediately changing the value and closing
		// the again on activation. on tab traversal it is not intended that the
		// value changes therefore this editor is not activated on traversal
		return false;
	}

	@Override
	public Object getCanonicalValue() {
		if (checked == null) {
			return null;
		} else {
			return Boolean.valueOf(checked.toString());
		}
	}
}
