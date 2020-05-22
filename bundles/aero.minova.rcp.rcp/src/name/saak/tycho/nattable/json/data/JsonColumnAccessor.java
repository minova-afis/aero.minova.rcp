package aero.minova.rcp.nattable.json.data;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonColumnAccessor  implements IColumnPropertyAccessor<JsonElement> {
	
	private List<String> colums;
	
	public JsonColumnAccessor(JsonElement jsonElement) {
		JsonArray jsonArray = jsonElement.getAsJsonArray();
		JsonElement firstJsonEntry = jsonArray.get(0);
		JsonObject jsonObject = firstJsonEntry.getAsJsonObject();
		Set<Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
		
		colums = entrySet.stream().map(entry -> entry.getKey()).collect(Collectors.toList());
	}
	
	@Override
	public int getColumnCount() {
		return colums.size();
	}
	
	@Override
	public String getColumnProperty(int columnIndex) {
		return colums.get(columnIndex);
	}
	
	@Override
	public int getColumnIndex(String propertyName) {
		return colums.indexOf(propertyName);
	}
	
	@Override
	public Object getDataValue(JsonElement rowObject, int columnIndex) {
		return rowObject.getAsJsonObject().get(getColumnProperty(columnIndex));
	}
	
	@Override
	public void setDataValue(JsonElement rowObject, int columnIndex, Object newValue) {
		//TODO
	}
	

}
