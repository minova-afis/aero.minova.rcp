package aero.minova.rcp.rcp.data;

import java.io.Reader;
import java.util.Iterator;

import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;
import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonDataProvider implements IRowDataProvider<JsonElement> {

	private JsonArray jsonArray;
	private IColumnAccessor<JsonElement> columnAccessor;

	public JsonDataProvider(Reader reader, IColumnAccessor<JsonElement> columnAccessor) {
		this(new JsonParser().parse(reader).getAsJsonArray(), columnAccessor);
	}

	public JsonDataProvider(JsonArray jsonArray, IColumnAccessor<JsonElement> columnAccessor) {
		this.jsonArray = jsonArray;
		this.columnAccessor = columnAccessor;
	}

	@Override
	public int getColumnCount() {
		return this.columnAccessor.getColumnCount();
	}

	@Override
	public int getRowCount() {
		return this.jsonArray.size();
	}

	@Override
	public Object getDataValue(int columnIndex, int rowIndex) {
		JsonElement rowObj = this.jsonArray.get(rowIndex);
		return this.columnAccessor.getDataValue(rowObj, columnIndex);
	}

	@Override
	public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
		JsonElement rowObj = this.jsonArray.get(rowIndex);
		this.columnAccessor.setDataValue(rowObj, columnIndex, newValue);
	}

	@Override
	public JsonElement getRowObject(int rowIndex) {
		return this.jsonArray.get(rowIndex);
	}

	@Override
	public int indexOfRowObject(JsonElement rowObject) {
		int index = 0;
		for (Iterator<JsonElement> iterator = jsonArray.iterator(); iterator.hasNext();) {
			JsonElement element = iterator.next();
			index++;
			if (rowObject.equals(element)) {
				return index;
			}
		}

		return -1;
	}
}