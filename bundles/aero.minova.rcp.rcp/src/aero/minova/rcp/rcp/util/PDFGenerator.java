package aero.minova.rcp.rcp.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.xml.sax.SAXException;

/**
 * Generate a PDF file using XML data and XSLT stylesheets
 */
public class PDFGenerator {

	public PDFGenerator() {}

	public void createPdfFile(String xmlDataString, String templateString, OutputStream pdfOutputStream)
			throws IOException, SAXException, TransformerException {
		System.out.println("Create pdf file ...");
		File tempFile = File.createTempFile("fop-" + System.currentTimeMillis(), ".pdf");

		FopFactory fopFactory = FopFactory.newInstance();
		fopFactory.setBaseURL(new File(".").toURI().toURL().toString());
		FOUserAgent userAgent = fopFactory.newFOUserAgent();

		try {
			// set output format
			Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, userAgent, pdfOutputStream);

			// Load template
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory
					.newTransformer(new StreamSource(new ByteArrayInputStream(templateString.getBytes(StandardCharsets.UTF_8))));

			// Set value of parameters in stylesheet
			transformer.setParameter("version", "1.0");

			// Input for XSLT transformations
			Source xmlSource = new StreamSource(new ByteArrayInputStream(xmlDataString.getBytes(StandardCharsets.UTF_8)));

			Result result = new SAXResult(fop.getDefaultHandler());

			transformer.transform(xmlSource, result);
		} finally {
			tempFile.delete();
		}
	}

}