package aero.minova.rcp.preferencewindow.control;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import aero.minova.rcp.dataservice.IDataService;

public class SendLogsButton extends CustomPWWidget {

	private TranslationService translationService;
	private IDataService dataService;

	public SendLogsButton(String label, String propertyKey, TranslationService translationService, IDataService dataService) {
		super(label, propertyKey, 2, false);
		this.translationService = translationService;
		this.dataService = dataService;
	}

	@Override
	protected Control build(Composite parent) {
		final Label label = new Label(parent, SWT.NONE);

		label.setText(getLabel());

		addControl(label);
		final GridData labelGridData = new GridData(SWT.END, SWT.CENTER, false, false);
		labelGridData.horizontalIndent = getIndent();
		label.setLayoutData(labelGridData);

		final Button button = new Button(parent, SWT.PUSH);
		button.setText(translationService.translate("@Action.Send", null));

		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dataService.sendLogs();
			}
		});

		return button;
	}

	@Override
	protected void check() {}

}
