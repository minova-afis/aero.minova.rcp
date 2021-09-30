package aero.minova.rcp.rcp.parts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class TestDetail extends Composite {
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private Text text;
	private Text text_1;
	private Text text_2;
	private Text text_3;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public TestDetail(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		
		Section sctnNewSection = formToolkit.createSection(this, Section.TITLE_BAR);
		FormData fd_sctnNewSection = new FormData();
		fd_sctnNewSection.bottom = new FormAttachment(100, -446);
		fd_sctnNewSection.top = new FormAttachment(0);
		fd_sctnNewSection.right = new FormAttachment(0, 450);
		fd_sctnNewSection.left = new FormAttachment(0);
		sctnNewSection.setLayoutData(fd_sctnNewSection);
		formToolkit.paintBordersFor(sctnNewSection);
		sctnNewSection.setText("Head");
		
		Composite composite = formToolkit.createComposite(sctnNewSection, SWT.NONE);
		formToolkit.paintBordersFor(composite);
		sctnNewSection.setClient(composite);
		composite.setLayout(new FormLayout());
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		formToolkit.adapt(lblNewLabel, true, true);
		lblNewLabel.setText("Matchcode");
		
		text = new Text(composite, SWT.BORDER);
		fd_lblNewLabel.bottom = new FormAttachment(text, 0, SWT.BOTTOM);
		fd_lblNewLabel.right = new FormAttachment(text, -6);
		FormData fd_text = new FormData();
		fd_text.top = new FormAttachment(0, 8);
		fd_text.left = new FormAttachment(0, 96);
		text.setLayoutData(fd_text);
		formToolkit.adapt(text, true, true);
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		fd_text.right = new FormAttachment(lblNewLabel_1, -4);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.top = new FormAttachment(0, 9);
		fd_lblNewLabel_1.left = new FormAttachment(0, 185);
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		formToolkit.adapt(lblNewLabel_1, true, true);
		
		Label lblDescription = new Label(composite, SWT.NONE);
		FormData fd_lblDescription = new FormData();
		fd_lblDescription.top = new FormAttachment(lblNewLabel, 6);
		fd_lblDescription.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		lblDescription.setLayoutData(fd_lblDescription);
		lblDescription.setText("Description");
		lblDescription.setBounds(0, 0, 78, 23);
		formToolkit.adapt(lblDescription, true, true);
		
		text_1 = new Text(composite, SWT.BORDER);
		FormData fd_text_1 = new FormData();
		fd_text_1.left = new FormAttachment(lblDescription, 4);
		fd_text_1.right = new FormAttachment(100, -10);
		fd_text_1.top = new FormAttachment(text, 5);
		text_1.setLayoutData(fd_text_1);
		formToolkit.adapt(text_1, true, true);
		
		Label lblNewLabel_2 = new Label(composite, SWT.NONE);
		FormData fd_lblNewLabel_2 = new FormData();
		fd_lblNewLabel_2.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		lblNewLabel_2.setLayoutData(fd_lblNewLabel_2);
		formToolkit.adapt(lblNewLabel_2, true, true);
		lblNewLabel_2.setText("Last Date");
		
		Label lblNewLabel_3 = new Label(composite, SWT.NONE);
		FormData fd_lblNewLabel_3 = new FormData();
		fd_lblNewLabel_3.top = new FormAttachment(text_1, 6);
		lblNewLabel_3.setLayoutData(fd_lblNewLabel_3);
		formToolkit.adapt(lblNewLabel_3, true, true);
		lblNewLabel_3.setText("Valid Until");
		
		Label lblNewLabel_4 = new Label(composite, SWT.NONE);
		FormData fd_lblNewLabel_4 = new FormData();
		fd_lblNewLabel_4.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		lblNewLabel_4.setLayoutData(fd_lblNewLabel_4);
		formToolkit.adapt(lblNewLabel_4, true, true);
		lblNewLabel_4.setText("Married");
		
		Label lblNewLabel_5 = new Label(composite, SWT.NONE);
		FormData fd_lblNewLabel_5 = new FormData();
		fd_lblNewLabel_5.top = new FormAttachment(lblNewLabel_4, 6);
		fd_lblNewLabel_5.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		lblNewLabel_5.setLayoutData(fd_lblNewLabel_5);
		formToolkit.adapt(lblNewLabel_5, true, true);
		lblNewLabel_5.setText("Vehicle");
		
		text_2 = new Text(composite, SWT.BORDER);
		fd_lblNewLabel_3.left = new FormAttachment(text_2, 50);
		fd_lblNewLabel_2.top = new FormAttachment(text_2, 0, SWT.TOP);
		FormData fd_text_2 = new FormData();
		fd_text_2.top = new FormAttachment(text_1, 6);
		fd_text_2.right = new FormAttachment(text, 0, SWT.RIGHT);
		text_2.setLayoutData(fd_text_2);
		formToolkit.adapt(text_2, true, true);
		
		text_3 = new Text(composite, SWT.BORDER);
		FormData fd_text_3 = new FormData();
		fd_text_3.top = new FormAttachment(text_1, 6);
		fd_text_3.left = new FormAttachment(lblNewLabel_3, 6);
		text_3.setLayoutData(fd_text_3);
		formToolkit.adapt(text_3, true, true);
		
		Button btnCheckButton = new Button(composite, SWT.RADIO);
		fd_lblNewLabel_4.bottom = new FormAttachment(btnCheckButton, 0, SWT.BOTTOM);
		FormData fd_btnCheckButton = new FormData();
		fd_btnCheckButton.top = new FormAttachment(text_2, 6);
		fd_btnCheckButton.left = new FormAttachment(text, 0, SWT.LEFT);
		btnCheckButton.setLayoutData(fd_btnCheckButton);
		formToolkit.adapt(btnCheckButton, true, true);
		btnCheckButton.setText("Check Button");
		
		CCombo combo = new CCombo(composite, SWT.BORDER);
		FormData fd_combo = new FormData();
		fd_combo.top = new FormAttachment(btnCheckButton, 6);
		fd_combo.right = new FormAttachment(text, 0, SWT.RIGHT);
		combo.setLayoutData(fd_combo);
		formToolkit.adapt(combo);
		formToolkit.paintBordersFor(combo);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
