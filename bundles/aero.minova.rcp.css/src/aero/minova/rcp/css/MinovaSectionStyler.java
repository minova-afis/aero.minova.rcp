package aero.minova.rcp.css;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import aero.minova.rcp.css.widgets.MinovaSection;
import aero.minova.rcp.css.widgets.MinovaSectionData;
import aero.minova.rcp.util.OSUtil;

/**
 * Diese Klasse kann MinovaSections stylen.
 *
 * @author Wilfried Saak
 */
public class MinovaSectionStyler implements ICssStyler {
	private int dateWidth = ICssStyler.CSS_DATE_WIDTH;
	private int dateTimeWidth = ICssStyler.CSS_DATE_TIME_WIDTH;
	private int numberWidth = ICssStyler.CSS_NUMBER_WIDTH;
	private int rowHeight = ICssStyler.CSS_ROW_HEIGHT; // Default aus default.css
	private int sectionSpacing = ICssStyler.CSS_SECTION_SPACING; // Default aus default.css
	private int textWidth = ICssStyler.CSS_TEXT_WIDTH; // Default aus default.css
	private int timeWidth = ICssStyler.CSS_TIME_WIDTH; // Default aus default.css

	/**
	 * Das Composite, dass wir stylen wollen
	 */
	private MinovaSection section;

	/**
	 * @param section
	 *            Die Section, die durch diese Klasse gestyled wird.
	 */
	public MinovaSectionStyler(MinovaSection section) {
		this.section = section;
	}

	/**
	 * Liefert eine Liste mit allen Controls der Section, die auch über {@link CssData} verfügen.
	 *
	 * @return Liste mit den Controls, die über {@link CssData} verfügen.
	 */
	private List<Control> getStylableControls() {
		List<Control> controls = new ArrayList<>();
		for (Control control : section.getChildren()) {
			if (control instanceof Composite composite) {
				for (Control c : composite.getChildren()) {
					if (c.getData(CssData.CSSDATA_KEY) != null) {
						controls.add(c);
					}
				}
			}
		}
		return controls;
	}

	/**
	 * @return die {@link #MinovaSection}, die gestyled wird.
	 */
	public MinovaSection getSection() {
		return section;
	}

	/**
	 * Liefert den Abstand von 2 Elementen in einer MinovaSection mit max. 4 Spalten. Dieser Abstand wird zwischen den Spalten, am Anfang und am Ende
	 * eingehalten. Daraus ergibt sich eine Gesamtebreite der {@link MinovaSectionStyler#section} von<br/>
	 * 5 * {@link #sectionSpacing} + 4 * {@link #textWidth}
	 *
	 * @return Horizontaler Abstand zwischen 2 Elementen in px
	 */
	@Override
	public int getSectionSpacing() {
		return sectionSpacing;
	}

	/**
	 * setzt die Breite der Section
	 */
	private void setSectionWidth() {
		((MinovaSectionData) section.getLayoutData()).setWidth(getSectionWidth());
	}

	@Override
	public void style() {
		setSectionWidth();
		styleSection();
	}

	/**
	 * Das Styling ist für eine Standard-Section mit 4 Spalten durchzuführen.
	 */
	private void styleSection() {
		for (Control c : getStylableControls()) {
			FormData fd = (FormData) c.getLayoutData();
			CssData cd = (CssData) c.getData(CssData.CSSDATA_KEY);

			if (fd == null || cd == null || cd.cssType == null) {
				return;
			}

			switch (cd.cssType) {
			case NUMBER_FIELD:
				styleNumberField(fd, cd);
				break;
			case DATE_FIELD:
				styleDateField(fd, cd);
				break;
			case TEXT_FIELD:
				styleTextField(fd, cd);
				break;
			case TIME_FIELD:
				styleTimeField(fd, cd);
				break;
			case DATE_TIME_FIELD:
				styleDateTimeField(fd, cd);
				break;
			case LABEL_TEXT_FIELD:
				styleLabelTextField(fd, cd);
				break;
			case LABEL_TEXT_BOLD_FIELD:
				styleLabelTextBoldField(fd, cd);
				break;
			case RADIO_FIELD:
				styleRadioField(fd, cd, c);
				break;
			case LABEL:
			default:
				styleLabel(fd);
				break;
			}
		}
	}

