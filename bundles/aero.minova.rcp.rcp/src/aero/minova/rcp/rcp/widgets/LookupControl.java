package aero.minova.rcp.rcp.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.ui.css.swt.CSSSWTConstants;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;

/**
 * Ein selbst definiertes UI-Element zur Anzeige von Lookups
 *
 * @author saak, wild
 * @since 11.0.0
 */
public class LookupControl extends Composite {
	private final class ContentProposalAdapterExtension extends ContentProposalAdapter {
		private ContentProposalAdapterExtension(Control control, IControlContentAdapter controlContentAdapter,
				IContentProposalProvider proposalProvider, KeyStroke keyStroke, char[] autoActivationCharacters) {
			super(control, controlContentAdapter, proposalProvider, keyStroke, autoActivationCharacters);
		}

		@Override
		public void openProposalPopup() {
			super.openProposalPopup();
		}

	}

	protected Text textControl;
	protected Label twistie;
	private Label description;
	private SimpleContentProposalProvider simpleContentProposalProvider;
	private ContentProposalAdapterExtension contentProposalAdapter;

	public LookupControl(Composite parent, int style) {
		super(parent, style);

		addLookupTwistie(style);
		addTextControl(style | SWT.BORDER);
		addLayout();
	}

	protected void addLayout() {
		FormLayout fl = new FormLayout();
		setLayout(fl);

		FormData fd;
		fd = new FormData();
		fd.right = new FormAttachment(100, -5);
		fd.top = new FormAttachment(textControl, 0, SWT.CENTER);
		twistie.setLayoutData(fd);
		fd = new FormData();
		fd.right = new FormAttachment(100, 0);
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, 0);
		fd.bottom = new FormAttachment(100, 0);
		textControl.setLayoutData(fd);
	}

	protected void addLookupTwistie(int style) {
		// org.eclipse.swt.widgets.Widget.PARENT_BACKGROUND is not visible
		twistie = new Label(this, style | SWT.NO_FOCUS | 1024);
		twistie.setText("▼");
		twistie.setToolTipText("Lookup.Twistie.ToolTip");
		twistie.setData(CSSSWTConstants.CSS_CLASS_NAME_KEY, "LookupTwistie");
	}

	protected void addTextControl(int style) {
		textControl = new Text(this, style);
		simpleContentProposalProvider = new SimpleContentProposalProvider();
		contentProposalAdapter = new ContentProposalAdapterExtension(textControl, new TextContentAdapter(),
				simpleContentProposalProvider, null, null);
		setData(CSSSWTConstants.CSS_CLASS_NAME_KEY, "LookupField");
	}

	public void setProposals(Table table) {
		List<String> helper = new ArrayList<>();
		for (Row row : table.getRows()) {
			if (row.getValue(table.getColumnIndex("Description")) != null) {
				String r = //
						row.getValue(table.getColumnIndex("KeyText")).getStringValue() + " ("
								+ row.getValue(table.getColumnIndex("Description")).getStringValue() + ")";
				helper.add(r);
			} else {
				helper.add(row.getValue(table.getColumnIndex("KeyText")).getStringValue());
			}
		}
		simpleContentProposalProvider.setProposals(helper.toArray(new String[0]));
		contentProposalAdapter.openProposalPopup();
	}

	public void addTwistieMouseListener(MouseListener ml) {
		twistie.addMouseListener(ml);
	}

	public String getText() {
		return textControl.getText();
	}

	public Text getTextControl() {
		return textControl;
	}

	public void setText(String string) {
		textControl.setText(string);
	}

	@Override
	public boolean setFocus() {
		return textControl.setFocus();
	}

	@Override
	public boolean forceFocus() {
		return false; // damit man rückwärts tabben kann!
	}

	@Override
	public boolean isFocusControl() {
		return textControl.isFocusControl();
	}

	@Override
	public void setFont(Font font) {
		if (!textControl.isDisposed()) {
			textControl.setFont(font);
		}
	}

	@Override
	public void addFocusListener(FocusListener listener) {
		textControl.addFocusListener(listener);
	}

	@Override
	public void removeFocusListener(FocusListener listener) {
		textControl.removeFocusListener(listener);
	}

	@Override
	public void addKeyListener(KeyListener listener) {
		textControl.addKeyListener(listener);
	}

	@Override
	public void removeKeyListener(KeyListener listener) {
		textControl.removeKeyListener(listener);
	}

	@Override
	public void addTraverseListener(TraverseListener listener) {
		textControl.addTraverseListener(listener);
	}

	@Override
	public void removeTraverseListener(TraverseListener listener) {
		textControl.removeTraverseListener(listener);
	}

	public Label getDescription() {
		return description;
	}

	public void setDescription(Label description) {
		this.description = description;
	}
}