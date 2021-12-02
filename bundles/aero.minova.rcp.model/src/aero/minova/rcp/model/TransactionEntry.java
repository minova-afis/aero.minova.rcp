package aero.minova.rcp.model;

/**
 * Ein Element einer Transaktion, mit ID und Tabelle
 * 
 * @param id
 * @param table
 */
public class TransactionEntry {

	private String id;
	private Table table;

	public TransactionEntry(String id, Table table) {
		this.setId(id);
		this.setTable(table);
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
