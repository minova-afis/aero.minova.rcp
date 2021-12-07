package aero.minova.rcp.preferencewindow.control;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.nebula.widgets.opal.commons.ResourceManager;
import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Text;

public class CustomPWFontChooser extends CustomPWChooser {
	private FontData fontData;

	/**
	 * Constructor
	 *
	 * @param label       associated label
	 * @param propertyKey associated key
	 */
	public CustomPWFontChooser(final String label, @Optional  String tooltip, final String propertyKey, TranslationService translationService) {
		super(label, tooltip, propertyKey, translationService);
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#check()
	 */
	@Override
	public void check() {
		final Object value = PreferenceWindow.getInstance().getValueFor(getCustomPropertyKey());
		if (value == null) {
			PreferenceWindow.getInstance().setValue(getCustomPropertyKey(), null);
		} else {
			if (!(value instanceof FontData)) {
				throw new UnsupportedOperationException("The property '" + getCustomPropertyKey()
						+ "' has to be a FontData because it is associated to a font chooser");
			}

		}
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWChooser#setButtonAction(org.eclipse.swt.widgets.Text,
	 *      org.eclipse.swt.widgets.Button)
	 */
	@Override
	protected void setButtonAction(final Text text, final Button button) {
		fontData = (FontData) PreferenceWindow.getInstance().getValueFor(getCustomPropertyKey());

		button.addListener(SWT.Selection, event -> {
			final FontDialog dialog = new FontDialog(text.getShell());
			final FontData result = dialog.open();
			if (result != null && result.getName() != null && !"".equals(result.getName().trim())) {
				fontData = result;
				PreferenceWindow.getInstance().setValue(getCustomPropertyKey(), result);
				text.setText(buildFontInformation());
			}
		});
		text.setText(buildFontInformation());
	}

	/**
	 * @return a string that contains data about the choosen font
	 */
	protected String buildFontInformation() {
		fontData = (FontData) PreferenceWindow.getInstance().getValueFor(getCustomPropertyKey());
		final StringBuilder sb = new StringBuilder();
		if (fontData != null) {
			sb.append(fontData.getName()).append(",").append(fontData.getHeight()).append(" pt");
			if ((fontData.getStyle() & SWT.BOLD) == SWT.BOLD) {
				sb.append(", ").append(ResourceManager.getLabel(ResourceManager.BOLD));
			}
			if ((fontData.getStyle() & SWT.ITALIC) == SWT.ITALIC) {
				sb.append(", ").append(ResourceManager.getLabel(ResourceManager.ITALIC));
			}
		}
		return sb.toString();
	}

}
