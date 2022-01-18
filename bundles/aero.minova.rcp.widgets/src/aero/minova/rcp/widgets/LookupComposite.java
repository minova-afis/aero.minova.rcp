package aero.minova.rcp.widgets;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dataservice.internal.CacheUtil;
import aero.minova.rcp.model.LookupValue;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MLookupField;

public class LookupComposite extends Composite {

	private static final String SETTEXT_KEY = "org.eclipse.nebula.widgets.opal.textassist.TextAssist.settext";
	private final Text text;
	private final Shell popup;
	private final Table table;
	private LookupContentProvider contentProvider;
	private boolean useSingleClick = false;
	/**
	 * Das Label, das den Wert beschreibt. Die Description aus der Datenbank.
	 */
	private Label description;
	/**
	 * Das Label, das das Feld beschreibt
	 */
	private Label label;
	private List<LookupValue> popupValues;
	// True, wenn gerade eine Anfrage verarbeitet wird. Wenn auf das Label geklickt wird, während die Variable true ist, wird die Anfrage nicht ausgeführt
	private boolean gettingData = false;
	private String lastRequestState = "";
	private long lastRequestTime = 0;

	@Inject
	private TranslationService translationService;

	/**
	 * Constructs a new instance of this class given its parent and a style value describing its behavior and appearance.
	 */
	public LookupComposite(Composite parent, int style) {
		super(parent, SWT.NONE);
		setLayout(new FillLayout());

		this.contentProvider = new LookupContentProvider();
		this.contentProvider.setLookup(this);

		text = new Text(this, style);
		popup = new Shell(getDisplay(), SWT.ON_TOP);
		popup.setLayout(new FillLayout());
		table = new Table(popup, SWT.FULL_SELECTION | SWT.V_SCROLL);
		table.setLinesVisible(true);
		new TableColumn(table, SWT.NONE); // KeyText
		new TableColumn(table, SWT.NONE); // Description

		addTextListener();
		addTableListener();

		final int[] events = new int[] { SWT.Move, SWT.FocusOut };
		for (final int event : events) {
			getShell().addListener(event, e -> {
				popup.setVisible(false);
			});
		}

	}

