package aero.minova.rcp.rcp.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

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

public class XMLDetailPart {
	
	@Inject
	private IDataFormService dataFormService;
	
	@Inject
	private IDataService dataService;
	
	@PostConstruct
	public void createComposite(Composite parent) {

		Form form = dataFormService.getForm();
		String tableName = form.getIndexView().getSource();
		Table data = dataService.getData(tableName);

		parent.setLayout(new GridLayout());

		for(Object o : form.getDetail().getHeadAndPage()) {
			if (o instanceof Head) {
				Head head = (Head)o;
				DetailUtil.createSection(parent, head);
				for(Object o2 : head.getFieldOrSeparatorOrGrid()) {
					if (o2 instanceof Field) {
						DetailUtil.createField((Field)o2);
					}
				}
			} else if (o instanceof Page) {
				Page page = (Page)o;
				DetailUtil.createSection(parent, page);
				for(Object o2 : page.getFieldOrSeparatorOrGrid()) {
					if (o2 instanceof Field) {
						DetailUtil.createField((Field)o2);
					}
				}
			}
		}
	}
}
