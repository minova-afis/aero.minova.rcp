package aero.minova.rcp.model.todo;

public class Todo {
	
	private int id;
	private String summary;
	private String description;

	public Todo(int id) {
		this(id, "", "");
	}

	public Todo(int id, String summary, String description) {
		this.id = id;
		this.summary = summary;
		this.description = description;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return getId() + " : " + getSummary();
	}
}
