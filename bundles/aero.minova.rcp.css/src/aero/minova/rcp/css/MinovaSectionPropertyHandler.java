package aero.minova.rcp.css;

import org.eclipse.e4.ui.css.core.dom.properties.ICSSPropertyHandler;
import org.eclipse.e4.ui.css.core.engine.CSSEngine;
import org.eclipse.e4.ui.css.swt.properties.AbstractCSSPropertySWTHandler;
import org.eclipse.swt.widgets.Control;
import org.w3c.dom.css.CSSValue;

import aero.minova.rcp.css.widgets.MinovaSection;

/**
 * @author Wilfried Saak
 */
@SuppressWarnings("restriction")
public class MinovaSectionPropertyHandler extends AbstractCSSPropertySWTHandler implements ICSSPropertyHandler {
	/**
	 * definiert die Breite eines Text Widgets für Datumsangaben.
	 */
	public final static String DATE_WIDTH = "date-width";
	/**
	 * definiert die Breite eines Text Widgets für Zeitpunktangaben (Datum und Zeit).
	 */
	public final static String DATE_TIME_WIDTH = "date-time-width";
	/**
	 * definiert die Breite eines Text Widgets für Zahlenwerte.
	 */
	public final static String NUMBER_WIDTH = "number-width";
	/**
	 * definiert die Höhe einer Zeile in der Section (inkl. Margin)
	 */
	public final static String ROW_HEIGHT = "row-height";
	/**
	 * definiert den Abstand zwischen 2 Widgets (horizontal und vertikal)
	 */
	public final static String SECTION_SPACING = "section-spacing";
	/**
	 * definiert die Breite eines Text Widgets für Texte. Es definiert auch die Breite der Labels.
	 */
	public final static String TEXT_WIDTH = "text-width";
	/**
	 * definiert die Breite eines Text Widgets für Zeitangaben.
	 */
	public final static String TIME_WIDTH = "time-width";

	@Override
	protected void applyCSSProperty(Control control, String property, CSSValue value, String pseudo, CSSEngine engine) throws Exception {
		if (!(control instanceof MinovaSection)) return;
		if (value.getCssValueType() != CSSValue.CSS_PRIMITIVE_VALUE) return;

		MinovaSection minovaSection = (MinovaSection) control;
		String val = value.getCssText();
		// bisher sind alles int-Werte
		int pixel = (int) Float.parseFloat(val.substring(0, val.length() - 2));

		switch (property) {
		case DATE_WIDTH:
			minovaSection.getCssStyler().setDateWidth(pixel);
			break;
		case DATE_TIME_WIDTH:
			minovaSection.getCssStyler().setDateTimeWidth(pixel);
			break;
		case NUMBER_WIDTH:
			minovaSection.getCssStyler().setNumberWidth(pixel);
			break;
		case ROW_HEIGHT:
			minovaSection.getCssStyler().setRowHeight(pixel);
			break;
		case SECTION_SPACING:
			minovaSection.getCssStyler().setSectionSpacing(pixel);
			break;
		case TEXT_WIDTH:
			minovaSection.getCssStyler().setTextWidth(pixel);
			break;
		case TIME_WIDTH:
			minovaSection.getCssStyler().setTimeWidth(pixel);
			break;
		default:
		}
	}

	@Override
	protected String retrieveCSSProperty(Control control, String property, String pseudo, CSSEngine engine) throws Exception {
		switch (property) {
		case DATE_WIDTH:
			return "" + ((MinovaSection) control).getCssStyler().getDateWidth();
		case DATE_TIME_WIDTH:
			return "" + ((MinovaSection) control).getCssStyler().getDateTimeWidth();
		case NUMBER_WIDTH:
			return "" + ((MinovaSection) control).getCssStyler().getNumberWidth();
		case ROW_HEIGHT:
			return "" + ((MinovaSection) control).getCssStyler().getRowHeight();
		case SECTION_SPACING:
			return "" + ((MinovaSection) control).getCssStyler().getSectionSpacing();
		case TEXT_WIDTH:
			return "" + ((MinovaSection) control).getCssStyler().getTextWidth();
		case TIME_WIDTH:
			return "" + ((MinovaSection) control).getCssStyler().getTimeWidth();
		default:
			return null;
		}
	}

}
