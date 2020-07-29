package aero.minova.rcp.dataservice.internal;

import java.math.BigInteger;
import java.time.Instant;
import java.time.ZonedDateTime;

import org.osgi.service.component.annotations.Component;

import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.form.model.xsd.Boolean;
import aero.minova.rcp.form.model.xsd.Column;
import aero.minova.rcp.form.model.xsd.Detail;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.form.model.xsd.Head;
import aero.minova.rcp.form.model.xsd.IndexView;
import aero.minova.rcp.form.model.xsd.Lookup;
import aero.minova.rcp.form.model.xsd.Number;
import aero.minova.rcp.form.model.xsd.Page;
import aero.minova.rcp.form.model.xsd.Text;

@Component
public class DataFormService implements IDataFormService {

	@Override
	public Form getForm() {
		Form form;
//		try {
//			xmlProcessor = new XmlProcessor(Form.class);
//			form = (Form) xmlProcessor.load(new File(
//					"/Users/erlanger/git/aero.minova.rcp/bundles/aero.minova.rcp.rcp/src/aero/minova/rcp/rcp/parts/WorkingTime.xml"));
//
//		} catch (JAXBException | SAXException | IOException e) {
//			e.printStackTrace();
//		}
		// test data for creating a NatTable
		form = new Form();
		form.setIndexView(new IndexView());
		form.getIndexView().setSource("OrderReceiver");
		Column c = new Column();
		c.setName("KeyLong"); // das hier ist Attribute im Datenmodell
		c.setTextAttribute("ID");
		form.getIndexView().getColumn().add(c);
		c = new Column();
		c.setName("KeyText"); // das hier ist Attribute im Datenmodell
		c.setTextAttribute("MatchCode");
		form.getIndexView().getColumn().add(c);
		c = new Column();
		c.setName("Description"); // das hier ist Attribute im Datenmodell
		c.setTextAttribute("Beschreibung");
		form.getIndexView().getColumn().add(c);c = new Column();
		c.setName("LastDate"); // das hier ist Attribute im Datenmodell
		c.setTextAttribute("Letzte Bearbeitung");
		form.getIndexView().getColumn().add(c);c = new Column();
		c.setName("ValidUntil"); // das hier ist Attribute im Datenmodell
		c.setTextAttribute("Gültig bis");
		form.getIndexView().getColumn().add(c);c = new Column();
		c.setName("Married"); // das hier ist Attribute im Datenmodell
		c.setTextAttribute("Verheiratet");
		form.getIndexView().getColumn().add(c);
		
		Detail detail = new Detail();
		Head head = new Head();
		
		Field field = new Field();
		field.setName("Keylong");
		field.setVisible(false);
		field.setKeyType("primary");
		field.setSqlIndex(new BigInteger("0"));
		Number number = new Number();
		number.setDecimals(0);
		field.setNumber(number);
		head.getFieldOrSeparatorOrGrid().add(field);
		
		field = new Field();
		field.setName("KeyText");
		field.setVisible(true);
		field.setSqlIndex(new BigInteger("1"));
		Text text = new Text();
		text.setLength(20);
		field.setText(text);
		head.getFieldOrSeparatorOrGrid().add(field);
		
		field = new Field();
		field.setName("Description");
		field.setVisible(true);
		field.setSqlIndex(new BigInteger("2"));
		text = new Text();
		text.setLength(50);
		field.setText(text);
		head.getFieldOrSeparatorOrGrid().add(field);
		
		field = new Field();
		field.setName("LastDate");
		field.setVisible(true);
		field.setSqlIndex(new BigInteger("3"));
		Instant instant = Instant.now();
		field.setDateTime(instant);
		head.getFieldOrSeparatorOrGrid().add(field);
		
		field = new Field();
		field.setName("ValidUntil");
		field.setVisible(true);
		field.setSqlIndex(new BigInteger("4"));
		ZonedDateTime zoned = ZonedDateTime.now();
		field.setDateTime(text);
		head.getFieldOrSeparatorOrGrid().add(field);
		
		field = new Field();
		field.setName("Married");
		field.setVisible(true);
		field.setSqlIndex(new BigInteger("5"));
		aero.minova.rcp.form.model.xsd.Boolean bool = new Boolean();
		text.setLength(20);
		field.setBoolean(bool);
		head.getFieldOrSeparatorOrGrid().add(field);
		
		field = new Field();
		field.setName("VehicleKey");
		field.setVisible(true);
		field.setSqlIndex(new BigInteger("6"));
		Lookup lookup = new Lookup();
		text.setLength(20);
		field.setLookup(lookup);
		head.getFieldOrSeparatorOrGrid().add(field);
		
		Page page = new Page();
		page.setText("Page");
		detail.getHeadAndPage().add(head);
		detail.getHeadAndPage().add(page);
		
		form.setDetail(detail);
		
		return form;
	}

}
