package aero.minova.rcp.preferencewindow.control;

import static org.eclipse.jface.dialogs.PlainMessageDialog.getBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.jface.dialogs.PlainMessageDialog;
import org.eclipse.jface.widgets.ButtonFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.IDataService;

public class ResetUIButton extends CustomPWWidget {

	private TranslationService translationService;
	private IWorkbench workbench;
	private IDataService dataService;

	ILog logger = Platform.getLog(this.getClass());

	public ResetUIButton(String label, final String tooltip, String propertyKey, TranslationService translationService, IWorkbench workbench,
			IDataService dataService) {
		super(label, tooltip, propertyKey, 2, false);
		this.translationService = translationService;
		this.workbench = workbench;
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
				.text(translationService.translate("@Action.Reset", null))//
				.onSelect(e -> resetUI()).create(parent);
	}

	private void resetUI() {
		Shell activeShell = Display.getCurrent().getActiveShell();

		PlainMessageDialog confirmRestart = getBuilder(activeShell, translationService.translate("@Action.Restart", null))
				.buttonLabels(List.of(translationService.translate("@Action.Restart", null), translationService.translate("@Abort", null)))
				.message(translationService.translate("@Preferences.ResetUIMessage", null)).build();

		int openConfirm = confirmRestart.open();
		if (openConfirm == 0) {
			try {
				Files.createFile(dataService.getStoragePath().resolve(Constants.RESET_UI_FILE_NAME));
				workbench.restart();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	@Override
	protected void check() {
		// Nothing to do
	}
}
