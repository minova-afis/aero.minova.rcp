package aero.minova.rcp.rcp.data;

import java.io.Reader;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonColumnHeaderDataProvider implements IDataProvider {

	private List<String> colums;

	public JsonColumnHeaderDataProvider(Reader reader) {
		this(new JsonParser().parse(reader));
	}

	public JsonColumnHeaderDataProvider(JsonElement jsonArray) {
		JsonArray asJsonArray = jsonArray.getAsJsonArray();
		JsonObject jsonObject = asJsonArray.get(0).getAsJsonObject();
		Set<Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
		colums = entrySet.stream().map(entry -> entry.getKey()).collect(Collectors.toList());
	}

	@Override
	public int getColumnCount() {
		return this.colums.size();
	}

	@Override
	public int getRowCount() {
		return 1;
	}

	@Override
	public Object getDataValue(int columnIndex, int rowIndex) {
		return getColumnHeaderLabel(columnIndex);
	}

	private Object getColumnHeaderLabel(int columnIndex) {
		return colums.get(columnIndex);
	}

	@Override
	public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
		throw new UnsupportedOperationException();
	}

}