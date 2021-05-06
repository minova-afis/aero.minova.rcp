package aero.minova.rcp.dataservice.internal;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
import aero.minova.rcp.form.model.xsd.Head;
import aero.minova.rcp.form.model.xsd.Page;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.DateTimeType;
import aero.minova.rcp.model.OutputType;
import aero.minova.rcp.model.Table;

@Component
public class DataFormService implements IDataFormService {

	@Reference
	IDataService dataService;

	HashMap<String, Form> forms = new HashMap<String, Form>();

	EventAdmin eventAdmin;

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
		DateTimeType dateTimeType = null;
		Integer decimals = null;
		if (f.getPercentage() != null || f.getMoney() != null || (f.getNumber() != null && f.getNumber().getDecimals() > 0)) {
			type = DataType.DOUBLE;
			if (f.getNumber() != null) {
				decimals = f.getNumber().getDecimals();
			} else if (f.getMoney() != null) {
				decimals = f.getMoney().getDecimals();
			} else if (f.getPercentage() != null) {
				decimals = f.getPercentage().getDecimals();
			}
		} else if (f.getNumber() != null || f.getLookup() != null) {
			type = DataType.INTEGER;
		} else if (f.getBoolean() != null) {
			type = DataType.BOOLEAN;
		} else if (f.getText() != null) {
			type = DataType.STRING;
		} else if (f.getDateTime() != null || f.getShortDate() != null || f.getShortTime() != null) {
			type = DataType.INSTANT;
			if (f.getDateTime() != null) {
				dateTimeType = DateTimeType.DATETIME;
			} else if (f.getShortDate() != null) {
				dateTimeType = DateTimeType.DATE;
			} else if (f.getShortTime() != null) {
				dateTimeType = DateTimeType.TIME;
			}
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

		if (forms.containsKey(name)) {
			return forms.get(name);
		}

		Form form = null;
		String formContent = "";
		try {
			// Datei ggf. vom Server holen, form wird synchron geladen, das sollte später auch asynchron werden
			formContent = dataService.getHashedFile(name).join();
		} catch (Exception e) {
			// Datei/Hash für Datei konnte nicht vom Server geladen werden, Versuchen lokale Datei zu nutzen
			try {
				postError("msg.WFCUsingLocalMask");
				// TODO: Fehlermeldung, Maske konnte nicht geladen werden, benutzen lokale (evtl. veraltete) Version
				formContent = dataService.getCachedFileContent(name).get();
			} catch (InterruptedException | ExecutionException e1) {
				// TODO: Fehlermeldung, Maske konnte nicht geladen werden
				postError("msg.WFCCouldntLoadMask");
			}
		}

		try {
			form = XmlProcessor.get(formContent, Form.class);
		} catch (JAXBException ex) {
			throw new RuntimeException(ex);
		}

		forms.put(name, form);
		return form;
	}

	public void postError(String message) {
		Dictionary<String, Object> data = new Hashtable<>(2);
		data.put(EventConstants.EVENT_TOPIC, Constants.BROKER_SHOWERRORMESSAGE);
		data.put(IEventBroker.DATA, message);
		Event event = new Event(Constants.BROKER_SHOWERRORMESSAGE, data);
		eventAdmin.postEvent(event);
	}

}
