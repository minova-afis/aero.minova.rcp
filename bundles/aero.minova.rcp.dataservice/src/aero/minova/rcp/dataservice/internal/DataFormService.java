package aero.minova.rcp.dataservice.internal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.osgi.service.component.annotations.Component;
import org.xml.sax.SAXException;

import aero.minova.rcp.dataservice.IDataFormService;
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

	@Override
	public Table getTableFromFormIndex(Form form) {
		Table dataTable = new Table();
		dataTable.setName(form.getIndexView().getSource());
		for (Column c : form.getIndexView().getColumn()) {

			aero.minova.rcp.model.Column columnTable = new aero.minova.rcp.model.Column(c.getName(),
					getDataType(c), OutputType.OUTPUT);
			dataTable.addColumn(columnTable);
		}
		return dataTable;
	}

	@Override
	public Table getTableFromFormDetail(Form form, String prefix) {
		Table dataTable = new Table();
		String tablename = form.getIndexView() != null ? "sp" : "op";
		if (!("sp".equals(form.getDetail().getProcedurePrefix())
				|| "op".equals(form.getDetail().getProcedurePrefix()))) {
			tablename = form.getDetail().getProcedurePrefix();
		}
		if (prefix != null) {
			tablename += prefix;
		}
		tablename += form.getDetail().getProcedureSuffix();
		dataTable.setName(tablename);
		List<Field> allFields = null;
		if (prefix != "Insert") {
			allFields = getFieldsFromForm(form, false);
		} else {
			allFields = getFieldsFromForm(form, true);
		}

		for (Field f : allFields) {
			dataTable.addColumn(createColumnFromField(f));
		}
		return dataTable;
	}

	@Override
	public List<Field> getFieldsFromForm(Form form, Boolean insert) {
		List<Field> allFields = new ArrayList<Field>();
		for (Object o : form.getDetail().getHeadAndPage()) {
			if (o instanceof Head) {
				Head head = (Head) o;
				List<Object> fields = head.getFieldOrGrid();
				allFields = filterFields(fields, insert);
			} else if (o instanceof Page) {
				Page page = (Page) o;
				List<Object> fields = page.getFieldOrGrid();
				allFields.addAll(filterFields(fields, insert));
			}
		}
		return allFields;
	}

	public List<Field> filterFields(List<Object> objects, Boolean insert) {
		List<Field> fields = new ArrayList<Field>();
		for (Object o : objects) {
			if (o instanceof Field) {
				Field f = (Field) o;
				if (insert == false) {
					fields.add(f);
				} else {
					if (!"primary".equals(f.getKeyType())) {
						fields.add(f);
					}
				}
			}
			// TODO:Grid verarbeiten
		}
		fields.sort((o1, o2) -> o1.getSqlIndex().compareTo(o2.getSqlIndex()));
		return fields;
	}

	public aero.minova.rcp.model.Column createColumnFromField(Field f) {
		DataType type = null;
		if (f.getPercentage() != null || f.getMoney() != null
				|| (f.getNumber() != null && f.getNumber().getDecimals() > 0)) {
			type = DataType.DOUBLE;
		} else if (f.getNumber() != null || f.getLookup() != null) {
			type = DataType.INTEGER;
		}
		else if (f.getBoolean() != null) {
			type = DataType.BOOLEAN;
		}
		else if (f.getText() != null) {
			type = DataType.STRING;
		}
		else if (f.getDateTime() != null || f.getShortDate() != null || f.getShortTime() != null) {
			type = DataType.INSTANT;
		}

		return new aero.minova.rcp.model.Column(f.getName(), type, OutputType.OUTPUT);

	}
	/**
	 * Diese Methode leißt die Colum ein und gibt das zugehörige DataType Element
	 * zurück
	 *
	 * @param c aero.minova.rcp.form.model.xsd.Column;
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
			//sollte Zoned sein
		} else if (c.getMoney() != null || (c.getNumber() != null && c.getNumber().getDecimals() >= 0)) {
			return DataType.DOUBLE;
		}
		return null;
	}

	@Override
	public Form getForm() {
		// Test data
		String userDir = System.getProperty("user.home");

		Form form = null;
		try {
			XmlProcessor xmlProcessor = new XmlProcessor(Form.class);
			form = (Form) xmlProcessor.load(new File(userDir
					+ "/git/aero.minova.rcp/bundles/aero.minova.rcp.rcp/src/aero/minova/rcp/rcp/parts/WorkingTime.xml"));

		} catch (JAXBException | SAXException | IOException e) {
			e.printStackTrace();
		}
		// test data for creating a NatTable
//		form = new Form();
//		form.setIndexView(new IndexView());
//		form.getIndexView().setSource("OrderReceiver");
//		Column c = new Column();
//		c.setName("KeyLong"); // das hier ist Attribute im Datenmodell
//		c.setTextAttribute("ID");
//		form.getIndexView().getColumn().add(c);
//		c = new Column();
//		c.setName("KeyText"); // das hier ist Attribute im Datenmodell
//		c.setTextAttribute("MatchCode");
//		form.getIndexView().getColumn().add(c);
//		c = new Column();
//		c.setName("Description"); // das hier ist Attribute im Datenmodell
//		c.setTextAttribute("Beschreibung");
//		form.getIndexView().getColumn().add(c);c = new Column();
//		c.setName("LastDate"); // das hier ist Attribute im Datenmodell
//		c.setTextAttribute("Letzte Bearbeitung");
//		form.getIndexView().getColumn().add(c);c = new Column();
//		c.setName("ValidUntil"); // das hier ist Attribute im Datenmodell
//		c.setTextAttribute("Gültig bis");
//		form.getIndexView().getColumn().add(c);c = new Column();
//		c.setName("Married"); // das hier ist Attribute im Datenmodell
//		c.setTextAttribute("Verheiratet");
//		form.getIndexView().getColumn().add(c);
//
//		Detail detail = new Detail();
//		Head head = new Head();
//		Page page = new Page();
//		page.setText("Administration");
//
//		Field field = new Field();
//		field.setName("Keylong");
//		field.setVisible(false);
//		field.setKeyType("primary");
//		field.setSqlIndex(new BigInteger("0"));
//		Number number = new Number();
//		number.setDecimals(0);
//		field.setNumber(number);
//		head.getFieldOrGrid().add(field);
//
//		field = new Field();
//		field.setName("KeyText");
//		field.setTextAttribute("MatchCode");
//		field.setVisible(true);
//		field.setSqlIndex(new BigInteger("1"));
//		Text text = new Text();
//		text.setLength(20);
//		field.setText(text);
//		head.getFieldOrGrid().add(field);
//
//		field = new Field();
//		field.setName("Description");
//		field.setTextAttribute("Beschreibung");
//		field.setVisible(true);
//		field.setSqlIndex(new BigInteger("2"));
//		text = new Text();
//		text.setLength(50);
//		field.setText(text);
//		head.getFieldOrGrid().add(field);
//
//		field = new Field();
//		field.setName("LastDate");
//		field.setTextAttribute("Letzte Änderung");
//		field.setVisible(true);
//		field.setSqlIndex(new BigInteger("3"));
//		Instant instant = Instant.now();
//		field.setDateTime(instant);
//		page.getFieldOrGrid().add(field);
//
//		field = new Field();
//		field.setName("ValidUntil");
//		field.setTextAttribute("Gültig bis");
//		field.setVisible(true);
//		field.setSqlIndex(new BigInteger("4"));
//		ZonedDateTime zoned = ZonedDateTime.now();
//		field.setDateTime(zoned);
//		page.getFieldOrGrid().add(field);
//
//		field = new Field();
//		field.setName("Married");
//		field.setTextAttribute("Verheiratet");
//		field.setVisible(true);
//		field.setSqlIndex(new BigInteger("5"));
//		aero.minova.rcp.form.model.xsd.Boolean bool = new Boolean();
//		text.setLength(20);
//		field.setBoolean(bool);
//		page.getFieldOrGrid().add(field);
//
//		field = new Field();
//		field.setName("VehicleKey");
//		field.setTextAttribute("Fahrzeug ID");
//		field.setVisible(true);
//		field.setSqlIndex(new BigInteger("6"));
//		Lookup lookup = new Lookup();
//		text.setLength(20);
//		field.setLookup(lookup);
//		page.getFieldOrGrid().add(field);
//
//
//		detail.getHeadAndPage().add(head);
//		detail.getHeadAndPage().add(page);
//
//		form.setDetail(detail);

		return form;
	}

}
