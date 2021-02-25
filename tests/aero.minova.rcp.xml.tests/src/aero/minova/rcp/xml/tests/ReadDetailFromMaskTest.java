package aero.minova.rcp.xml.tests;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import aero.minova.rcp.dataservice.XmlProcessor;
import aero.minova.rcp.dataservice.internal.DataFormService;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.model.Table;

class ReadDetailFromMaskTest {

	private DataFormService dfs;

	@BeforeEach
	public void setup() {
		dfs = new DataFormService();
	}

	@Test
	void dataServiceConvertColumnToDataType() throws Exception {
		String userDir = System.getProperty("user.home");

		Form form = null;
		try {
			XmlProcessor xmlProcessor = new XmlProcessor();
			form = (Form) xmlProcessor.load(new File(userDir
					+ "/git/aero.minova.rcp/bundles/aero.minova.rcp.rcp/src/aero/minova/rcp/rcp/parts/WorkingTime.xml"),
					Form.class);

		} catch (JAXBException | SAXException | IOException e) {
			e.printStackTrace();
		}
		Table tableFromFormDetail = dfs.getTableFromFormDetail(form, "Read");
		assertEquals(13, tableFromFormDetail.getColumnCount());
		assertEquals("KeyLong", tableFromFormDetail.getColumnName(0));
		assertEquals("EmployeeKey", tableFromFormDetail.getColumnName(1));
		assertEquals("OrderReceiverKey", tableFromFormDetail.getColumnName(3));
		assertEquals("ServiceContractKey", tableFromFormDetail.getColumnName(2));
		assertEquals("ServiceObjectKey", tableFromFormDetail.getColumnName(4));
		assertEquals("ServiceKey", tableFromFormDetail.getColumnName(5));
		assertEquals("BookingDate", tableFromFormDetail.getColumnName(6));
		assertEquals("StartDate", tableFromFormDetail.getColumnName(7));
		assertEquals("EndDate", tableFromFormDetail.getColumnName(8));
		assertEquals("RenderedQuantity", tableFromFormDetail.getColumnName(9));
		assertEquals("ChargedQuantity", tableFromFormDetail.getColumnName(10));
		assertEquals("Description", tableFromFormDetail.getColumnName(11));
		assertEquals("Spelling", tableFromFormDetail.getColumnName(12));
		assertEquals("spReadWorkingTime", tableFromFormDetail.getName());
	}

	@Test
	void dataServiceReadDataWithProcedureSuffix() throws Exception {
		String userDir = System.getProperty("user.home");

		Form form = null;
		try {
			XmlProcessor xmlProcessor = new XmlProcessor();
			form = (Form) xmlProcessor.load(new File(userDir
					+ "/git/aero.minova.rcp/bundles/aero.minova.rcp.rcp/src/aero/minova/rcp/rcp/parts/WorkingTime.xml"),
					Form.class);

		} catch (JAXBException | SAXException | IOException e) {
			e.printStackTrace();
		}

		form.getDetail().setProcedurePrefix("xtsap");
		Table tableFromFormDetail = dfs.getTableFromFormDetail(form, "Read");
		assertEquals("xtsapReadWorkingTime", tableFromFormDetail.getName());
	}

	@Test
	void dataServiceReadOptionpage() throws Exception {
		String userDir = System.getProperty("user.home");

		Form form = null;
		try {
			XmlProcessor xmlProcessor = new XmlProcessor();
			form = (Form) xmlProcessor.load(new File(userDir
					+ "/git/aero.minova.rcp/bundles/aero.minova.rcp.rcp/src/aero/minova/rcp/rcp/parts/WorkingTime.xml"),
					Form.class);

		} catch (JAXBException | SAXException | IOException e) {
			e.printStackTrace();
		}
		form.setIndexView(null);
		Table tableFromFormDetail = dfs.getTableFromFormDetail(form, "Read");
		assertEquals("opReadWorkingTime", tableFromFormDetail.getName());
	}

}
