package aero.minova.rcp.rcp.handlers;

import java.util.List;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;

import aero.minova.rcp.plugin1.model.Row;

public class PrintIndexHandler {

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SELECTION) List<Row> rows) {
		for (Row row : rows) {
			System.out.println(row);
		}
	}
}
