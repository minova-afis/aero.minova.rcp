package aero.minova.rcp.rcp.nattable;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.MoveDirectionEnum;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.widget.EditModeEnum;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

public class MinovaGridTextCellEditor extends MinovaTextCellEditor {

	public MinovaGridTextCellEditor() {
		super();
	}

	/**
	 * Creates a TextCellEditor that will not move the selection on committing a value by pressing enter.
	 *
	 * @param commitOnUpDown
	 *            Flag to configure whether the editor should commit and move the selection in the corresponding way if the up or down key is pressed.
	 */
	public MinovaGridTextCellEditor(boolean commitOnUpDown) {
		super(commitOnUpDown, false);
	}

	/**
	 * Creates a TextCellEditor that will not move the selection on pressing the left or right arrow keys on the according edges.
	 *
	 * @param commitOnUpDown
	 *            Flag to configure whether the editor should commit and move the selection in the corresponding way if the up or down key is pressed.
	 * @param moveSelectionOnEnter
	 *            Flag to configure whether the selection should move after a value was committed after pressing enter.
	 */
	public MinovaGridTextCellEditor(boolean commitOnUpDown, boolean moveSelectionOnEnter) {
		super(commitOnUpDown, moveSelectionOnEnter, false);
	}

	/**
	 * Creates a TextCellEditor.
	 *
	 * @param commitOnUpDown
	 *            Flag to configure whether the editor should commit and move the selection in the corresponding way if the up or down key is pressed.
	 * @param moveSelectionOnEnter
	 *            Flag to configure whether the selection should move after a value was committed after pressing enter.
	 * @param commitOnLeftRight
	 *            Flag to configure whether the editor should commit and move the selection in the corresponding way if the left or right key is pressed on the
	 *            according content edge.
	 * @since 1.4
	 */
	public MinovaGridTextCellEditor(boolean commitOnUpDown, boolean moveSelectionOnEnter, boolean commitOnLeftRight) {
		super(commitOnUpDown, moveSelectionOnEnter, commitOnLeftRight);
	}

	private EHandlerService getHandlerService(Control control) {
		return (EHandlerService) control.getParent().getData("EHandlerService");
	}

	private ECommandService getCommandService(Control control) {
		return (ECommandService) control.getParent().getData("ECommandService");
	}

	/**
	 * Creates the editor control that is wrapped by this ICellEditor. Will use the style configurations in ConfigRegistry for styling the control.
	 *
	 * @param parent
	 *            The Composite that will be the parent of the new editor control. Can not be <code>null</code>
	 * @param style
	 *            The SWT style of the text control to create.
	 * @return The created editor control that is wrapped by this ICellEditor.
	 */
	@Override
	protected Text createEditorControl(final Composite parent, int style) {
		// create the Text control based on the specified style
		final Text textControl = new Text(parent, style);

		// set style information configured in the associated cell style
		textControl.setBackground(this.cellStyle.getAttributeValue(CellStyleAttributes.BACKGROUND_COLOR));
		textControl.setForeground(this.cellStyle.getAttributeValue(CellStyleAttributes.FOREGROUND_COLOR));
		textControl.setFont(this.cellStyle.getAttributeValue(CellStyleAttributes.FONT));
		Cursor cursor = new Cursor(Display.getDefault(), SWT.CURSOR_IBEAM);
		textControl.setCursor(cursor);
		textControl.addDisposeListener((e) -> {
			if (cursor != null && !cursor.isDisposed()) {
				cursor.dispose();
			}
		});
		// add a key listener that will commit or close the editor for special
		// key strokes and executes conversion/validation on input to the editor
		textControl.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent event) {
				if (isCommitOnEnter() && (event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR) && event.stateMask == 0) {
					textControl.getParent().forceFocus();
					commit(MoveDirectionEnum.NONE, false);
					Map<String, String> parameter = new HashMap<>();
					ParameterizedCommand command = getCommandService(textControl).createCommand("aero.minova.rcp.rcp.command.traverseenter", parameter);
					EHandlerService handlerService = getHandlerService(textControl);
					handlerService.executeHandler(command);
					close();
				} else if (event.keyCode == SWT.ESC && event.stateMask == 0) {
					close();
				} else if ((commitOnUpDown || commitOnLeftRight) && editMode == EditModeEnum.INLINE) {

					Text control = (Text) event.widget;

					if (commitOnUpDown && event.keyCode == SWT.ARROW_UP) {
						commit(MoveDirectionEnum.UP);
					} else if (commitOnUpDown && event.keyCode == SWT.ARROW_DOWN) {
						commit(MoveDirectionEnum.DOWN);
					} else if (commitOnLeftRight && control.getSelectionCount() == 0 && event.keyCode == SWT.ARROW_LEFT && control.getCaretPosition() == 0) {
						commit(MoveDirectionEnum.LEFT);
					} else if (commitOnLeftRight && control.getSelectionCount() == 0 && event.keyCode == SWT.ARROW_RIGHT
							&& control.getCaretPosition() == control.getCharCount()) {
						commit(MoveDirectionEnum.RIGHT);
					}
				} 
			}

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					// always do the conversion
					Object canonicalValue = getCanonicalValue(inputConversionErrorHandler);
					// and always do the validation, even if for committing the
					// validation should be skipped, on editing
					// a validation failure should be made visible
					// otherwise there would be no need for validation!
					validateCanonicalValue(canonicalValue, inputValidationErrorHandler);
				} catch (Exception ex) {
					// do nothing as exceptions caused by conversion or
					// validation are handled already we just need this catch
					// block for stopping the process if conversion failed with
					// an exception
				}
			}
		});

		return textControl;
	}
}
