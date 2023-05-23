package aero.minova.rcp.rcp.widgets;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import aero.minova.rcp.dataservice.ImageUtil;

public class AboutDialog extends TitleAreaDialog {
	private Font lizenzFont;
	private Font infoFont;
	private String version;
	private LocalResourceManager resManager;

	public AboutDialog(Shell parentShell, String versionString) {
		super(parentShell);
		this.version = versionString;
		resManager = new LocalResourceManager(JFaceResources.getResources(), parentShell);
		lizenzFont = resManager.createFont(FontDescriptor.createFrom(new FontData("Arial", 12, SWT.NORMAL)));
		infoFont = resManager.createFont(FontDescriptor.createFrom(new FontData("Arial", 14, SWT.NORMAL)));
		setTitleAreaColor(new RGB(236, 236, 236));
	}

	@Override
	public void create() {
		super.create();

		Image min = resManager.createImage(ImageUtil.getImageDefault("MINOVAT.png"));
		setTitleImage(min);

		if (getShell() != null) {
			getShell().pack();
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		this.getShell().setText("About");

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		parent.setLayout(layout);
		GridData gd = null;

		Composite address = createAddress(parent);
		gd = new GridData();
		address.setLayoutData(gd);

		Composite info = createAppInfo(parent);
		gd = new GridData();
		gd.verticalAlignment = SWT.TOP;
		info.setLayoutData(gd);

		return parent;
	}

	protected Composite createAppInfo(Composite parent) {
		Composite info = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 1;
		info.setLayout(layout);
		Image wfc = resManager.createImage(ImageUtil.getImageDescriptor("WFC.Application", 128));
		createText(info, wfc, "WebFatClient CoreApplicationService", infoFont, IMessageProvider.INFORMATION);
		return info;
	}

	protected Composite createAddress(Composite parent) {
		Composite info = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 1;
		info.setLayout(layout);

		Image home = resManager.createImage(ImageUtil.getImageDefault("home.png"));
		createText(info, home, "MINOVA Information Services GmbH", lizenzFont, IMessageProvider.INFORMATION);
		createText(info, null, "Leightonstraße 2", lizenzFont, IMessageProvider.INFORMATION);
		createText(info, null, "97074 Würzburg", lizenzFont, IMessageProvider.INFORMATION);
		createText(info, null, "AG Würzburg HRB 7625", lizenzFont, IMessageProvider.INFORMATION);

		Image phone = resManager.createImage(ImageUtil.getImageDefault("phone.png"));
		createText(info, phone, "+49 (0) 931 - 32235 - 19", lizenzFont, IMessageProvider.INFORMATION);
		Image fax = resManager.createImage(ImageUtil.getImageDefault("fax.png"));
		createText(info, fax, "+49 (0) 931 - 32235 - 55", lizenzFont, IMessageProvider.INFORMATION);
		Image mail = resManager.createImage(ImageUtil.getImageDefault("mail.png"));
		createText(info, mail, "service@minova.de", lizenzFont, IMessageProvider.INFORMATION);
		Image versionImg = resManager.createImage(ImageUtil.getImageDefault("version.png"));
		createText(info, versionImg, version, lizenzFont, IMessageProvider.INFORMATION);

		return info;
	}

	protected Label createText(Composite parent, String text) {
		return createText(parent, null, text, null, IMessageProvider.INFORMATION);
	}

	/**
	 * @param parent
	 * @param img
	 * @param text
	 * @param font
	 * @param color
	 *            (not used)
	 * @return
	 */
	protected Label createText(Composite parent, Image img, String text, Font font, int color) {
		Label lblImg = new Label(parent, SWT.NONE);
		lblImg.setSize(16, 16);
		if (img != null) {
			lblImg.setImage(img);
		}
		Label toRet = new Label(parent, SWT.NONE);
		toRet.setText(text);
		if (font != null) {
			toRet.setFont(font);
		}
		return toRet;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = SWT.CENTER;
		parent.setLayoutData(gridData);

		createOkButton(parent, OK, "OK");
		parent.pack();
	}

	protected Button createOkButton(Composite parent, int id, String label) {
		// increment the number of columns in the button bar
		((GridLayout) parent.getLayout()).numColumns++;
		Button button = new Button(parent, SWT.PUSH);
		button.setText(label);
		button.setFont(JFaceResources.getDialogFont());
		button.setData(Integer.valueOf(id));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				okPressed();
			}
		});

		parent.pack();
		Shell shell = parent.getShell();
		if (shell != null) {
			shell.setDefaultButton(button);
			shell.pack();
		}

		setButtonLayoutData(button);
		return button;
	}

	@Override
	protected boolean isResizable() {
		return false;
	}
}