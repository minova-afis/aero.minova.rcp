package aero.minova.rcp.rcp.nattable;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
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

	private GridLookupContentProvider contentProvider;

	/**
	 * Create a new single selection {@link MinovaComboBoxCellEditor} based on the given list of items, showing the default number of items in the dropdown of
	 * the combo.
	 */
	public MinovaComboBoxCellEditor(GridLookupContentProvider contentProvider) {
		super(contentProvider.getValues(), NatCombo.DEFAULT_NUM_OF_VISIBLE_ITEMS);
		this.contentProvider = contentProvider;
	}

	@Override
	public boolean commit(MoveDirectionEnum direction, boolean closeAfterCommit, boolean skipValidation) {
		boolean commited = super.commit(direction, closeAfterCommit, skipValidation);
		parent.forceFocus();
		return commited;
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
					EHandlerService handlerService = (EHandlerService) combo.getParent().getData("EHandlerService");
					ECommandService commandService = (ECommandService) combo.getParent().getData("ECommandService");
					commit(MoveDirectionEnum.NONE, true);
					Map<String, String> parameter = new HashMap<>();
					ParameterizedCommand command = commandService.createCommand("aero.minova.rcp.rcp.command.traverseenter", parameter);
					handlerService.executeHandler(command);
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

		// Bei Klick auf den Pfeil Lookup Content aktualisieren (Zelle muss deaktiviert werden damit neue Werte angezeigt werden)
		Control arrow = combo.getChildren()[1];
		arrow.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				contentProvider.update();
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
