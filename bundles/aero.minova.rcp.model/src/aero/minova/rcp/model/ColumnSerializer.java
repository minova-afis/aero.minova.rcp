package aero.minova.rcp.model;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ColumnSerializer implements JsonSerializer<Column> {

	@Override
	public JsonElement serialize(Column src, Type typeOfSrc, JsonSerializationContext context) {

		JsonObject o = new JsonObject();
		o.add("name", new JsonPrimitive(src.getName()));
		o.add("type", new JsonPrimitive(src.getType().toString()));
		if (src.getOutputType() != null) {
			o.add("outputType", new JsonPrimitive(src.getOutputType().toString()));
		}

		return o;
	}
}
