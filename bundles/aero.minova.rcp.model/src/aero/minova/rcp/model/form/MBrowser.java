package aero.minova.rcp.model.form;

import org.eclipse.swt.graphics.Image;

public class MBrowser {
	
	public MBrowser(String id) {
		this.id = id;
	}
	
	private String title;
	private String id;
	private Image icon;
	private MSection mSection;
	private IBrowserAccessor browserAccessor;

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Image getIcon() {
		return icon;
	}
	public void setIcon(Image icon) {
		this.icon = icon;
	}
	public MSection getmSection() {
		return mSection;
	}
	public void setmSection(MSection mSection) {
		this.mSection = mSection;
	}
	public IBrowserAccessor getBrowserAccessor() {
		return browserAccessor;
	}
	public void setBrowserAccessor(IBrowserAccessor browserAccessor) {
		this.browserAccessor = browserAccessor;
	}
}
