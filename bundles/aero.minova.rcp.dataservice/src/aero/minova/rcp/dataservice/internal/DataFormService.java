package aero.minova.rcp.dataservice.internal;

import org.osgi.service.component.annotations.Component;

import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.form.model.xsd.Column;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.form.model.xsd.IndexView;

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
		Column c2 = new Column();
		c2.setName("EmployeeText"); // das hier ist Attribute im Datenmodell
		c2.setTextAttribute("Mitarbeiter");
		form.getIndexView().getColumn().add(c2);
		return form;
	}

}
