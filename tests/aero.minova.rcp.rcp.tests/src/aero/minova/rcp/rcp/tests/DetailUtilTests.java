package aero.minova.rcp.rcp.tests;

import static org.junit.Assert.assertTrue;

import org.eclipse.jface.widgets.CompositeFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.plugin1.model.Table;
import aero.minova.rcp.rcp.util.DetailUtil;

public class DetailUtilTests {

	private Table t;
	private Shell shell;

	@Before
	public void setup() {
		shell = new Shell();
		
	}
	@After
	public void tearDown() {
		shell.dispose();
	}

	@Test
	public void ensureInvisibleFieldIsHidden() {
		Composite composite = CompositeFactory.newComposite(SWT.None).create(shell);
		Field field = new Field();
		field.setVisible(false);
		DetailUtil.createField(field, composite);
		assertTrue(composite.getChildren().length==0);
	}
	
	
}
