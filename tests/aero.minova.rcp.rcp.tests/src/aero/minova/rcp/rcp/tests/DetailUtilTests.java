package aero.minova.rcp.rcp.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.widgets.CompositeFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Head;
import aero.minova.rcp.form.model.xsd.Lookup;
import aero.minova.rcp.form.model.xsd.Number;
import aero.minova.rcp.plugin1.model.Table;
import aero.minova.rcp.rcp.util.DetailUtil;

public class DetailUtilTests {

	private Table t;
	private Shell shell;
	private Composite composite;
	private FormToolkit formToolkit;
	private Map<String, Control> controls;
	
	@Before
	public void setup() {
		shell = new Shell();
		formToolkit = new FormToolkit(Display.getDefault());
		composite = CompositeFactory.newComposite(SWT.None).create(shell);
		composite.setLayout(new GridLayout(6, false));
		controls = new HashMap<String, Control>();

	}

	@After
	public void tearDown() {
		shell.dispose();
	}

	
	
	@Test (expected = NumberFormatException.class)
	public void ensureWeCannotHandleNumberParsingExceptionsinNumberRowsSpanned() {
		Field field = new Field();
		field.setVisible(true);
		field.setTextAttribute("Testing");
		field.setNumberRowsSpanned("THIS RESULTS IN AN EXCEPTION");
		DetailUtil.createField(field, composite, controls);
	}
	
	@Test
	public void ensureInvisibleFieldIsHidden() {
		Composite composite = CompositeFactory.newComposite(SWT.None).create(shell);
		Field field = new Field();
		field.setVisible(false);
		DetailUtil.createField(field, composite, controls);
		assertTrue(composite.getChildren().length == 0);
	}

	@Test
	public void getSpannedHintForElement() throws Exception {
		Field field = new Field();
		field.setLookup(new Lookup());

		field.setNumberColumnsSpanned(new BigInteger("4"));
		assertTrue(DetailUtil.getSpannedHintForElement(field, true) == 2);

		field.setNumberColumnsSpanned(new BigInteger("2"));
		assertTrue(DetailUtil.getSpannedHintForElement(field, true) == 2);

	}

	@Test
	public void testHeader() throws Exception {
		Field field = new Field();
		field.setTextAttribute("Test");
		field.setDateTime(new Object());
		field.setNumberColumnsSpanned(new BigInteger("4"));

		Field lookup = new Field();
		lookup.setTextAttribute("Test");
		lookup.setLookup(new Lookup());
		lookup.setNumberColumnsSpanned(new BigInteger("4"));

		Head head = new Head();
		head.getFieldOrGrid().add(field);
		head.getFieldOrGrid().add(lookup);

		Composite co = DetailUtil.createSection(formToolkit, composite.getParent(), head);
		for (Object o2 : head.getFieldOrGrid()) {
			if (o2 instanceof Field) {
				DetailUtil.createField((Field) o2, co, controls);
			}
		}
		Control[] children = co.getChildren();
		assertEquals(children.length, 6);
		Object layoutData = children[0].getLayoutData();
		assertEquals(layoutData.getClass(), GridData.class);
		assertEquals(((GridData) layoutData).horizontalAlignment, SWT.RIGHT);
		assertEquals(1, ((GridData) layoutData).horizontalSpan);
		assertEquals(150, ((GridData) layoutData).widthHint);
		Object layoutData1 = children[1].getLayoutData();
		assertTrue(layoutData1.getClass().equals(GridData.class));
		assertTrue(((GridData) layoutData1).horizontalAlignment == SWT.LEFT);
		assertEquals(2, ((GridData) layoutData1).horizontalSpan);
		assertEquals(170, ((GridData) layoutData1).widthHint);
		layoutData = children[2].getLayoutData();
		assertEquals(layoutData.getClass(), GridData.class);
		assertEquals(((GridData) layoutData).horizontalAlignment, SWT.LEFT);
		assertEquals(3, ((GridData) layoutData).horizontalSpan);
		assertEquals(children[2].getClass(), Label.class);
		assertTrue(((Label) children[2]).getText().isEmpty());

		layoutData = children[3].getLayoutData();
		assertTrue(layoutData.getClass().equals(GridData.class));
		assertTrue(((GridData) layoutData).horizontalAlignment == SWT.RIGHT);
		assertTrue(((GridData) layoutData).horizontalSpan == 1);
		assertTrue(((GridData) layoutData).widthHint == 150);
		layoutData = children[4].getLayoutData();
		assertTrue(layoutData.getClass().equals(GridData.class));
		assertTrue(((GridData) layoutData).horizontalAlignment == SWT.LEFT);
		assertTrue(((GridData) layoutData).horizontalSpan == 2);
		assertTrue(((GridData) layoutData).widthHint == 170);
		layoutData = children[5].getLayoutData();
		assertTrue(layoutData.getClass().equals(GridData.class));
		assertTrue(((GridData) layoutData).horizontalAlignment == SWT.LEFT);
		assertTrue(((GridData) layoutData).horizontalSpan == 3);
		assertEquals(320, ((GridData) layoutData).widthHint);

	}

