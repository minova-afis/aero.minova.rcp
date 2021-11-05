package aero.minova.rcp.dataservice.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dataservice.XmlProcessor;
import aero.minova.rcp.form.model.xsd.Column;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.form.model.xsd.Grid;
import aero.minova.rcp.form.model.xsd.Head;
import aero.minova.rcp.form.model.xsd.Page;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.DateTimeType;
import aero.minova.rcp.model.KeyType;
import aero.minova.rcp.model.OutputType;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.util.ErrorObject;

@Component
public class DataFormService implements IDataFormService {

	@Reference
	IDataService dataService;

	EventAdmin eventAdmin;

	private List<String> requestedForms = new ArrayList<>();

	@Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MANDATORY)
	void registerEventAdmin(EventAdmin admin) {
		this.eventAdmin = admin;
	}

	void unregisterEventAdmin(EventAdmin admin) {
		this.eventAdmin = null;
	}

	@Override
	public Table getTableFromFormIndex(Form form) {
		Table dataTable = new Table();
		dataTable.setName(form.getIndexView().getSource());
		for (Column c : form.getIndexView().getColumn()) {
			aero.minova.rcp.model.Column tableColumn = new aero.minova.rcp.model.Column(c.getName(), getDataType(c), OutputType.OUTPUT);
			tableColumn.setLabel(c.getLabel());

			Integer decimals = null;
			if (c.getNumber() != null) {
				decimals = c.getNumber().getDecimals();
			} else if (c.getMoney() != null) {
				decimals = c.getMoney().getDecimals();
			} else if (c.getPercentage() != null) {
				decimals = c.getPercentage().getDecimals();
			}
			tableColumn.setDecimals(decimals);

			DateTimeType dateTimeType = null;
			if (c.getDateTime() != null) {
				dateTimeType = DateTimeType.DATETIME;
			} else if (c.getShortDate() != null) {
				dateTimeType = DateTimeType.DATE;
			} else if (c.getShortTime() != null) {
				dateTimeType = DateTimeType.TIME;
			}
			tableColumn.setDateTimeType(dateTimeType);

			boolean visibleBasedOnSize = c.getSize() == null || c.getSize().intValue() > 0;
			tableColumn.setVisible(c.isVisible() && visibleBasedOnSize);

			dataTable.addColumn(tableColumn);
		}
		return dataTable;
	}

	@Override
	public Table getTableFromFormDetail(Form form, String prefix) {
		Table dataTable = new Table();
		String tablename = form.getIndexView() != null ? "sp" : "op";
		if ((!"sp".equals(form.getDetail().getProcedurePrefix()) && !"op".equals(form.getDetail().getProcedurePrefix()))) {
			tablename = form.getDetail().getProcedurePrefix();
		}
		if (prefix != null) {
			tablename += prefix;
		}
		tablename += form.getDetail().getProcedureSuffix();
		dataTable.setName(tablename);
		List<Field> allFields = null;
		allFields = getFieldsFromForm(form);

		// Sortierung der Felder nach sql-index oder der Reihe nach!
		allFields = allFields.stream().sorted(Comparator.comparing(Field::getSqlIndex)).filter(f -> f.getSqlIndex().intValue() >= 0)
				.collect(Collectors.toList());
		allFields.stream().forEach(f -> dataTable.addColumn(createColumnFromField(f, prefix)));
		return dataTable;
	}

	@Override
	public List<Field> getFieldsFromForm(Form form) {
		List<Field> allFields = new ArrayList<>();
		for (Object o : form.getDetail().getHeadAndPageAndGrid()) {
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
		List<Field> keyFields = new ArrayList<>();
		for (Field f : getFieldsFromForm(form)) {
			if ("primary".equals(f.getKeyType())) {
				keyFields.add(f);
			}
		}
		return keyFields;
	}

	public List<Field> filterFields(List<Object> objects) {
		List<Field> fields = new ArrayList<>();
		for (Object o : objects) {
			if (o instanceof Field) {
				Field f = (Field) o;
				fields.add(f);
			}
			// TODO:Grid verarbeiten
		}

		// Nach SQL-Index sortieren und nur Felder mit SQL-Index >= 0 zurückgeben
		fields = fields.stream().sorted(Comparator.comparing(Field::getSqlIndex)).filter(f -> f.getSqlIndex().intValue() >= 0).collect(Collectors.toList());
		return fields;
	}

	@Override
	public aero.minova.rcp.model.Column createColumnFromField(Field f, String prefix) {
		DataType type = null;
		DateTimeType dateTimeType = null;
		Integer decimals = null;
		if (f.getPercentage() != null || (f.getNumber() != null && f.getNumber().getDecimals() > 0)) {
			type = DataType.DOUBLE;
			if (f.getNumber() != null) {
				decimals = f.getNumber().getDecimals();
			} else if (f.getMoney() != null) {
				decimals = f.getMoney().getDecimals();
			} else if (f.getPercentage() != null) {
				decimals = f.getPercentage().getDecimals();
			}
		} else if (f.getNumber() != null || f.getLookup() != null || f.getEditor() != null) {
			type = DataType.INTEGER;
		} else if (f.getMoney() != null) {
			type = DataType.BIGDECIMAL;
		} else if (f.getBoolean() != null) {
			type = DataType.BOOLEAN;
		} else if (f.getDateTime() != null || f.getShortDate() != null || f.getShortTime() != null) {
			type = DataType.INSTANT;
			if (f.getDateTime() != null) {
				dateTimeType = DateTimeType.DATETIME;
			} else if (f.getShortDate() != null) {
				dateTimeType = DateTimeType.DATE;
			} else if (f.getShortTime() != null) {
				dateTimeType = DateTimeType.TIME;
			}
		} else {
			type = DataType.STRING;
		}

		aero.minova.rcp.model.Column c;
		if (prefix.equals("Read")) {
			c = new aero.minova.rcp.model.Column(f.getName(), type, OutputType.OUTPUT);
		} else {
			c = new aero.minova.rcp.model.Column(f.getName(), type);
		}

		c.setLabel(f.getLabel());
		c.setDecimals(decimals);
		c.setDateTimeType(dateTimeType);
		c.setReadOnly(f.isReadOnly());
		c.setRequired(f.isRequired());
		c.setKeyType(Arrays.stream(KeyType.values()).filter(e -> e.name().equalsIgnoreCase(f.getKeyType())).findAny().orElse(null));
		c.setLookup(f.getLookup() != null);
		if (f.getLookup() != null) {
			c.setLookupTable(f.getLookup().getTable());
		}
		c.setVisible(f.isVisible());

		return c;

	}

	@Override
	/**
	 * Erstellt eine Table aus dem übergenen Grid
	 */
	public Table getTableFromGrid(Grid grid) {
		Table dataTable = new Table();
		String prefix = "Read";
		String tablename = grid.getProcedurePrefix() + prefix + grid.getProcedureSuffix();
		dataTable.setName(tablename);
		for (Field f : grid.getField()) {
			dataTable.addColumn(createColumnFromField(f, prefix));
		}
		return dataTable;
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
		String formContent = "";

		try {
			// Datei ggf. vom Server holen, form wird synchron geladen, das sollte später auch asynchron werden
			formContent = dataService.getHashedFile(name).join();
		} catch (Exception e) {
			// Datei/Hash für Datei konnte nicht vom Server geladen werden, Versuchen lokale Datei zu nutzen
			try {
				formContent = dataService.getCachedFileContent(name).get();
				// Fehlermeldung nur einmal pro Form zeigen
				if (!requestedForms.contains(name)) {
					postError(new ErrorObject("msg.WFCUsingLocalMask", dataService.getUserName(), e));
				}
			} catch (InterruptedException | ExecutionException e1) {
				if (!requestedForms.contains(name)) {
					postError(new ErrorObject("msg.WFCCouldntLoadMask", dataService.getUserName(), e1));
				}
			}
		}

		try {
			form = XmlProcessor.get(formContent, Form.class);
		} catch (JAXBException ex) {
			throw new RuntimeException(ex);
		}

		requestedForms.add(name);
		return form;
	}

	public void postError(ErrorObject message) {
		Dictionary<String, Object> data = new Hashtable<>(2);
		data.put(EventConstants.EVENT_TOPIC, Constants.BROKER_SHOWCONNECTIONERRORMESSAGE);
		data.put(IEventBroker.DATA, message);
		Event event = new Event(Constants.BROKER_SHOWCONNECTIONERRORMESSAGE, data);
		eventAdmin.postEvent(event);
	}
}
