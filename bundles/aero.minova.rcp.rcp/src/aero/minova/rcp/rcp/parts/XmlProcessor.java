package aero.minova.rcp.rcp.parts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.xml.sax.SAXException;

 

/**
 * Kann XML-Dokumente mittels XSD-Schema lesen und schreiben
 * 
 * @author wild
 * @since 11.0.0
 */
public class XmlProcessor {
    private Class<?> expectedRootClass;

 

    /**
     * Erzeugt einen XmlProcessor, der Objekte vom angegebenen Typ lesen und
     * schreiben kann
     * 
     * @param expectedRootClass
     * @param schemaFile
     */
    public XmlProcessor(Class<?> expectedRootClass) {
        this.expectedRootClass = expectedRootClass;
    }

 

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
    public Object load(File file) throws JAXBException, SAXException, IOException {
        JAXBContext jc = JAXBContext.newInstance(this.expectedRootClass.getPackage().getName());
        Unmarshaller unmarshaller = jc.createUnmarshaller();

        // unmarshaller.setProperty(name, value);

 

        // Wenn der Block verlassen wird, (Error order Sonstiges) wird die close-Methode
        // des FileInputStream aufgerufen.(Giilt für jedes Element wenn es autocloseable
        // implementiert
        Object rootElement = null;
        
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            rootElement = unmarshaller.unmarshal(fileInputStream);
        } 
        return rootElement;
    }

}