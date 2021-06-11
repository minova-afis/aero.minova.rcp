package aero.minova.rcp.rcp.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.UIEvents;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.event.ValueChangeEvent;
import aero.minova.rcp.model.event.ValueChangeListener;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.rcp.parts.WFCDetailPart;

public class SaveDetailHandler implements ValueChangeListener {

	@Inject
	IEventBroker broker;

	boolean firstCall = true;

	@Inject
	MPart part;

	// Während gespeichert wird soll der Handler daktiviert sein
	private boolean saving;

	@Execute
	public void execute(@Optional MPerspective perspective) {
		saving = true;
		// canExecute() Methode wird aufgerufen, damit Knopf deaktiviert wird
		broker.send(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, "aero.minova.rcp.rcp.handledtoolitem.save");
		broker.post(Constants.BROKER_SAVEENTRY, perspective);
	}

	@CanExecute
	public boolean canExecute(MPart part, @Named(IServiceConstants.ACTIVE_SELECTION) @Optional Object selection) {
		if (part.getObject() instanceof WFCDetailPart && !saving) {
			MDetail detail = ((WFCDetailPart) part.getObject()).getDetail();

			// Handler als Listener hinzufügen, damit auf Änderungen reagiert werden kann
			if (firstCall) {
				for (MField f : detail.getFields()) {
					f.addValueChangeListener(this);
				}
			}
			firstCall = false;

			return detail.allFieldsValid();
		}
		return false;
	}

	@Override
	public void valueChange(ValueChangeEvent evt) {
		// canExecute() Methode wird aufgerufen
		broker.send(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, "aero.minova.rcp.rcp.handledtoolitem.save");
	}

	// Speichern wieder aktivieren
	@Inject
	@Optional
	public void saveComplete(@UIEventTopic(Constants.BROKER_SAVECOMPLETE) boolean complete) {
		saving = false;
	}
}