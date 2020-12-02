package aero.minova.rcp.rcp.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;

public class MinovaProposalProvider implements IContentProposalProvider {

	List<MinovaContentProposal> proposals = new ArrayList<>();
	private Table table;
	@Override
	public IContentProposal[] getProposals(String contents, int position) {
		proposals.clear();
		for (Row row : table.getRows()) {

			Value value = row.getValue(table.getColumnIndex("Description"));
			String descr = (value != null) ? value.getStringValue() : "";
			proposals.add(
			new MinovaContentProposal(
					row.getValue(table.getColumnIndex("KeyLong")).getIntegerValue(),
					row.getValue(table.getColumnIndex("KeyText")).getStringValue(),
							descr));
		}

		return proposals.toArray(new MinovaContentProposal[0]);
	}

	public void setProposals(Table table) {
		this.table = table;
	}
}