	private void styleLabelTextField(FormData fd, CssData cd) {
		fd.right.offset = sectionSpacing * -1;
		fd.top.offset = cd.row * rowHeight;
	}

	private void styleLabelTextBoldField(FormData fd, CssData cd) {
		styleLabelTextField(fd, cd);
	}

	private void styleLabel(FormData fd) {
		fd.right.offset = sectionSpacing * -1;
	}

	private void styleDateField(FormData fd, CssData cd) {
		fd.top.offset = cd.row * rowHeight;
		fd.width = dateWidth;
	}

	private void styleNumberField(FormData fd, CssData cd) {
		fd.top.offset = cd.row * rowHeight;
		fd.width = numberWidth;
	}

	private void styleTextField(FormData fd, CssData cd) {
		if (cd == null) {
			return;
		}

		// Top
		fd.top.offset = cd.row * rowHeight;

		// Breite
		if (!cd.fill) {
			fd.width = textWidth - sectionSpacing * 2;
		}

		// Höhe
		if (cd.numberRowsSpanned > 1) {
			// Mehrzeilige Textfelder verhalten sich unter Mac und Windows unterschiedlich
			if (OSUtil.isMac()) {
				fd.height = rowHeight * cd.numberRowsSpanned - sectionSpacing;
				fd.top.offset = (int) (cd.row * rowHeight + sectionSpacing * 0.5);
			} else {
				fd.height = rowHeight * cd.numberRowsSpanned - sectionSpacing * 2;
			}
		}
	}

	private void styleRadioField(FormData fd, CssData cd, Control parent) {
		fd.top.offset = cd.row * rowHeight;
		Composite comp = (Composite) parent;
		int i = 0;
		for (Control child : comp.getChildren()) {
			if (child instanceof Button) {
				FormData childFD = (FormData) child.getLayoutData();
				childFD.top.offset = i / 3 * rowHeight + sectionSpacing;
				i++;
			}
		}
	}

	private void styleTimeField(FormData fd, CssData cd) {
		if (cd == null) {
			return;
		}

		// Top
		fd.top.offset = cd.row * rowHeight;

		// Breite
		fd.width = timeWidth;
	}

	private void styleDateTimeField(FormData fd, CssData cd) {
		fd.top.offset = cd.row * rowHeight;
		fd.width = dateTimeWidth;
	}

	@Override
	public int getDateWidth() {
		return dateWidth;
	}

	@Override
	public void setDateWidth(int dateWidth) {
		this.dateWidth = dateWidth;
		style();
	}

	@Override
	public int getDateTimeWidth() {
		return dateTimeWidth;
	}

	@Override
	public void setDateTimeWidth(int dateTimeWidth) {
		this.dateTimeWidth = dateTimeWidth;
		style();
	}

	@Override
	public int getNumberWidth() {
		return numberWidth;
	}

	@Override
	public void setNumberWidth(int width) {
		this.numberWidth = width;
		style();
	}

	@Override
	public int getRowHeight() {
		return rowHeight;
	}

	@Override
	public void setRowHeight(int rowHeight) {
		this.rowHeight = rowHeight;
	}

	@Override
	public int getSectionWidth() {
		return 4 * textWidth + 5 * sectionSpacing;
	}

	@Override
	public void setSectionSpacing(int sectionSpacing) {
		this.sectionSpacing = sectionSpacing;
	}

	@Override
	public int getTextWidth() {
		return textWidth;
	}

	@Override
	public void setTextWidth(int textWidth) {
		this.textWidth = textWidth;
		style();
	}

	@Override
	public int getTimeWidth() {
		return timeWidth;
	}

	@Override
	public void setTimeWidth(int timeWidth) {
		this.timeWidth = timeWidth;
		style();
	}
}