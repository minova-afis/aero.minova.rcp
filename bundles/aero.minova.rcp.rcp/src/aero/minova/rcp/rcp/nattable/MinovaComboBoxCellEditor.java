package aero.minova.rcp.rcp.nattable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.nebula.widgets.nattable.data.convert.IDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.editor.ComboBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.MoveDirectionEnum;
import org.eclipse.nebula.widgets.nattable.widget.EditModeEnum;
import org.eclipse.nebula.widgets.nattable.widget.NatCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Control;

public class MinovaComboBoxCellEditor extends ComboBoxCellEditor {

	/**
	 * Create a new single selection {@link MinovaComboBoxCellEditor} based on the given list of items, showing the default number of items in the dropdown of
	 * the combo.
	 *
	 * @param canonicalValues
	 *            Array of items to be shown in the drop down box. These will be converted using the {@link IDisplayConverter} for display purposes
	 */
	public MinovaComboBoxCellEditor(List<?> canonicalValues) {
		super(canonicalValues, NatCombo.DEFAULT_NUM_OF_VISIBLE_ITEMS);
	}

	private EHandlerService getHandlerService(Control control) {
		return (EHandlerService) control.getParent().getData("EHandlerService");
	}

	private ECommandService getCommandService(Control control) {
		return (ECommandService) control.getParent().getData("ECommandService");
	}

	/**
	 * Registers special listeners to the {@link NatCombo} regarding the {@link EditModeEnum}, that are needed to commit/close or change the visibility state of
	 * the {@link NatCombo} dependent on UI interactions.
	 *
	 * @param combo
	 *            The {@link NatCombo} to add the listeners to.
	 */
	@Override
	protected void addNatComboListener(final NatCombo combo) {
		combo.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent event) {
				if ((event.keyCode == SWT.CR) || (event.keyCode == SWT.KEYPAD_CR)) {
					combo.getParent().forceFocus();
					commit(MoveDirectionEnum.NONE, false);
					Map<String, String> parameter = new HashMap<>();
					ParameterizedCommand command = getCommandService(combo).createCommand("aero.minova.rcp.rcp.command.traverseenter", parameter);
					EHandlerService handlerService = getHandlerService(combo);
					handlerService.executeHandler(command);
					close();
				} else if (event.keyCode == SWT.TAB) {
					commit(MoveDirectionEnum.RIGHT, true);
				} else if (event.keyCode == SWT.ESC) {
					if (MinovaComboBoxCellEditor.this.editMode == EditModeEnum.INLINE) {
						close();
					} else {
						combo.hideDropdownControl();
					}
				}
			}

		});

		combo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				commit(MoveDirectionEnum.NONE, (!MinovaComboBoxCellEditor.this.multiselect && MinovaComboBoxCellEditor.this.editMode == EditModeEnum.INLINE));
				if (!MinovaComboBoxCellEditor.this.multiselect && MinovaComboBoxCellEditor.this.editMode == EditModeEnum.DIALOG) {
					// hide the dropdown after a value was selected in the combo
					// in a dialog
					combo.hideDropdownControl();
				}
			}
		});

		if (this.editMode == EditModeEnum.INLINE) {
			combo.addShellListener(new ShellAdapter() {
				@Override
				public void shellClosed(ShellEvent e) {
					close();
				}
			});
		}

		if (this.editMode == EditModeEnum.DIALOG) {
			combo.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {
					combo.hideDropdownControl();
				}
			});
		}
	}
}
