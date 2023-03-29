package aero.minova.rcp.preferencewindow.control;

import static org.eclipse.jface.dialogs.PlainMessageDialog.getBuilder;

import java.awt.Desktop;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.jface.dialogs.PlainMessageDialog;
import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dataservice.internal.FileUtil;

public class TextButtonForCurrentWorkspace extends CustomPWWidget {

	ILog logger = Platform.getLog(this.getClass());

	IEclipseContext context;
	IDataService dataService;
	TranslationService translationService;
	IWorkbench workbench;
	IStylingEngine engine;

	/**
	 * Constructor
	 *
	 * @param label
	 *            associated label
	 * @param propertyKey
	 *            associated key
	 * @param workbench
	 * @param iEclipseContext
	 * @param dataService2
	 */
	public TextButtonForCurrentWorkspace(final String label, final String tooltip, final String propertyKey, IEclipseContext context) {
		super(label, tooltip, propertyKey, 2, false);
		this.context = context;
		this.translationService = context.get(TranslationService.class);
		this.dataService = context.get(IDataService.class);
		this.engine = context.get(IStylingEngine.class);
		this.workbench = context.get(IWorkbench.class);
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#build(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control build(final Composite parent) {

		// Aktueller Workspace
		Composite cmp = createTextFieldWithLabel(parent, getLabel(), getTooltip(), dataService.getStoragePath().toAbsolutePath().toString());

		// Workspace Öffnen Knopf
		final Button openButton = new Button(cmp, SWT.PUSH);
		GridData buttonGridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
		openButton.setText(translationService.translate("@Action.Open", null));
		openButton.setLayoutData(buttonGridData);

		openButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					Desktop.getDesktop().open(dataService.getStoragePath().toAbsolutePath().toFile());
				} catch (IOException e1) {
					logger.error(e1.getMessage(), e1);
				}
			}
		});

		// Workspace Löschen Knopf
		final Button deleteButton = new Button(cmp, SWT.PUSH);
		buttonGridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
		deleteButton.setText(translationService.translate("@Action.Delete", null));
		deleteButton.setLayoutData(buttonGridData);

		deleteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell activeShell = Display.getCurrent().getActiveShell();

				PlainMessageDialog confirmRestart = getBuilder(activeShell, translationService.translate("@Action.Restart", null))
						.buttonLabels(List.of(translationService.translate("@Action.Restart", null), translationService.translate("@Abort", null)))
						.message(translationService.translate("@msg.WFCDeleteWorkspaceRestart", null)).build();

				if (confirmRestart.open() == 0) {
					context.set(IWorkbench.PERSIST_STATE, false);
					FileUtil.deleteDir(dataService.getStoragePath().toAbsolutePath().toFile());
					workbench.restart();
				}
			}
		});

		// Aktueller User
		createTextFieldWithLabel(parent, translationService.translate("@Preferences.CurrentUser", null), null, dataService.getUserName());

		// Aktuelle Connection
		createTextFieldWithLabel(parent, translationService.translate("@Preferences.CurrentConnection", null), null, dataService.getServer().toString());

		return deleteButton;
	}

	private Composite createTextFieldWithLabel(final Composite parent, String labelText, String tooltipText, String textFieldContent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(labelText);
		label.setToolTipText(tooltipText);
		addControl(label);
		GridData labelGridData = new GridData(SWT.END, SWT.CENTER, false, false);
		labelGridData.horizontalIndent = getIndent();
		label.setLayoutData(labelGridData);

		Composite cmp = new Composite(parent, SWT.NONE);
		cmp.setLayout(new GridLayout(3, false));
		addControl(cmp);

		Text text = new Text(cmp, SWT.BORDER | SWT.READ_ONLY);
		text.setToolTipText(tooltipText);
		addControl(text);
		GridData textGridData = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
		textGridData.widthHint = 250;
		text.setLayoutData(textGridData);
		engine.setClassname(text, Constants.CSS_READONLY);

		text.setText(textFieldContent);

		return cmp;
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#check()
	 */
	@Override
	public void check() {
		final Object value = PreferenceWindow.getInstance().getValueFor(getCustomPropertyKey());
		if (value == null) {
			PreferenceWindow.getInstance().setValue(getCustomPropertyKey(), Boolean.valueOf(false));
		} else {
			if (!(value instanceof Boolean)) {
				throw new UnsupportedOperationException(
						"The property '" + getCustomPropertyKey() + "' has to be a Boolean because it is associated to a checkbox");
			}
		}
	}

}
