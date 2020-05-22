package aero.minova.rcp.rcp.handlers;

import java.util.Date;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import aero.minova.rcp.tasks.model.ITodoService;
import aero.minova.rcp.tasks.model.Todo;
import aero.minova.rcp.tasks.wizard.TodoWizard;

public class NewTodoHandler {

	@Execute
	public void execute(Shell shell, ITodoService todoService) {
		// use -1 to indicate a not existing id
		Todo todo = new Todo(-1);
		todo.setDueDate(new Date());
		WizardDialog dialog = new WizardDialog(shell, new TodoWizard(todo));
		if (dialog.open() == WizardDialog.OK) {
			// call service to save Todo object
			todoService.saveTodo(todo);
		}

	}

}
