package aero.minova.rcp.preferencewindow.control;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.jface.widgets.ButtonFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import aero.minova.rcp.dataservice.IDataService;

public class SendLogsButton extends CustomPWWidget {

	private TranslationService translationService;
	private IDataService dataService;

	public SendLogsButton(String label, final String tooltip, String propertyKey, TranslationService translationService, IDataService dataService) {
		super(label, tooltip, propertyKey, 2, false);
		this.translationService = translationService;
		this.dataService = dataService;
	}

	@Override
	protected Control build(Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setText(getLabel());
		label.setToolTipText(getTooltip());
		addControl(label);
		final GridData labelGridData = new GridData(SWT.END, SWT.CENTER, false, false);
		labelGridData.horizontalIndent = getIndent();
		label.setLayoutData(labelGridData);

		return ButtonFactory.newButton(SWT.PUSH)//
				.text(translationService.translate("@Action.Send", null))//
				.onSelect(e -> dataService.sendLogs()).create(parent);
	}

	@Override
	protected void check() {}
}
