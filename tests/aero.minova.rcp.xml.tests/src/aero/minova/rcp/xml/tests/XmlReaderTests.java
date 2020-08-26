package aero.minova.rcp.xml.tests;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;

import aero.minova.rcp.dataservice.internal.DataFormService;
import aero.minova.rcp.dataservice.internal.XmlProcessor;
import aero.minova.rcp.form.model.xsd.Column;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.plugin1.model.DataType;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class XmlReaderTests {
	private DataFormService dfs;

	@org.junit.Test
	public void testName() {
		assertTrue(true);
	}

	@Before
	public void setup() {
		dfs = new DataFormService();
	}

	@Test
	public void dataServiceConvertColumnToDataType() throws Exception {
		String userDir = System.getProperty("user.home");

		Form form = null;
		try {
			XmlProcessor xmlProcessor = new XmlProcessor(Form.class);
			form = (Form) xmlProcessor.load(new File(userDir
					+ "/git/aero.minova.rcp/bundles/aero.minova.rcp.rcp/src/aero/minova/rcp/rcp/parts/WorkingTime.xml"));

		} catch (JAXBException | SAXException | IOException e) {
			e.printStackTrace();
		}
		List<Column> column = form.getIndexView().getColumn();
		assertEquals(15, column.size());
		assertEquals(DataType.INTEGER, dfs.getDataType(column.get(0)));
		assertEquals(DataType.STRING, dfs.getDataType(column.get(1)));
		assertEquals(DataType.INSTANT, dfs.getDataType(column.get(5)));// Short-Date
		assertEquals(DataType.INSTANT, dfs.getDataType(column.get(7)));//Short-Time
		assertEquals(DataType.DOUBLE, dfs.getDataType(column.get(8)));
		assertEquals(DataType.BOOLEAN, dfs.getDataType(column.get(12)));

	}
}
