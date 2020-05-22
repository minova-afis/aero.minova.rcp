
package aero.minova.rcp.nattable.part;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import javax.annotation.PostConstruct;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import aero.minova.rcp.nattable.json.data.JsonColumnAccessor;
import aero.minova.rcp.nattable.json.data.JsonColumnHeaderDataProvider;
import aero.minova.rcp.nattable.json.data.JsonDataProvider;

public class NatTableGsonPart {

	@PostConstruct
	public void postConstruct(Composite parent) throws IOException {

		Reader reader = getJsonTestReader();

		JsonParser jsonParser = new JsonParser();

		JsonElement jsonElement = jsonParser.parse(reader);

		JsonDataProvider jsonDataProvider = new JsonDataProvider(jsonElement.getAsJsonArray(),
				new JsonColumnAccessor(jsonElement));

		DataLayer dataLayer = new DataLayer(jsonDataProvider);
		SelectionLayer selectionLayer = new SelectionLayer(dataLayer);
		ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);

		IDataProvider headerDataProvider = new JsonColumnHeaderDataProvider(jsonElement);
		DataLayer headerDataLayer = new DataLayer(headerDataProvider);
		ILayer columnHeaderLayer = new ColumnHeaderLayer(headerDataLayer, viewportLayer, selectionLayer);

		CompositeLayer compositeLayer = new CompositeLayer(1, 2);
		compositeLayer.setChildLayer(GridRegion.COLUMN_HEADER, columnHeaderLayer, 0, 0);
		compositeLayer.setChildLayer(GridRegion.BODY, viewportLayer, 0, 1);

		new NatTable(parent, compositeLayer);
	}

	private Reader getJsonTestReader() throws IOException, UnsupportedEncodingException {
		Bundle bundle = FrameworkUtil.getBundle(getClass());
		URL find = FileLocator.find(bundle, new Path("test.json"), null);
		InputStream is = find.openStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		return br;
	}

}