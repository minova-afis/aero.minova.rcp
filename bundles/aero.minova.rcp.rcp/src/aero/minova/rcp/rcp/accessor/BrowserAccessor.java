package aero.minova.rcp.rcp.accessor;

import aero.minova.rcp.model.form.IBrowserAccessor;
import aero.minova.rcp.model.form.MBrowser;
import aero.minova.rcp.rcp.widgets.BrowserSection;

public class BrowserAccessor implements IBrowserAccessor {
	
	BrowserSection browserSection;
	MBrowser mBrowser;
	
	public BrowserSection getBrowserSection() {
		return browserSection;
	}
	public void setBrowserSection(BrowserSection browserSection) {
		this.browserSection = browserSection;
	}
	public MBrowser getmBrowser() {
		return mBrowser;
	}
	public void setmBrowser(MBrowser mBrowser) {
		this.mBrowser = mBrowser;
	}
}
