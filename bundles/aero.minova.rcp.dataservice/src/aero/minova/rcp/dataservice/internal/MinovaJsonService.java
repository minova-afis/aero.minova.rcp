package aero.minova.rcp.dataservice.internal;

import org.osgi.service.component.annotations.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import aero.minova.rcp.dataservice.IMinovaJsonService;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.ValueDeserializer;
import aero.minova.rcp.model.ValueSerializer;

@Component
public class MinovaJsonService implements IMinovaJsonService {

	private Gson gson;

	private Gson gson2;

	void init() {
		if (gson == null) {
			gson = new GsonBuilder() //
					.registerTypeAdapter(Value.class, new ValueSerializer()) //
					.registerTypeAdapter(Value.class, new ValueDeserializer()) //
					.setPrettyPrinting() //
					.create();
		}
		if (gson2 == null) {
			gson2 = new GsonBuilder() //
					.registerTypeAdapter(Value.class, new ValueSerializer(true)) //
					.registerTypeAdapter(Value.class, new ValueDeserializer(true)) //
					.setPrettyPrinting() //
					.create();
		}
	}

	@Override
	public String table2Json(Table t) {
		return table2Json(t, false);
	}

	@Override
	public String table2Json(Table t, boolean useUserValues) {
		init();
		return useUserValues ? gson2.toJson(t) : gson.toJson(t);
	}

	@Override
	public Table json2Table(String jsonString) {
		return json2Table(jsonString, false);
	}

	@Override
	public Table json2Table(String jsonString, boolean useUserValues) {
		init();
		return useUserValues ? gson2.fromJson(jsonString, Table.class) : gson.fromJson(jsonString, Table.class);
	}

}
