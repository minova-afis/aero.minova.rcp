package aero.minova.rcp.rcp.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;

import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.form.model.xsd.Head;
import aero.minova.rcp.form.model.xsd.Page;
import aero.minova.rcp.rcp.util.DetailUtil;

public class XMLDetailPart {

	@Inject
	private IDataFormService dataFormService;

//	@Inject
//	private IDataService dataService;
	
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	


	@PostConstruct
	public void createComposite(Composite parent) {
		//Top-Level_Element
		parent.setLayout(new GridLayout(1, true));

		Form form = dataFormService.getForm();

		for (Object o : form.getDetail().getHeadAndPage()) {
			if (o instanceof Head) {
				Head head = (Head) o;
				Composite detailFieldComposite = DetailUtil.createSection(formToolkit, parent, head);
				for (Object fieldOrGrid : head.getFieldOrGrid()) {
					if (fieldOrGrid instanceof Field) {
						DetailUtil.createField((Field) fieldOrGrid, detailFieldComposite);
					}
				}
			} else if (o instanceof Page) {
				Page page = (Page) o;
				Composite detailFieldComposite = DetailUtil.createSection(formToolkit, parent, page);
				for (Object o2 : page.getFieldOrGrid()) {
					if (o2 instanceof Field) {
						DetailUtil.createField((Field) o2, detailFieldComposite);
					}
				}
			}
		}
	}
}
