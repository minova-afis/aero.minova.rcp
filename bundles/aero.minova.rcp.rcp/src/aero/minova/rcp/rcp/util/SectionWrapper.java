package aero.minova.rcp.rcp.util;

import java.util.ArrayList;
import java.util.List;

import aero.minova.rcp.form.model.xsd.Browser;
import aero.minova.rcp.form.model.xsd.Grid;
import aero.minova.rcp.form.model.xsd.Head;
import aero.minova.rcp.form.model.xsd.Page;

/**
 * Diese Klasse ermöglicht es unabhängig von der Art der Sektion, an die Attribute dieser zu gelangen.
 * 
 * @author bauer
 */
public class SectionWrapper {

	private Object section;
	private boolean isHead = false;
	private boolean isOP = false;
	private String formTitle;
	private String formSuffix;
	private String id;
	private String icon;
	private boolean isVisible;

	public Object getSection() {
		return section;
	}

	public void setSection(Object headOrPageOrGrid) {
		this.section = headOrPageOrGrid;
	}

	public boolean isHead() {
		return isHead;
	}

	public void setHead(boolean isHead) {
		this.isHead = isHead;
	}

	public boolean isOP() {
		return isOP;
	}

	public void setOP(boolean isOP) {
		this.isOP = isOP;
	}

	public String getFormTitle() {
		return formTitle;
	}

	public void setFormTitle(String formTitle) {
		this.formTitle = formTitle;
	}

	public String getFormSuffix() {
		return formSuffix;
	}

	public void setFormSuffix(String formSuffix) {
		this.formSuffix = formSuffix;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public SectionWrapper(Object section) {
		this.section = section;
		if (section instanceof Head) {
			isHead = true;
			id = "Head";
			isVisible = true;
		} else if (section instanceof Page) {
			id = ((Page) section).getId();
			icon = ((Page) section).getIcon();
			isVisible = ((Page) section).isVisible();
		} else if (section instanceof Grid) {
			id = ((Grid) section).getId();
			icon = ((Grid) section).getIcon();
			isVisible = true;
		} else {
			id = ((Browser) section).getId();
			icon = ((Browser) section).getIcon();
			isVisible = true;
		}
	}

	// Konstruktor für eine OptionPage.
	public SectionWrapper(Object section, boolean isOP, String formSuffix) {
		this(section);
		this.formSuffix = formSuffix;
		this.isOP = isOP;
		if (section instanceof Head && !isOP) {
			isHead = true;
		} else {
			isHead = false;
		}

		if (section instanceof Head && isOP) {
			id = formSuffix + ".Head";
		}
	}

	public SectionWrapper(Object headOrPageOrGrid, boolean isOP, String formSuffix, String formTitle, String icon) {
		this(headOrPageOrGrid, isOP, formSuffix);
		this.formTitle = formTitle;
		this.icon = icon;
	}

	public String getTranslationText() {
		if (isHead) {
			return "@Head";
		} else if (section instanceof Head && isOP) {
			return formTitle;
		} else if (section instanceof Grid) {
			return ((Grid) section).getTitle();
		} else if (section instanceof Page) {
			return ((Page) section).getText();
		} else if (section instanceof Browser) {
			return ((Browser) section).getTitle();
		}
		return "";
	}

	public List<Object> getFieldOrGrid() {
		if (section instanceof Head) {
			return ((Head) section).getFieldOrGrid();
		} else if (section instanceof Grid || section instanceof Browser) {
			// es existieren keine Felder, nur eine Table
			List<Object> mylistList = new ArrayList<>();
			mylistList.add(section);
			return mylistList;
		}
		return ((Page) section).getFieldOrGrid();
	}

}