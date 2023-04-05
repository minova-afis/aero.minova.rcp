package aero.minova.rcp.dataservice;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * Kann XML-Dokumente mittels XSD-Schema lesen und schreiben
 */
public class XmlProcessor {

	private XmlProcessor() {}

	public static <T> T get(String content, Class<T> expectedRootClass) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(expectedRootClass.getPackageName(), expectedRootClass.getClassLoader());
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		Object obj = unmarshaller.unmarshal(new StringReader(content));
		if (expectedRootClass.isInstance(obj)) {
			return expectedRootClass.cast(obj);
		}
		throw new IllegalArgumentException("XML does not represent an instance of type:" + expectedRootClass.getName());
	}
}