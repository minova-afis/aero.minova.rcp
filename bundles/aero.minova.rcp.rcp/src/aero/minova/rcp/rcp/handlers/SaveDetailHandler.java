package aero.minova.rcp.rcp.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.model.application.ui.menu.impl.HandledToolItemImpl;
import org.eclipse.e4.ui.workbench.UIEvents;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.event.ValueChangeEvent;
import aero.minova.rcp.model.event.ValueChangeListener;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MGrid;
import aero.minova.rcp.rcp.parts.WFCDetailPart;
import aero.minova.rcp.rcp.util.DirtyFlagUtil;

public class SaveDetailHandler implements ValueChangeListener {

	@Inject
	IEventBroker broker;

	boolean firstCall = true;

	@Inject
	MPart part;

	@Inject
	@Optional
	DirtyFlagUtil dirtyFlagUtil;

	// Während gespeichert wird soll der Handler daktiviert sein
	private boolean saving;

	MField firstPrimaryKey;
	HandledToolItemImpl saveToolItem;
	MPart mPart;
	WFCDetailPart detail;
	MDetail mDetail;

	@CanExecute
	public boolean canExecute(MPart mPart) {
		if (firstCall) {
			init(mPart);
		}

		return !saving && mDetail.allFieldsAndGridsValid() && dirtyFlagUtil.isDirty();
	}

	private void init(MPart mPart) {
		this.mPart = mPart;
		detail = (WFCDetailPart) part.getObject();
		mDetail = detail.getDetail();

		// Bei Booking muss das Icon und Label angepasst werden
		if (mDetail.isBooking()) {
			this.firstPrimaryKey = mDetail.getPrimaryFields().get(0);
			firstPrimaryKey.addValueChangeListener(this); // Damit zwischen Book und Correct gewechselt wird

			for (MToolBarElement item : mPart.getToolbar().getChildren()) {
				if (Constants.SAVE_DETAIL_BUTTON.equals(item.getElementId())) {
					saveToolItem = (HandledToolItemImpl) item;
					saveToolItem.setIconURI(saveToolItem.getIconURI().replace("SaveRecord", "Book"));
					saveToolItem.setLabel("@Action.Book");
					saveToolItem.setTooltip("@Action.Book");
					break;
				}
			}
		}

		// Bei allen Änderungen @CanExecute aufrufen
		for (MField f : detail.getDetail().getFields()) {
			f.addValueChangeListener(evt -> broker.send(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, Constants.SAVE_DETAIL_BUTTON));
		}
		for (MGrid g : detail.getDetail().getGrids()) {
			g.addGridChangeListener(evt -> broker.send(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, Constants.SAVE_DETAIL_BUTTON));
		}

		firstCall = false;
	}

	@Execute
	public void execute(@Optional MPerspective perspective) {
		saving = true;
		// canExecute() Methode wird aufgerufen, damit Knopf deaktiviert wird
		broker.send(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, Constants.SAVE_DETAIL_BUTTON);
		broker.post(Constants.BROKER_SAVEENTRY, perspective);
	}

	/**
	 * Icon und Label updaten
	 */
	@Override
	public void valueChange(ValueChangeEvent evt) {
		boolean setToCorrect = firstPrimaryKey.getValue() != null;

		if (setToCorrect) {
			saveToolItem.setIconURI(saveToolItem.getIconURI().replace("/Book", "/CorrectBooking"));
			saveToolItem.setLabel("@Action.Correct");
			saveToolItem.setTooltip("@Action.Correct");
		} else {
			saveToolItem.setIconURI(saveToolItem.getIconURI().replace("/CorrectBooking", "/Book"));
			saveToolItem.setLabel("@Action.Book");
			saveToolItem.setTooltip("@Action.Book");
		}
	}

	// Speichern wieder aktivieren
	@Inject
	@Optional
	public void saveComplete(@UIEventTopic(Constants.BROKER_SAVECOMPLETE) boolean complete) {
		saving = false;
	}

}