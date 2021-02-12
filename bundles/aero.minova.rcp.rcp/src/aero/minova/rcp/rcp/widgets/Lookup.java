package aero.minova.rcp.rcp.widgets;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.ServiceCaller;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dataservice.ILocalDatabaseService;
import aero.minova.rcp.model.LookupValue;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MLookupField;
import aero.minova.rcp.rcp.fields.LookupField;
import aero.minova.rcp.rcp.util.Constants;
import aero.minova.rcp.rcp.util.LookupCASRequestUtil;

public class Lookup extends Composite {

	private static final String SETTEXT_KEY = "org.eclipse.nebula.widgets.opal.textassist.TextAssist.settext";
	private final Text text;
	private final Shell popup;
	private final Table table;
	private LookupContentProvider contentProvider;
	private int numberOfLines;
	private boolean useSingleClick = false;
	private LookupValue firstValue;
	/**
	 * Das Label, das den Wert beschreibt. Die Description aus der Datenbank.
	 */
	private Label description;
	/**
	 * Das Label, das das Feld beschreibt
	 */
	private Label label;
	private List<LookupValue> popupValues;
	private MouseListener mouseListener;

	/**
	 * Constructs a new instance of this class given its parent and a style value
	 * describing its behavior and appearance.
	 * <p>
	 * The style value is either one of the style constants defined in class
	 * <code>SWT</code> which is applicable to instances of this class, or must be
	 * built by <em>bitwise OR</em>'ing together (that is, using the
	 * <code>int</code> "|" operator) two or more of those <code>SWT</code> style
	 * constants. The class description lists the style constants that are
	 * applicable to the class. Style bits are also inherited from superclasses.
	 * </p>
	 *
	 * @param parent          a composite control which will be the parent of the
	 *                        new instance (cannot be null)
	 * @param style           the style of control to construct
	 * @param contentProvider the content provider
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_NULL_ARGUMENT - if the parent
	 *                                     is null</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     parent</li>
	 *                                     <li>ERROR_INVALID_SUBCLASS - if this
	 *                                     class is not an allowed subclass</li>
	 *                                     </ul>
	 *
	 * @see SWT#SINGLE
	 * @see SWT#MULTI
	 * @see SWT#READ_ONLY
	 * @see SWT#WRAP
	 * @see SWT#LEFT
	 * @see SWT#RIGHT
	 * @see SWT#CENTER
	 * @see SWT#PASSWORD
	 * @see SWT#SEARCH
	 * @see SWT#ICON_SEARCH
	 * @see SWT#ICON_CANCEL
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */
	public Lookup(final Composite parent, final int style, final LookupContentProvider contentProvider) {
		super(parent, SWT.NONE);
		this.contentProvider = contentProvider;
		this.contentProvider.setLookup(this);

		setLayout(new FillLayout());
		numberOfLines = 10;
		text = new Text(this, style);
		popup = new Shell(getDisplay(), SWT.ON_TOP);
		popup.setLayout(new FillLayout());
		table = new Table(popup, SWT.SINGLE);
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
		return new Listener() {
			@Override
			public void handleEvent(final Event event) {
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
					if (event.keyCode == SWT.ARROW_DOWN) {
						requestAllLookupEntries();
					}
				}
			}
		};
	}

	public void showAllElements(String value) {
		popupValues = contentProvider.getContent(value);
		if (popupValues == null || popupValues.isEmpty()) {
			popup.setVisible(false);
			firstValue = null;
			return;
		}
		firstValue = popupValues.get(0);
		if (popupValues.size() > numberOfLines) {
			popupValues = popupValues.subList(0, numberOfLines);
		}

		table.removeAll();
		final int numberOfRows = Math.min(popupValues.size(), numberOfLines);
		for (int i = 0; i < numberOfRows; i++) {
			final TableItem tableItem = new TableItem(table, SWT.NONE);
			tableItem.setText(0, popupValues.get(i).keyText);
			tableItem.setText(1, popupValues.get(i).description);
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
		popup.pack();
		final int width = popup.getBounds().width;
		final int height = popup.getBounds().height;

		if (y + height > displayRect.y + displayRect.height) {
			y = parentRect.y - height;
		}
		if (x + width > displayRect.x + displayRect.width) {
			x = displayRect.x + displayRect.width - width;
		}

		popup.setLocation(x, y);
		popup.setVisible(true);
		table.setFocus();
	}

	/**
	 * @return a listener for the modify event
	 */
	private Listener createModifyListener() {
		return new Listener() {
			@Override
			public void handleEvent(final Event event) {
				if (text.getData(SETTEXT_KEY) != null && Boolean.TRUE.equals(text.getData(SETTEXT_KEY))) {
					text.setData(SETTEXT_KEY, null);
					return;
				}
				text.setData(SETTEXT_KEY, null);

				final String string = text.getText();
				if (string.length() == 0) {
					popup.setVisible(false);
					return;
				}
				showAllElements(string);
			}
		};
	}

	/**
	 * @return a listener for the FocusOut event
	 */
	private Listener createFocusOutListener() {
		return new Listener() {
			@Override
			public void handleEvent(final Event event) {
				/*
				 * Async is needed to wait until focus reaches its new Control
				 */
				Lookup.this.getDisplay().asyncExec(() -> {
					if (Lookup.this.isDisposed() || Lookup.this.getDisplay().isDisposed()) {
						return;
					}
					final Control control = Lookup.this.getDisplay().getFocusControl();
					if (control == null || (control != text && control != table && control != popup)) {
						popup.setVisible(false);
					}
				});
			}
		};
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
	 * @param contentProvider the contentProvider to set
	 */
	public void setContentProvider(final LookupContentProvider contentProvider) {
		checkWidget();
		this.contentProvider = contentProvider;
	}

	/**
	 * @return the numberOfLines
	 */
	public int getNumberOfLines() {
		checkWidget();
		return numberOfLines;
	}

	/**
	 * @param numberOfLines the numberOfLines to set
	 */
	public void setNumberOfLines(final int numberOfLines) {
		checkWidget();
		this.numberOfLines = numberOfLines;
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#addListener(int,org.eclipse.swt.widgets.Listener)
	 */
	@Override
	public void addListener(final int eventType, final Listener listener) {
		checkWidget();
		text.addListener(eventType, listener);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#addModifyListener(org.eclipse.swt.events.ModifyListener)
	 */
	public void addModifyListener(final ModifyListener listener) {
		checkWidget();
		text.addModifyListener(listener);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#addSelectionListener(org.eclipse.swt.events.SelectionListener)
	 */
	public void addSelectionListener(final SelectionListener listener) {
		checkWidget();
		text.addSelectionListener(listener);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#addVerifyListener(org.eclipse.swt.events.VerifyListener)
	 */
	public void addVerifyListener(final VerifyListener listener) {
		checkWidget();
		text.addVerifyListener(listener);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#append(java.lang.String)
	 */
	public void append(final String string) {
		checkWidget();
		text.append(string);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#clearSelection()
	 */
	public void clearSelection() {
		checkWidget();
		text.clearSelection();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#computeSize(int, int, boolean)
	 */
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
	 * @see org.eclipse.swt.widgets.Text#copy()
	 */
	public void copy() {
		checkWidget();
		text.copy();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#cut()
	 */
	public void cut() {
		checkWidget();
		text.cut();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#getCaretLineNumber()
	 */
	public int getCaretLineNumber() {
		checkWidget();
		return text.getCaretLineNumber();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#getCaretLocation()
	 */
	public Point getCaretLocation() {
		checkWidget();
		return text.getCaretLocation();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#getCaretPosition()
	 */
	public int getCaretPosition() {
		checkWidget();
		return text.getCaretPosition();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#getCharCount()
	 */
	public int getCharCount() {
		checkWidget();
		return text.getCharCount();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#getDoubleClickEnabled()
	 */
	public boolean getDoubleClickEnabled() {
		checkWidget();
		return text.getDoubleClickEnabled();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#getEchoChar()
	 */
	public char getEchoChar() {
		checkWidget();
		return text.getEchoChar();
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
	 * @see org.eclipse.swt.widgets.Text#getLineCount()
	 */
	public int getLineCount() {
		checkWidget();
		return text.getLineCount();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#getLineDelimiter()
	 */
	public String getLineDelimiter() {
		checkWidget();
		return text.getLineDelimiter();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#getLineHeight()
	 */
	public int getLineHeight() {
		checkWidget();
		return text.getLineHeight();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#getMessage()
	 */
	public String getMessage() {
		checkWidget();
		return text.getMessage();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#getOrientation()
	 */
	@Override
	public int getOrientation() {
		checkWidget();
		return text.getOrientation();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#getSelection()
	 */
	public Point getSelection() {
		checkWidget();
		return text.getSelection();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#getSelectionCount()
	 */
	public int getSelectionCount() {
		checkWidget();
		return text.getSelectionCount();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#getSelectionText()
	 */
	public String getSelectionText() {
		checkWidget();
		return text.getSelectionText();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#getTabs()
	 */
	public int getTabs() {
		checkWidget();
		return text.getTabs();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#getText()
	 */
	public String getText() {
		checkWidget();
		return text.getText();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#getText(int, int)
	 */
	public String getText(final int start, final int end) {
		checkWidget();
		return text.getText(start, end);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#getTextLimit()
	 */
	public int getTextLimit() {
		checkWidget();
		return text.getTextLimit();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#getTopIndex()
	 */
	public int getTopIndex() {
		checkWidget();
		return text.getTopIndex();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#getTopPixel()
	 */
	public int getTopPixel() {
		checkWidget();
		return text.getTopPixel();
	}

	/**
	 * Returns the single click enabled flag.
	 * <p>
	 * If the the single click flag is true, the user can select an entry with a
	 * single click. Otherwise, the user can select an entry with a double click.
	 * </p>
	 *
	 * @return whether or not single is enabled
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public boolean getUseSingleClick() {
		checkWidget();
		return useSingleClick;
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#insert(java.lang.String)
	 */
	public void insert(final String string) {
		checkWidget();
		text.insert(string);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#paste()
	 */
	public void paste() {
		checkWidget();
		text.paste();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#removeModifyListener(org.eclipse.swt.events.ModifyListener)
	 */
	public void removeModifyListener(final ModifyListener listener) {
		checkWidget();
		text.removeModifyListener(listener);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#removeSelectionListener(org.eclipse.swt.events.SelectionListener)
	 */
	public void removeSelectionListener(final SelectionListener listener) {
		checkWidget();
		text.removeSelectionListener(listener);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#removeVerifyListener(org.eclipse.swt.events.VerifyListener)
	 */
	public void removeVerifyListener(final VerifyListener listener) {
		checkWidget();
		text.removeVerifyListener(listener);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#selectAll()
	 */
	public void selectAll() {
		checkWidget();
		text.selectAll();
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#setDoubleClickEnabled(boolean)
	 */
	public void setDoubleClickEnabled(final boolean doubleClick) {
		checkWidget();
		text.setDoubleClickEnabled(doubleClick);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#setEchoChar(char)
	 */
	public void setEchoChar(final char echo) {
		checkWidget();
		text.setEchoChar(echo);
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

	/**
	 * @see org.eclipse.swt.widgets.Text#setSelection(int, int)
	 */
	public void setSelection(final int start, final int end) {
		checkWidget();
		text.setSelection(start, end);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#setSelection(int)
	 */
	public void setSelection(final int start) {
		checkWidget();
		text.setSelection(start);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#setSelection(org.eclipse.swt.graphics.Point)
	 */
	public void setSelection(final Point selection) {
		checkWidget();
		text.setSelection(selection);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#setTabs(int)
	 */
	public void setTabs(final int tabs) {
		checkWidget();
		text.setTabs(tabs);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#setText(java.lang.String)
	 */
	public void setText(final String text) {
		checkWidget();
		this.text.setData(SETTEXT_KEY, Boolean.TRUE);
		this.text.setText(text);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#setTextLimit(int)
	 */
	public void setTextLimit(final int textLimit) {
		checkWidget();
		text.setTextLimit(textLimit);
	}

	/**
	 * Sets the single click enabled flag.
	 * <p>
	 * If the the single click flag is true, the user can select an entry with a
	 * single click. Otherwise, the user can select an entry with a double click.
	 * </p>
	 *
	 * @param singleClick the new single click flag
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public void setUseSingleClick(boolean singleClick) {
		checkWidget();
		this.useSingleClick = singleClick;
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#setTopIndex(int)
	 */
	public void setTopIndex(final int topIndex) {
		checkWidget();
		text.setTopIndex(topIndex);
	}

	/**
	 * @see org.eclipse.swt.widgets.Text#showSelection()
	 */
	public void showSelection() {
		checkWidget();
		text.showSelection();
	}

	public void fillSelectedValue() {
		MField field = (MField) getData(Constants.CONTROL_FIELD);
		if (popup.isVisible() && table.getSelectionIndex() != -1) {
			LookupValue lv = popupValues.get(table.getSelectionIndex());
			text.setText(lv.keyText);
			field.setValue(lv, true);
		} else if (popup.isVisible() && firstValue != null) {
			text.setText(firstValue.keyText);
			field.setValue(firstValue, true);
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
		if (this.label != null && mouseListener != null) {
			label.removeMouseListener(mouseListener);
		}

		this.label = label;
		if (label == null) {
			return;
		}

		label.addMouseListener(mouseListener = new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				Lookup.this.setFocus();
				requestAllLookupEntries();
			}
		});
	}

	protected void requestAllLookupEntries() {
		setMessage("...");

		CompletableFuture<?> tableFuture;
		MLookupField field = (MLookupField) getData(Constants.CONTROL_FIELD);
		MDetail detail = field.getDetail();

		BundleContext bundleContext = FrameworkUtil.getBundle(LookupField.class).getBundleContext();
		ServiceReference<?> serviceReference = bundleContext.getServiceReference(IDataService.class.getName());
		IDataService dataService = (IDataService) bundleContext.getService(serviceReference);

//		tableFuture = dataService.listLookupAsync("%", field, false);
//		tableFuture.thenAccept(v -> contentProvider.setValues(v));

		ServiceCaller<ILocalDatabaseService> localDatabaseService = new ServiceCaller<>(LookupField.class, ILocalDatabaseService.class);

		tableFuture = LookupCASRequestUtil.getRequestedTable(0, null, field, detail, dataService, "List");
		tableFuture.thenAccept(ta -> Display.getDefault().asyncExec(() -> {
			aero.minova.rcp.model.Table t = null;
			if (ta instanceof SqlProcedureResult) {
				t = ((SqlProcedureResult) ta).getResultSet();
			} else if (ta instanceof aero.minova.rcp.model.Table) {
				t = (aero.minova.rcp.model.Table) ta;
			}
			localDatabaseService.current().get().replaceResultsForLookupField(field.getName(), t);
			contentProvider.setTable(t);
		}));

	}

	/**
	 * Diese Methode wird aufgerufen, sobald der {@link LookupContentProvider} neue Werte von der Datenbank erhalten hat.
	 */
	public void valuesUpdated() {
		if (!text.isFocusControl()) {
			return;
		}

		if (text.getText().length() > 0) {
			showAllElements(text.getText());
		} else {
			showAllElements("%");
		}
	}
}