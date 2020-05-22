package aero.minova.rcp.tasks.wizard;

import java.util.Collections;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import aero.minova.rcp.tasks.model.Todo;
import aero.minova.rcp.tasks.ui.parts.TodoDetailsPart;

public class TodoWizardPage1 extends WizardPage{
	
    private Todo todo;

    public TodoWizardPage1(Todo todo) {
        super("page1");
        this.todo = todo;
        setTitle("New Todo");
        setDescription("Enter the todo data");
    }

    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        // usage of the new operator
        // NO automatic dependency injection
        TodoDetailsPart part = new TodoDetailsPart();
        part.createControls(container);
        part.setTodos(Collections.singletonList(todo));
        setPageComplete(true);
        setControl(container);
    }

	

}
