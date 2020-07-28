package aero.minova.rcp.dataservice.internal;

import org.osgi.service.component.annotations.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import aero.minova.rcp.dataservice.IMinovaJsonService;
import aero.minova.rcp.plugin1.model.Table;
import aero.minova.rcp.plugin1.model.Value;
import aero.minova.rcp.plugin1.model.ValueDeserializer;
import aero.minova.rcp.plugin1.model.ValueSerializer;

@Component
public class MinovaJsonService implements IMinovaJsonService {

	private Gson gson;

	void init() {
		if (gson == null) {
			gson = new GsonBuilder() //
					.registerTypeAdapter(Value.class, new ValueSerializer()) //
					.registerTypeAdapter(Value.class, new ValueDeserializer()) //
					.setPrettyPrinting() //
					.create();
		}
	}

	@Override
	public String table2Json(Table t) {
		init();
		String s = gson.toJson(t);
		return s;
	}

	@Override
	public Table json2Table(String jsonString) {
		init();
		return gson.fromJson(jsonString, Table.class);
	}

}
