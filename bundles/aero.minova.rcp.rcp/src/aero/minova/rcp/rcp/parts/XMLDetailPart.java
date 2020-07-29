package aero.minova.rcp.rcp.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.form.model.xsd.Head;
import aero.minova.rcp.form.model.xsd.Page;
import aero.minova.rcp.plugin1.model.Table;
import aero.minova.rcp.rcp.util.DetailUtil;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.SWT;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class XMLDetailPart {

	@Inject
	private IDataFormService dataFormService;

	@Inject
	private IDataService dataService;

	@PostConstruct
	public void createComposite(Composite parent) {
		parent.setLayout(new FillLayout(SWT.None));

		Form form = dataFormService.getForm();
		String tableName = form.getIndexView().getSource();
		Table data = dataService.getData(tableName);

		for (Object o : form.getDetail().getHeadAndPage()) {
			if (o instanceof Head) {
				Head head = (Head) o;
				Composite co = DetailUtil.createSection(parent, head);
				for (Object o2 : head.getFieldOrGrid()) {
					if (o2 instanceof Field) {
						DetailUtil.createField((Field) o2, co);
					}
				}
				co.getParent().getParent().pack();
				co.requestLayout();
			} else if (o instanceof Page) {
				Page page = (Page) o;
				Composite co = DetailUtil.createSection(parent, page);
				for (Object o2 : page.getFieldOrGrid()) {
					if (o2 instanceof Field) {
						DetailUtil.createField((Field) o2, co);
					}
				}
				co.getParent().getParent().pack();
				co.requestLayout();
			}
		}
	}
}
