package aero.minova.rcp.dataservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.osgi.service.component.annotations.Component;
import org.xml.sax.SAXException;


 

/**
 * Kann XML-Dokumente mittels XSD-Schema lesen und schreiben
 * 
 * @author wild
 * @since 11.0.0
 */
/**
 * Erzeugt einen XmlProcessor als immediate OSGi component, der Objekte vom
 * angegebenen Typ lesen und schreiben kann
 * 
 * @param expectedRootClass
 * @param schemaFile
 */

@Component(immediate = true)
public class XmlProcessor {
 

    /**
     * XML-Datei vom angegebenen Pfad laden
     * 
     * @param file
     *            das Datei-Objekt
     * @throws JAXBException
     *             verschiedene Fehlermöglichkeiten innerhalb JaxB
     * @throws SAXException
     *             falls das XSD-Schema nicht geladen werden konnte
     * @throws IOException 
     */
	@Deprecated(forRemoval = true)
	// CLIENT CODE SOLL DIE FILES NICHT MEHR DIREKT ADRESSIEREN, DIES SOLL ÜBER DEN
	// DATASERVICE GEMACHT WERDEN; DAMIT DIESER CACHEN / HASH CODE CHECKS
	// DURCHFÜHREN KANN
    public Object load(File file, Class<?> expectedRootClass) throws JAXBException, SAXException, IOException {
		JAXBContext jc = JAXBContext.newInstance(expectedRootClass.getPackageName(),
				expectedRootClass.getClassLoader());
        Unmarshaller unmarshaller = jc.createUnmarshaller();
 

        Object rootElement = null;
        
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            rootElement = unmarshaller.unmarshal(fileInputStream);
        } 
        return rootElement;
	}

	public static <T> T get(String content, Class<T> expectedRootClass) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(expectedRootClass.getPackageName(),
				expectedRootClass.getClassLoader());
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		Object obj = unmarshaller.unmarshal(new StringReader(content));
		if (expectedRootClass.isInstance(obj)) {
			return expectedRootClass.cast(obj);
		}
		throw new IllegalArgumentException("XML does not represent an instance of type:" + expectedRootClass.getName());
	}
}