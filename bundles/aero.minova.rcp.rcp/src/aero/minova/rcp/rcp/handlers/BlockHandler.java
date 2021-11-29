package aero.minova.rcp.rcp.handlers;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Evaluate;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.model.application.ui.menu.impl.HandledToolItemImpl;
import org.eclipse.e4.ui.services.IServiceConstants;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.builder.RowBuilder;
import aero.minova.rcp.model.event.ValueChangeEvent;
import aero.minova.rcp.model.event.ValueChangeListener;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.helper.ActionCode;
import aero.minova.rcp.rcp.parts.WFCDetailPart;

public class BlockHandler implements ValueChangeListener {

	@Inject
	IDataFormService dataFormService;

	@Inject
	IDataService dataService;

	@Inject
	IEventBroker broker;

	HandledToolItemImpl blockedToolItem;
	WFCDetailPart detail;
	MPart mPart;
	MField keyLong;
	MField blocked;
	boolean firstCall = true;

	/**
	 * Button entsprechend der Maske anzeigen
	 * 
	 * @param part
	 * @return
	 */
	@Evaluate
	public boolean visible(MPart part) {
		this.detail = (WFCDetailPart) part.getObject();
		return detail.getForm().getDetail().isButtonBlockVisible();
	}

	@CanExecute
	public boolean canExecute(MPart part, @Named(IServiceConstants.ACTIVE_SELECTION) @Optional Object selection) {
		if (firstCall) {
			init(part);
		}

		return keyLong.getValue() != null;
	}

	private void init(MPart part) {
		this.mPart = part;
		this.detail = (WFCDetailPart) part.getObject();
		this.keyLong = detail.getDetail().getField("KeyLong");
		this.blocked = detail.getDetail().getField("Blocked");
		blocked.addValueChangeListener(this);

		// Knopf finden
		for (MToolBarElement item : mPart.getToolbar().getChildren()) {
			if (item.getElementId().equals("aero.minova.rcp.rcp.handledtoolitem.block")) {
				blockedToolItem = (HandledToolItemImpl) item;
			}
		}

		firstCall = false;
	}

	@Execute
	public void execute() {

		// Dirty-Flag verhindern
		Table selectedTable = detail.getRequestUtil().getSelectedTable();
		selectedTable.setValue("Blocked", 0, new Value(blockedToolItem.isSelected()));

		// Blocked-Feld entsprechend mit Wert belegen
		blocked.setValue(new Value(blockedToolItem.isSelected()), false);

		// Blocked-Prozedur aufrufen
		Table formTable = dataFormService.getTableFromFormDetail(detail.getForm(), Constants.BLOCK_REQUEST);
		RowBuilder rb = RowBuilder.newRow();
		for (Column c : formTable.getColumns()) {
			rb.withValue(detail.getDetail().getField(c.getName()).getValue());
		}
		formTable.addRow(rb.create());
		CompletableFuture<SqlProcedureResult> res = dataService.callProcedureAsync(formTable);

		try {
			SqlProcedureResult sqlProcedureResult = res.get();
			if (sqlProcedureResult != null && sqlProcedureResult.getReturnCode() >= 0) {
				String msg = blockedToolItem.isSelected() ? "msg.BlockSuccessful" : "msg.UnblockSuccessful";
				broker.post(Constants.BROKER_SHOWNOTIFICATION, msg);
				broker.post(Constants.BROKER_SENDEVENTTOHELPER, ActionCode.AFTERBLOCK);
			} else {
				// Bei Misserfolg Felder neu laden, damit blockiert richtig gesetzt wird
				broker.post(Constants.BROKER_RELOADFIELDS, null);
			}
		} catch (InterruptedException | ExecutionException e) {
			// Bei Misserfolg Felder neu laden, damit blockiert richtig gesetzt wird
			broker.post(Constants.BROKER_RELOADFIELDS, null);
		}
	}

	@Override
	/**
	 * Button de-/selektieren, damit man sieht, ob Blockiert aktiv ist
	 */
	public void valueChange(ValueChangeEvent evt) {
		boolean blockedV = false;
		if (blocked.getValue() != null) {
			blockedV = blocked.getValue().getBooleanValue();
		}
		blockedToolItem.setSelected(blockedV);
	}
}
