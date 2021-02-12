package aero.minova.rcp.xml.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import aero.minova.rcp.dataservice.XmlProcessor;
import aero.minova.rcp.dataservice.internal.DataFormService;
import aero.minova.rcp.form.model.xsd.Column;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.model.DataType;

class XmlReaderTests {
	private DataFormService dfs;
	private Path path;


	@BeforeEach
	public void setup() {
		dfs = new DataFormService();
		path = Path.of("resources", "work", "WorkingTime.xml");
	}

	@Test
	@DisplayName("Ensure that the WorkingTime.xml file for the test is present")
	void ensureThatLocalWorkingTimeFileIsPresent() throws IOException {
		assertTrue(path.toFile().exists(), "WorkingTime.xml should be prent for the tests");
	}

	@Test
	@DisplayName("Ensure DataFormService can convert WorkingTime.xml to the correct data type")
	void dataServiceConvertColumnToDataType() throws Exception {

		Form form = null;
		String content = Files.readString(path);
		try {
			XmlProcessor xmlProcessor = new XmlProcessor();
			form = xmlProcessor.get(content, Form.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		assertNotNull(form);
		List<Column> column = form.getIndexView().getColumn();
		assertEquals(15, column.size());
		assertEquals(DataType.INTEGER, dfs.getDataType(column.get(0)));
		assertEquals(DataType.STRING, dfs.getDataType(column.get(1)));
		assertEquals(DataType.STRING, dfs.getDataType(column.get(4)));
		assertEquals(DataType.INSTANT, dfs.getDataType(column.get(5)));
		assertEquals(DataType.INSTANT, dfs.getDataType(column.get(6)));// Short-Date
		assertEquals(DataType.INSTANT, dfs.getDataType(column.get(7)));//Short-Time
		assertEquals(DataType.DOUBLE, dfs.getDataType(column.get(8)));
		assertEquals(DataType.BOOLEAN, dfs.getDataType(column.get(12)));

	}
}