	@Test
	public void testNee3e3ame() throws Exception {
		Field field = new Field();
		field.setTextAttribute("Test");
		field.setDateTime(new Object());
		field.setNumberColumnsSpanned(new BigInteger("4"));
		DetailUtil.createField(field, composite, controls);
		Control[] children = composite.getChildren();
		assertEquals(children.length, 3);
		Object layoutData = children[0].getLayoutData();
		assertEquals(layoutData.getClass(), GridData.class);
		assertEquals(((GridData) layoutData).horizontalAlignment, SWT.RIGHT);
		assertEquals(1, ((GridData) layoutData).horizontalSpan);
		assertEquals(150, ((GridData) layoutData).widthHint);
		Object layoutData1 = children[1].getLayoutData();
		assertTrue(layoutData1.getClass().equals(GridData.class));
		assertTrue(((GridData) layoutData1).horizontalAlignment == SWT.LEFT);
		assertEquals(2, ((GridData) layoutData1).horizontalSpan);
		assertEquals(170, ((GridData) layoutData1).widthHint);
		Object layoutData2 = children[2].getLayoutData();
		assertEquals(layoutData2.getClass(), GridData.class);
		assertEquals(((GridData) layoutData2).horizontalAlignment, SWT.LEFT);
		assertEquals(3, ((GridData) layoutData2).horizontalSpan);
		assertEquals(children[2].getClass(), Label.class);
		assertTrue(((Label) children[2]).getText().isEmpty());
	}

	@Test
	public void testName() throws Exception {
		Field field = new Field();
		field.setTextAttribute("Test");
		field.setNumber(new Number());
		field.setUnitText("L");
		DetailUtil.createField(field, composite, controls);
		Control[] children = composite.getChildren();
		assertTrue(children.length == 3);
		Object layoutData = children[0].getLayoutData();
		assertTrue(layoutData.getClass().equals(GridData.class));
		assertTrue(((GridData) layoutData).horizontalAlignment == SWT.RIGHT);
		assertTrue(((GridData) layoutData).horizontalSpan == 1);
		assertEquals(150, ((GridData) layoutData).widthHint);
		Object layoutData1 = children[1].getLayoutData();
		assertTrue(layoutData1.getClass().equals(GridData.class));
		assertTrue(((GridData) layoutData1).horizontalAlignment == SWT.LEFT);
		assertEquals(1, ((GridData) layoutData1).horizontalSpan);
		assertEquals(150, ((GridData) layoutData1).widthHint);
		Object layoutData2 = children[2].getLayoutData();
		assertTrue(layoutData2.getClass().equals(GridData.class));
		assertTrue(((GridData) layoutData2).horizontalAlignment == SWT.LEFT);
		assertTrue(((GridData) layoutData2).horizontalSpan == 1);
		assertEquals(20, ((GridData) layoutData2).widthHint);
	}

	@Test
	public void createLookUpLayoutRow() throws Exception {
		Field field = new Field();
		field.setTextAttribute("Test");
		field.setLookup(new Lookup());
		field.setNumberColumnsSpanned(new BigInteger("4"));
		DetailUtil.createField(field, composite, controls);
		Control[] children = composite.getChildren();
		assertTrue(children.length == 3);
		Object layoutData = children[0].getLayoutData();
		assertTrue(layoutData.getClass().equals(GridData.class));
		assertTrue(((GridData) layoutData).horizontalAlignment == SWT.RIGHT);
		assertTrue(((GridData) layoutData).horizontalSpan == 1);
		assertTrue(((GridData) layoutData).widthHint == 150);
		Object layoutData1 = children[1].getLayoutData();
		assertTrue(layoutData1.getClass().equals(GridData.class));
		assertTrue(((GridData) layoutData1).horizontalAlignment == SWT.LEFT);
		assertTrue(((GridData) layoutData1).horizontalSpan == 2);
		assertTrue(((GridData) layoutData1).widthHint == 170);
		Object layoutData2 = children[2].getLayoutData();
		assertTrue(layoutData2.getClass().equals(GridData.class));
		assertTrue(((GridData) layoutData2).horizontalAlignment == SWT.LEFT);
		assertTrue(((GridData) layoutData2).horizontalSpan == 3);
		assertEquals(320, ((GridData) layoutData2).widthHint);
	}

}
