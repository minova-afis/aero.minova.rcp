package aero.minova.rcp.rcp.parts;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
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

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import aero.minova.rcp.rcp.data.JsonColumnAccessor;
import aero.minova.rcp.rcp.data.JsonColumnHeaderDataProvider;
import aero.minova.rcp.rcp.data.JsonDataProvider;

public class GsonPart {

	NatTable table = null;
	Composite parent = null;
	String filename = null;
	private MPart part;

	@PostConstruct
	public void postConstruct(Composite parent, MPart part) throws IOException {
		this.parent = parent;
		this.part = part;
		checkTable();
	}

	@Inject
	public void setFilename(@Optional @Named("JSONFilename") String filename, EPartService partService)
			throws UnsupportedEncodingException, IOException {
		if (filename == null)
			return;
		else
			this.filename = filename;
		if (checkTable()) {
			partService.activate(part);
		}
	}

	private boolean checkTable() throws IOException, UnsupportedEncodingException {
		if (filename == null || parent == null)
			return false;
		if (table != null) {
			table.dispose();
		}

		Reader reader = getJsonTestReader(filename);

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

		table = new NatTable(parent, compositeLayer);
		parent.requestLayout();
		return true;
	}

	private Reader getJsonTestReader(String filename) throws IOException, UnsupportedEncodingException {
		FileInputStream fis = new FileInputStream(filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
		return br;
	}

}