package aero.minova.rcp.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.osgi.service.component.annotations.Component;

import aero.minova.rcp.model.todo.Todo;
import aero.minova.rcp.model.todo.TodoService;
import aero.minova.rcp.model.tree.TreeItem;

@Component(service = TodoService.class)
public class TodoServiceImpl implements TodoService {
	private static AtomicInteger current = new AtomicInteger(1);

	public List<TreeItem<Todo>> getSampleTodoTreeItems() {
		List<TreeItem<Todo>> treeItems = new ArrayList<>();

		TreeItem<Todo> javaTodo = createTodoTreeItem("Java", "Java Group");
		javaTodo.add(createTodoTreeItem("RxJava", "RxJava for reactive development"));
		javaTodo.add(createTodoTreeItem("Retrofit", "Retrofit as rest client"));

		TreeItem<Todo> eclipseTodo = createTodoTreeItem("Eclipse", "Eclipse Group");

		eclipseTodo.add(createTodoTreeItem("Application model", "Flexible and extensible"));
		eclipseTodo.add(createTodoTreeItem("DI", "@Inject as programming mode"));
		eclipseTodo.add(createTodoTreeItem("OSGi", "Services"));
		eclipseTodo.add(createTodoTreeItem("SWT", "Widgets"));
		eclipseTodo.add(createTodoTreeItem("JFace", "Especially Viewers!"));
		eclipseTodo.add(createTodoTreeItem("CSS Styling", "Style your application"));
		eclipseTodo.add(createTodoTreeItem("Eclipse services", "Selection, model, Part"));
		eclipseTodo.add(createTodoTreeItem("Renderer", "Different UI toolkit"));

		treeItems.add(javaTodo);
		javaTodo.forEach(treeItems::add);
		treeItems.add(eclipseTodo);
		eclipseTodo.forEach(treeItems::add);

		return treeItems;
	}

	private static TreeItem<Todo> createTodoTreeItem(String summary, String description) {
		return new TreeItem<Todo>(new Todo(current.getAndIncrement(), summary, description));
	}

}
