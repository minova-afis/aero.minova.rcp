package aero.minova.rcp.preferencewindow.control;

import javax.inject.Inject;

import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.preferences.WorkspaceAccessPreferences;

public class TextButtonForDefaultWorkspace extends CustomPWWidget {

	@Inject
	Logger logger;

	TranslationService translationService;

	/**
	 * Constructor
	 *
	 * @param label
	 *            associated label
	 * @param propertyKey
	 *            associated key
	 */
	public TextButtonForDefaultWorkspace(final String label, final String tooltip, final String propertyKey, final TranslationService translationService) {
		super(label, tooltip, propertyKey, 2, false);
		this.translationService = translationService;
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#build(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control build(final Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setText(getLabel());
		label.setToolTipText(getTooltip());
		addControl(label);
		final GridData labelGridData = new GridData(SWT.END, SWT.CENTER, false, false);
		labelGridData.horizontalIndent = getIndent();
		label.setLayoutData(labelGridData);

		Composite cmp = new Composite(parent, SWT.NONE);
		cmp.setLayout(new GridLayout(2, false));
		addControl(cmp);

		final Text text = new Text(cmp, SWT.BORDER | SWT.READ_ONLY);
		text.setToolTipText(getTooltip());
		addControl(text);
		final GridData textGridData = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
		textGridData.widthHint = 250;
		text.setLayoutData(textGridData);

		// Auslesen des PrimaryWorksapces
		if (!WorkspaceAccessPreferences.getSavedPrimaryWorkspaceAccessData(logger).isEmpty()) {
			ISecurePreferences prefs = WorkspaceAccessPreferences.getSavedPrimaryWorkspaceAccessData(logger).get();
			try {
				String profil = prefs.get(WorkspaceAccessPreferences.PROFILE, null);
				text.setText(profil);
			} catch (StorageException e1) {
				e1.printStackTrace();
			}
		} else {
			text.setText(translationService.translate("@msg.NotSet", null));
		}

		final Button button = new Button(cmp, SWT.PUSH);
		final GridData buttonGridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
		button.setText(translationService.translate("@Action.Reset", null));
		button.setLayoutData(buttonGridData);

		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				text.setText(translationService.translate("@msg.NotSet", null));
				if (!WorkspaceAccessPreferences.getSavedPrimaryWorkspaceAccessData(logger).isEmpty()) {
					WorkspaceAccessPreferences.resetDefaultWorkspace(logger);
				}
			}
		});

		return button;
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
