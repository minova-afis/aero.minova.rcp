package aero.minova.rcp.tasks.ui.parts;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.databinding.beans.typed.BeanProperties;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.databinding.viewers.ViewerSupport;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.tasks.model.ITodoService;
import aero.minova.rcp.tasks.model.Todo;

public class TodoOverviewPart {

	@Inject
	ITodoService todoService;

	private Button btnLoadData;
	private TableViewer viewer;

	private WritableList<Todo> writableList;

	protected String searchString = "";

	@PostConstruct
	public void createControls(Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		btnLoadData = new Button(parent, SWT.PUSH);
		btnLoadData.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnLoadData.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// pass in updateViewer method as Consumer
				todoService.getTodos(TodoOverviewPart.this::updateViewer);
			};
		});
		btnLoadData.setText("Load Data");

		Button selectAllButton = new Button(parent, SWT.PUSH);
		selectAllButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		selectAllButton.setText("Select All");
		selectAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				viewer.setSelection(new StructuredSelection((List<?>) viewer.getInput()));
			}
		});

		Text search = new Text(parent, SWT.SEARCH | SWT.CANCEL | SWT.ICON_SEARCH);

		// assuming that GridLayout is used
		search.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		search.setMessage("Filter");

		// filter at every keystroke
		search.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				Text source = (Text) e.getSource();
				searchString = source.getText();
				// trigger update in the viewer
				viewer.refresh();
			}
		});

		// SWT.SEARCH | SWT.CANCEL is not supported under Windows7 and
		// so the following SelectionListener will not work under Windows7
		search.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				if (e.detail == SWT.CANCEL) {
					Text text = (Text) e.getSource();
					text.setText("");
					//
				}
			}
		});

		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		Table table = viewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);

		column.getColumn().setWidth(100);
		column.getColumn().setText("Summary");
		column = new TableViewerColumn(viewer, SWT.NONE);

		column.getColumn().setWidth(100);
		column.getColumn().setText("Description");

		// add a filter which will search in the summary and description field
		viewer.addFilter(new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				Todo todo = (Todo) element;
				return todo.getSummary().contains(searchString) || todo.getDescription().contains(searchString);
			}
		});

		viewer.setComparator(new ViewerComparator() {
			public int compare(Viewer viewer, Object e1, Object e2) {
				Todo t1 = (Todo) e1;
				Todo t2 = (Todo) e2;
				return t1.getDueDate().compareTo(t2.getDueDate());
			};
		});
		writableList = new WritableList<>();
		// fill the writable list, when Consumer callback is called. Databinding
		// will do the rest once the list is filled
		todoService.getTodos(writableList::addAll);
		ViewerSupport.bind(viewer, writableList,
				BeanProperties.values(new String[] { Todo.FIELD_SUMMARY, Todo.FIELD_DESCRIPTION }));
	}

	public void updateViewer(List<Todo> list) {
		if (viewer != null) {
			writableList.clear();
			writableList.addAll(list);
		}
	}

	@Focus
	public void setFocus() {
		viewer.getControl().setFocus();
	}

}