	private void addTextListener() {
		text.addListener(SWT.KeyDown, createKeyDownListener());
		text.addListener(SWT.Modify, createModifyListener());
		text.addListener(SWT.FocusOut, createFocusOutListener());
		text.addDisposeListener(e -> closePopup());
		text.addFocusListener(new FocusAdapter() {

			// FocusLost aktualisiert das FieldValue sobald, das Field den Fokus verliert. Dabei ist die Art, wie der Fokus verloren geht egal (Mit Maus was
			// anderes auswählen oder mit Enter oder Tab nächstes Feld selektieren).
			//
			// Grund: Beim KeyBinding mit Enter wird das Event, dass den Wert aktualisiert, nicht ausgeführt, daher wird mit FocusLost der Wert automatisch
			// gesetzt, sobald das Feld verlassen wird.
			@Override
			public void focusLost(FocusEvent e) {
				if (popup.isVisible() && table.getSelectionIndex() != -1) {
					MField field = (MField) ((Control) e.widget).getParent().getData(Constants.CONTROL_FIELD);
					LookupValue lv = popupValues.get(table.getSelectionIndex());
					text.setText(lv.keyText);
					field.setValue(lv, true);
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
				text.selectAll();
			}
		});
	}

	private void addTableListener() {
		table.addListener(SWT.Selection, event -> {
			if (!useSingleClick) {
				return;
			}
//			text.setText(table.getSelection()[0].getText());
			MField field = (MField) getData(Constants.CONTROL_FIELD);
			LookupValue lv = popupValues.get(table.getSelectionIndex());
			field.setValue(lv, true);
			popup.setVisible(false);
		});

		table.addListener(SWT.DefaultSelection, event -> {
			if (useSingleClick) {
				return;
			}
//			text.setText(table.getSelection()[0].getText());
			MField field = (MField) getData(Constants.CONTROL_FIELD);
			LookupValue lv = popupValues.get(table.getSelectionIndex());
			field.setValue(lv, true);
			popup.setVisible(false);
		});
		table.addListener(SWT.KeyDown, event -> {
			if (event.keyCode == SWT.ESC) {
				popup.setVisible(false);
			}
		});

		table.addListener(SWT.FocusOut, createFocusOutListener());
	}

	/**
	 * @return a listener for the keydown event
	 */
	private Listener createKeyDownListener() {
		return event -> {
			if (popupIsOpen()) {
				switch (event.keyCode) {
				case SWT.ARROW_DOWN:
					int index = (table.getSelectionIndex() + 1) % table.getItemCount();
					table.setSelection(index);
					event.doit = false;
					break;
				case SWT.ARROW_UP:
					index = table.getSelectionIndex() - 1;
					if (index < 0) {
						index = table.getItemCount() - 1;
					}
					table.setSelection(index);
					event.doit = false;
					break;
				case SWT.CR:
				case SWT.KEYPAD_CR:
					if (popup.isVisible() && table.getSelectionIndex() != -1) {
						text.setText(table.getSelection()[0].getText());
						popup.setVisible(false);
					}
					break;
				case SWT.ESC:
					popup.setVisible(false);
					break;
				}
			} else {
				if (event.keyCode == SWT.ARROW_DOWN || ((event.stateMask & SWT.CTRL) != 0) && (event.keyCode == SWT.SPACE)) {
					showAllElements(text.getText());
				}
			}
		};
	}

	public void showAllElements(String value) {
		table.setFont(text.getFont());
		popupValues = contentProvider.getContent(value);
		if (popupValues == null || popupValues.isEmpty()) {
			popup.setVisible(false);
			if (contentProvider.getValuesSize() == 0) {
				MinovaNotifier.show(Display.getCurrent().getActiveShell(), translationService.translate("@msg.NoLookupEntries", null),
						translationService.translate("@Notification", null));
			}
			return;
		}

		table.removeAll();
		for (LookupValue popupValue : popupValues) {
			TableItem tableItem;
			if (popupValue.getKeyText().equalsIgnoreCase(text.getText())) {
				// Bei genauer Übereinstimmung des Matchcodes Element an erste Stelle setzten, siehe #1086
				tableItem = new TableItem(table, SWT.NONE, 0);
			} else {
				tableItem = new TableItem(table, SWT.NONE);
			}
			tableItem.setText(0, popupValue.keyText);
			tableItem.setText(1, popupValue.description.replace("\r\n", "; "));
			tableItem.setFont(text.getFont());
		}
		table.getColumn(0).pack();
		table.getColumn(0).setWidth(table.getColumn(0).getWidth() + 5);
		table.getColumn(1).pack();
		table.getColumn(1).setWidth(table.getColumn(1).getWidth() + 5);

		final Point point = text.toDisplay(text.getLocation().x, text.getSize().y + text.getBorderWidth() - 3);
		int x = point.x;
		int y = point.y;

		final Rectangle displayRect = getMonitor().getClientArea();
		final Rectangle parentRect = getDisplay().map(getParent(), null, getBounds());

		final int nrLines = Math.min(15, popupValues.size());
		final int width = table.getColumn(0).getWidth() + table.getColumn(1).getWidth() + 50;
		final int height = table.getItemHeight() * nrLines + 15;
		popup.setBounds(x, y, width, height);

		if (y + height > displayRect.y + displayRect.height) {
			y = parentRect.y - height;
		}
		if (x + width > displayRect.x + displayRect.width) {
			x = displayRect.x + displayRect.width - width;
		}

		popup.setLocation(x, y);
		popup.setVisible(true);

		if (System.getProperty("os.name").startsWith("Linux")) {
			table.setFocus();
		}
	}

	/**
	 * @return a listener for the modify event
	 */
	private Listener createModifyListener() {
		return event -> {
			if (text.getData(SETTEXT_KEY) != null && Boolean.TRUE.equals(text.getData(SETTEXT_KEY))) {
				text.setData(SETTEXT_KEY, null);
				return;
			}
			text.setData(SETTEXT_KEY, null);

			final String string = text.getText();

			// Der Wert des Feldes soll auf null gesetzt werden, wenn der Text gelöscht oder geändert wird
			MField field = (MField) text.getParent().getData(Constants.CONTROL_FIELD);
			if (string.isBlank()) {
				field.setValue(null, true);
			} else if (field.getValue() instanceof LookupValue) {
				field.setValue(null, true);
				// Den Eingetragenen Text wieder ins Textfeld setzten
				text.setText(string);
				text.setSelection(text.getText().length());
			}

			if (string.length() == 0) {
				popup.setVisible(false);
				return;
			}
			showAllElements(string);
		};
	}

	/**
	 * @return a listener for the FocusOut event
	 */
	private Listener createFocusOutListener() {
		return event -> LookupComposite.this.getDisplay().asyncExec(() -> {
			if (LookupComposite.this.isDisposed() || LookupComposite.this.getDisplay().isDisposed()) {
				return;
			}
			final Control control = LookupComposite.this.getDisplay().getFocusControl();
			if (control == null || (control != text && control != table && control != popup)) {
				popup.setVisible(false);
			}
		});
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#getBackground()
	 */
	@Override
	public Color getBackground() {
		checkWidget();
		return text.getBackground();
	}

	/**
	 * @return the contentProvider
	 */
	public LookupContentProvider getContentProvider() {
		checkWidget();
		return contentProvider;
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#getForeground()
	 */
	@Override
	public Color getForeground() {
		checkWidget();
		return super.getForeground();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#setBackground(org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void setBackground(final Color color) {
		checkWidget();
		text.setBackground(color);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#addListener(int,org.eclipse.swt.widgets.Listener)
	 */
	@Override
	public void addListener(final int eventType, final Listener listener) {
		checkWidget();
		text.addListener(eventType, listener);
	}

	@Override
	public Point computeSize(final int wHint, final int hHint, final boolean changed) {
		checkWidget();
		return text.computeSize(wHint, hHint, changed);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#computeTrim(int, int, int, int)
	 */
	@Override
	public Rectangle computeTrim(final int x, final int y, final int width, final int height) {
		checkWidget();
		return super.computeTrim(x, y, width, height);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#getEditable()
	 */
	public boolean getEditable() {
		checkWidget();
		return text.getEditable();
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#getEnabled()
	 */
	@Override
	public boolean getEnabled() {
		checkWidget();
		return super.getEnabled();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#getText()
	 */
	public String getText() {
		checkWidget();
		return text.getText();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#setEditable(boolean)
	 */
	public void setEditable(final boolean editable) {
		checkWidget();
		text.setEditable(editable);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(final boolean value) {
		checkWidget();
		text.setEnabled(value);
	}

	/**
	 * @see org.eclipse.swt.widgets.Composite#setFocus()
	 */
	@Override
	public boolean setFocus() {
		checkWidget();
		return text.setFocus();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#setFont(org.eclipse.swt.graphics.Font)
	 */
	@Override
	public void setFont(final Font font) {
		checkWidget();
		text.setFont(font);
		table.setFont(font);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#setForeground(org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void setForeground(final Color color) {
		checkWidget();
		text.setForeground(color);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#setMessage(java.lang.String)
	 */
	public void setMessage(final String string) {
		checkWidget();
		text.setMessage(string);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#setOrientation(int)
	 */
	@Override
	public void setOrientation(final int orientation) {
		checkWidget();
		text.setOrientation(orientation);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#setRedraw(boolean)
	 */
	@Override
	public void setRedraw(final boolean redraw) {
		checkWidget();
		text.setRedraw(redraw);
	}

	@Override
	public boolean isFocusControl() {
		checkWidget();
		return text.isFocusControl();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#setText(java.lang.String)
	 */
	public void setText(final String text) {
		checkWidget();
		this.text.setData(SETTEXT_KEY, Boolean.TRUE);
		this.text.setText(text);
	}

	public void fillSelectedValue() {
		MField field = (MField) getData(Constants.CONTROL_FIELD);
		if (popup.isVisible() && table.getSelectionIndex() != -1) {
			LookupValue lv = popupValues.get(table.getSelectionIndex());
			text.setText(lv.keyText);
			field.setValue(lv, true);
		} else if (popup.isVisible()) {

			// Zuerst versuchen, einen genauen Match auf den KeyText zu finden, siehe #1086
			for (LookupValue lv : popupValues) {
				if (lv.keyText.equalsIgnoreCase(text.getText())) {
					text.setText(lv.keyText);
					field.setValue(lv, true);
					return;
				}
			}

			// Ansonsten ersten Wert eintragen
			if (!popupValues.isEmpty()) {
				text.setText(popupValues.get(0).keyText);
				field.setValue(popupValues.get(0), true);
			}
		}
	}

	public Label getDescription() {
		return description;
	}

	public void setDescription(Label description) {
		this.description = description;
	}

	public boolean popupIsOpen() {
		return popup.getVisible();
	}

	public void setLabel(Label label) {
		Objects.requireNonNull(label);
		this.label = label;

		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				setFocus();
				requestAllLookupEntries();
			}
		});
	}

	public Label getLabel() {
		return this.label;
	}

	protected void requestAllLookupEntries() {
		if (isReadOnly()) {
			return;
		}

		if (!checkLastState()) {
			if (!gettingData) {
				gettingData = true;
				setMessage("...");

				MLookupField field = (MLookupField) getData(Constants.CONTROL_FIELD);
				BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
				ServiceReference<?> serviceReference = bundleContext.getServiceReference(IDataService.class.getName());
				IDataService dataService = (IDataService) bundleContext.getService(serviceReference);

				CompletableFuture<List<LookupValue>> listLookup = dataService.listLookup(field, false);
				setLastState();

				try {
					List<LookupValue> l = listLookup.get();
					contentProvider.setValues(l);
					gettingData = false;
				} catch (InterruptedException | ExecutionException e) {}
			} else {
				MinovaNotifier.show(Display.getCurrent().getActiveShell(), translationService.translate("@msg.ActiveRequest", null),
						translationService.translate("@Notification", null));
			}
		} else {
			showAllElements(text.getText());
		}
	}

	/**
	 * Diese Methode wird aufgerufen, sobald der {@link LookupContentProvider} neue Werte von der Datenbank erhalten hat.
	 */
	public void valuesUpdated() {
		if (!text.isFocusControl()) {
			return;
		}

		showAllElements(text.getText());
	}

	public void closePopup() {
		popup.setVisible(false);
	}

	private boolean isReadOnly() {
		MField field = (MField) this.getData(Constants.CONTROL_FIELD);
		return field.isReadOnly();
	}

	/**
	 * Speichert Zeitpunkt auf Millisekundenbasis und Zustand der abhängigen Lookup-Felder der letzten Abfrage.
	 */
	private void setLastState() {
		this.lastRequestState = CacheUtil.getNameList((MField) this.getData(Constants.CONTROL_FIELD));
		this.lastRequestTime = System.currentTimeMillis();
	}

	/**
	 * False, wenn es eine Änderung gab oder mehr als 5 Sekunden zwischen zwei Anfragen vergangen sind.
	 */
	private boolean checkLastState() {
		String state = CacheUtil.getNameList((MField) this.getData(Constants.CONTROL_FIELD));
		if (!state.equals(lastRequestState)) {
			return false;
		}
		long time = System.currentTimeMillis();
		if (time - lastRequestTime >= 2000) {
			return false;
		}
		return true;
	}

	public List<LookupValue> getPopupValues() {
		return popupValues;
	}

	public Table getTable() {
		return table;
	}

	@Override
	public String toString() {
		return "Lookup (" + label.getText() + ")";
	}

}