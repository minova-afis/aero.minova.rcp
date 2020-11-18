package aero.minova.rcp.rcp.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.builder.ValueBuilder;
import aero.minova.rcp.rcp.widgets.LookupControl;

public class WFCDetailLookupFieldUtil {

	private static final String AERO_MINOVA_RCP_TRANSLATE_PROPERTY = "aero.minova.rcp.translate.property";
	private static final int COLUMN_WIDTH = 140;
	private static final int MARGIN_LEFT = 5;
	private static final int MARGIN_TOP = 5; // Zwischenräume
	private static final int COLUMN_HEIGHT = 28;

	public static Control createLookupField(Composite composite, Field field, int row, int column,
			FormToolkit formToolkit, IEventBroker broker, Map<String, Control> controls, MPerspective perspective) {
		String labelText = field.getLabel() == null ? "" : field.getLabel();
		Label label = formToolkit.createLabel(composite, labelText, SWT.RIGHT);
		LookupControl lookupControl = new LookupControl(composite, SWT.LEFT);
		Label descriptionLabel = formToolkit.createLabel(composite, "", SWT.LEFT);
		FormData lookupFormData = new FormData();
		FormData labelFormData = new FormData();
		FormData descriptionLabelFormData = new FormData();

		lookupFormData.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT);
		lookupFormData.left = new FormAttachment(composite, MARGIN_LEFT * (column + 1) + (column + 1) * COLUMN_WIDTH);
		lookupFormData.width = COLUMN_WIDTH;

		labelFormData.top = new FormAttachment(lookupControl, 0, SWT.CENTER);
		labelFormData.right = new FormAttachment(lookupControl, MARGIN_LEFT * -1, SWT.LEFT);
		labelFormData.width = COLUMN_WIDTH;

		descriptionLabelFormData.top = new FormAttachment(lookupControl, 0, SWT.CENTER);
		descriptionLabelFormData.left = new FormAttachment(lookupControl, 0, SWT.RIGHT);
//		descriptionLabelFormData.right = new FormAttachment(composite, 0, SWT.RIGHT);
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

		lookupControl.addTwistieMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			/*
			 * Aufruf der Prozedur mit um den Datensatz zu laden. prüfen ob noch andere
			 * LookUpFelder eingetragen wurden
			 */
			public void mouseDown(MouseEvent e) {
				Map<MPerspective, String> brokerObject = new HashMap<>();
				brokerObject.put(perspective, field.getName());
				broker.post(Constants.BROKER_WFCLOADALLLOOKUPVALUES, brokerObject);
			}

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub

			}

		});
		// hinterlegen einer Methode in die component, um stehts die Daten des richtigen
		// Indexes in der Detailview aufzulisten. Hierfür wird eine Anfrage an den CAS
		// gestartet, um die Werte des zugehörigen Keys zu erhalten
		lookupControl.setData(Constants.CONTROL_LOOKUPCONSUMER, (Consumer<Map>) m -> {

			int keyLong = (Integer) ValueBuilder.value((Value) m.get("value")).create();
			lookupControl.setData(Constants.CONTROL_DATATYPE, ValueBuilder.value((Value) m.get("value")).getDataType());
			lookupControl.setData(Constants.CONTROL_KEYLONG, keyLong);

			CompletableFuture<?> tableFuture;
			tableFuture = LookupCASRequestUtil.getRequestedTable(keyLong, null, field, controls,
					(IDataService) m.get("dataService"), (UISynchronize) m.get("sync"), "Resolve");
			tableFuture.thenAccept(ta -> ((UISynchronize) m.get("sync")).asyncExec(() -> {
				Table t = null;
				if (ta instanceof SqlProcedureResult) {
					SqlProcedureResult sql = (SqlProcedureResult) ta;
					t = sql.getResultSet();
				} else if (ta instanceof Table) {
					t = (Table) ta;
				}
				updateSelectedLookupEntry(t, (Control) m.get("control"));

			}));
		});

		return lookupControl;
	}

	/**
	 * Abfangen der Table der in der Consume-Methode versendeten CAS-Abfrage mit
	 * Bindung zur Componente
	 *
	 * @param ta
	 * @param c
	 */
	public static void updateSelectedLookupEntry(Table ta, Control c) {
		Row r = ta.getRows().get(0);
		LookupControl lc = (LookupControl) c;
		int index = ta.getColumnIndex(Constants.TABLE_KEYTEXT);
		Value v = r.getValue(index);
		lc.setText((String) ValueBuilder.value(v).create());
		if (lc.getDescription() != null && ta.getColumnIndex(Constants.TABLE_DESCRIPTION) > -1) {
			if (r.getValue(ta.getColumnIndex(Constants.TABLE_DESCRIPTION)) != null) {
				lc.getDescription().setText((String) ValueBuilder
						.value(r.getValue(ta.getColumnIndex(Constants.TABLE_DESCRIPTION))).create());
			} else {
				lc.getDescription().setText("");
			}
		}
	}
}
