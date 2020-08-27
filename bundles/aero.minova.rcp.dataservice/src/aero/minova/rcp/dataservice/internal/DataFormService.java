package aero.minova.rcp.dataservice.internal;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.osgi.service.component.annotations.Component;
import org.xml.sax.SAXException;

import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.form.model.xsd.Column;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.plugin1.model.DataType;
import aero.minova.rcp.plugin1.model.Table;

@Component
public class DataFormService implements IDataFormService {

	@Override
	public Table getTableFromFormIndex(Form form) {
		Table dataTable = new Table();
		dataTable.setName(form.getIndexView().getSource());
		for (Column c : form.getIndexView().getColumn()) {

			aero.minova.rcp.plugin1.model.Column columnTable = new aero.minova.rcp.plugin1.model.Column(c.getName(),
					getDataType(c));
			dataTable.addColumn(columnTable);
		}
		return dataTable;
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
			return DataType.STRING;
		} else if (c.getText() != null) {
			return DataType.STRING;
		} else if (c.getShortTime() != null || c.getLongTime() != null) {
			return DataType.STRING;
		} else if (c.getShortDate() != null || c.getLongDate() != null || c.getDateTime() != null) {
			return DataType.STRING;
			//sollte Zoned sein
		} else if (c.getMoney() != null || (c.getNumber() != null && c.getNumber().getDecimals() >= 0)) {
			return DataType.STRING;
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
