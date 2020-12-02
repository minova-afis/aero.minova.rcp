package aero.minova.rcp.rcp.fields;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import org.eclipse.core.runtime.ServiceCaller;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dataservice.ILocalDatabaseService;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.rcp.accessor.LookUpValueAccessor;
import aero.minova.rcp.rcp.util.Constants;
import aero.minova.rcp.rcp.util.LookupCASRequestUtil;
import aero.minova.rcp.rcp.widgets.LookupControl;

public class LookupField {

	private static final String AERO_MINOVA_RCP_TRANSLATE_PROPERTY = "aero.minova.rcp.translate.property";
	private static final int COLUMN_WIDTH = 140;
	private static final int MARGIN_LEFT = 5;
	private static final int MARGIN_TOP = 5; // Zwischenräume
	private static final int COLUMN_HEIGHT = 28;

	public static Control create(Composite composite, MField field, int row, int column, FormToolkit formToolkit,
			IEventBroker broker, MPerspective perspective, ILocalDatabaseService localDatabaseService, MDetail detail) {
		String labelText = field.getLabel() == null ? "" : field.getLabel();
		Label label = formToolkit.createLabel(composite, labelText, SWT.RIGHT);
		LookupControl lookupControl = new LookupControl(composite, SWT.LEFT);
		Label descriptionLabel = formToolkit.createLabel(composite, "", SWT.LEFT);
		FormData lookupFormData = new FormData();
		FormData labelFormData = new FormData();
		FormData descriptionLabelFormData = new FormData();

		IEclipseContext context = perspective.getContext();
		LookUpValueAccessor lookUpValueAccessor = new LookUpValueAccessor(field, detail, lookupControl);
		ContextInjectionFactory.inject(lookUpValueAccessor, context);
		field.setValueAccessor(lookUpValueAccessor);

		lookupFormData.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT);
		lookupFormData.left = new FormAttachment(composite, MARGIN_LEFT * (column + 1) + (column + 1) * COLUMN_WIDTH);
		lookupFormData.width = COLUMN_WIDTH;

		labelFormData.top = new FormAttachment(lookupControl, 0, SWT.CENTER);
		labelFormData.right = new FormAttachment(lookupControl, MARGIN_LEFT * -1, SWT.LEFT);
		labelFormData.width = COLUMN_WIDTH;

		descriptionLabelFormData.top = new FormAttachment(lookupControl, 0, SWT.CENTER);
		descriptionLabelFormData.left = new FormAttachment(lookupControl, 0, SWT.RIGHT);
		if (field.getNumberColumnsSpanned() != null && field.getNumberColumnsSpanned().intValue() == 4) {
			descriptionLabelFormData.width = MARGIN_LEFT * 2 + COLUMN_WIDTH * 2;
		} else {
			descriptionLabelFormData.width = 0;
		}

		label.setData(AERO_MINOVA_RCP_TRANSLATE_PROPERTY, labelText);
		label.setLayoutData(labelFormData);

		lookupControl.setLayoutData(lookupFormData);
		lookupControl.setDescription(descriptionLabel);
		lookupControl.setData(Constants.CONTROL_FIELD, field);

		descriptionLabel.setLayoutData(descriptionLabelFormData);

		lookupControl.addTwistieMouseListener(new MouseAdapter() {

			@Override
			/*
			 * Aufruf der Prozedur mit um den Datensatz zu laden. prüfen ob noch andere
			 * LookUpFelder eingetragen wurden
			 */
			public void mouseDown(MouseEvent e) {
				requestLookUpEntriesAll(field, detail, lookupControl);
			}
		});
		return lookupControl;
	}

	/**
	 * Auslesen aller bereits einhgetragenen key die mit diesem Controll in
	 * Zusammenhang stehen Es wird eine Liste von Ergebnissen Erstellt, diese wird
	 * dem benutzer zur verfügung gestellt.
	 *
	 * @param luc
	 */
	@Inject
	@Optional
	public static void requestLookUpEntriesAll(MField field, MDetail detail, LookupControl lookUpControl) {
		String name = field.getName();
		CompletableFuture<?> tableFuture;

		BundleContext bundleContext = FrameworkUtil.getBundle(LookupField.class).getBundleContext();
		ServiceReference<?> serviceReference = bundleContext.getServiceReference(IDataService.class.getName());
		IDataService dataService = (IDataService) bundleContext.getService(serviceReference);

		ServiceCaller<ILocalDatabaseService> localDatabaseService = new ServiceCaller<>(LookupField.class,
				ILocalDatabaseService.class);

		tableFuture = LookupCASRequestUtil.getRequestedTable(0, null, field, detail, dataService,
				"List");
		lookUpControl.getTextControl().setText("...");
		tableFuture.thenAccept(ta -> Display.getDefault().asyncExec(() -> {
			if (ta instanceof SqlProcedureResult) {
				SqlProcedureResult sql = (SqlProcedureResult) ta;
				localDatabaseService.current().get().replaceResultsForLookupField(field.getName(), sql.getResultSet());

//					lookupUtil.changeOptionsForLookupField(sql.getResultSet(), lookUpControl, false);
			} else if (ta instanceof Table) {
				Table t = (Table) ta;
				localDatabaseService.current().get().replaceResultsForLookupField(field.getName(), t);
//					lookupUtil.changeOptionsForLookupField(t, lookUpControl, false);
			}
			lookUpControl.getTextControl().setText("");
		}));
	}
}
