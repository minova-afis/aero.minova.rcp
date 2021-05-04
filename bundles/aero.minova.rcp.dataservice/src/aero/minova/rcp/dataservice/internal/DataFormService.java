package aero.minova.rcp.dataservice.internal;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.xml.bind.JAXBException;

import org.eclipse.core.runtime.Platform;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dataservice.XmlProcessor;
import aero.minova.rcp.form.model.xsd.Column;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.form.model.xsd.Head;
import aero.minova.rcp.form.model.xsd.Page;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.OutputType;
import aero.minova.rcp.model.Table;

@Component
public class DataFormService implements IDataFormService {

	@Reference
	IDataService dataService;

	@Override
	public Table getTableFromFormIndex(Form form) {
		Table dataTable = new Table();
		dataTable.setName(form.getIndexView().getSource());
		for (Column c : form.getIndexView().getColumn()) {
			aero.minova.rcp.model.Column tableColumn = new aero.minova.rcp.model.Column(c.getName(), getDataType(c), OutputType.OUTPUT);
			tableColumn.setLabel(c.getLabel());

			dataTable.addColumn(tableColumn);
		}
		return dataTable;
	}

	@Override
	public Table getTableFromFormDetail(Form form, String prefix) {
		Table dataTable = new Table();
		String tablename = form.getIndexView() != null ? "sp" : "op";
		if (!("sp".equals(form.getDetail().getProcedurePrefix()) || "op".equals(form.getDetail().getProcedurePrefix()))) {
			tablename = form.getDetail().getProcedurePrefix();
		}
		if (prefix != null) {
			tablename += prefix;
		}
		tablename += form.getDetail().getProcedureSuffix();
		dataTable.setName(tablename);
		List<Field> allFields = null;
		allFields = getFieldsFromForm(form);

		for (Field f : allFields) {
			dataTable.addColumn(createColumnFromField(f, prefix));
		}
		return dataTable;
	}

	@Override
	public List<Field> getFieldsFromForm(Form form) {
		List<Field> allFields = new ArrayList<Field>();
		for (Object o : form.getDetail().getHeadAndPage()) {
			if (o instanceof Head) {
				Head head = (Head) o;
				List<Object> fields = head.getFieldOrGrid();
				allFields = filterFields(fields);
			} else if (o instanceof Page) {
				Page page = (Page) o;
				List<Object> fields = page.getFieldOrGrid();
				allFields.addAll(filterFields(fields));
			}
		}
		return allFields;
	}

	@Override
	public List<Field> getAllPrimaryFieldsFromForm(Form form) {
		List<Field> keyFields = new ArrayList<Field>();
		List<Field> allFields = new ArrayList<Field>();
		allFields = getFieldsFromForm(form);
		for (Field f : allFields) {
			if ("primary".equals(f.getKeyType())) {
				keyFields.add(f);
			}
		}
		return keyFields;
	}

	public List<Field> filterFields(List<Object> objects) {
		List<Field> fields = new ArrayList<Field>();
		for (Object o : objects) {
			if (o instanceof Field) {
				Field f = (Field) o;
				fields.add(f);
			}
			// TODO:Grid verarbeiten
		}
		fields.sort((o1, o2) -> o1.getSqlIndex().compareTo(o2.getSqlIndex()));
		return fields;
	}

	public aero.minova.rcp.model.Column createColumnFromField(Field f, String prefix) {
		DataType type = null;
		if (f.getPercentage() != null || f.getMoney() != null || (f.getNumber() != null && f.getNumber().getDecimals() > 0)) {
			type = DataType.DOUBLE;
		} else if (f.getNumber() != null || f.getLookup() != null) {
			type = DataType.INTEGER;
		} else if (f.getBoolean() != null) {
			type = DataType.BOOLEAN;
		} else if (f.getText() != null) {
			type = DataType.STRING;
		} else if (f.getDateTime() != null || f.getShortDate() != null || f.getShortTime() != null) {
			type = DataType.INSTANT;
		}

		aero.minova.rcp.model.Column c;
		if (prefix.equals("Read")) {
			c = new aero.minova.rcp.model.Column(f.getName(), type, OutputType.OUTPUT);
		} else {
			c = new aero.minova.rcp.model.Column(f.getName(), type);
		}

		c.setLabel(f.getLabel());

		return c;

	}

	/**
	 * Diese Methode leißt die Colum ein und gibt das zugehörige DataType Element zurück
	 *
	 * @param c
	 *            aero.minova.rcp.form.model.xsd.Column;
	 * @return DataType
	 */
	public DataType getDataType(Column c) {
		if ((c.getNumber() != null && c.getNumber().getDecimals() == 0) || c.getBignumber() != null) {
			return DataType.INTEGER;
		} else if (c.getBoolean() != null) {
			return DataType.BOOLEAN;
		} else if (c.getText() != null) {
			return DataType.STRING;
		} else if (c.getShortTime() != null || c.getLongTime() != null) {
			return DataType.INSTANT;
		} else if (c.getShortDate() != null || c.getLongDate() != null || c.getDateTime() != null) {
			return DataType.INSTANT;
			// sollte Zoned sein
		} else if (c.getMoney() != null || (c.getNumber() != null && c.getNumber().getDecimals() >= 0)) {
			return DataType.DOUBLE;
		}
		return null;
	}

	@Override
	public Form getForm(String name) {

		Form form = null;
		CompletableFuture<String> hashedFile = dataService.getHashedFile(name); // Datei ggf. vom Server holen
		// form wird synchron geladen, das sollte später auch asynchron werden
		String formContent = hashedFile.join();

		try {
			String localpath = Platform.getInstanceLocation().getURL().toURI().toString();
			File formFile = new File(localpath + name);
			if (!formFile.exists()) {
				// Datei vom Server holen
			}
			form = XmlProcessor.get(formContent, Form.class);
		} catch (URISyntaxException | JAXBException ex) {
			throw new RuntimeException(ex);
		}

		return form;
	}

}
