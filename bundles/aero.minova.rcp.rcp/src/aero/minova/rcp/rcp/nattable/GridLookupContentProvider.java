package aero.minova.rcp.rcp.nattable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.e4.core.services.translation.TranslationService;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.model.LookupValue;
import aero.minova.rcp.model.form.MLookupField;

public class GridLookupContentProvider {

	TranslationService translationService;
	List<LookupValue> values;
	IDataService dataService;
	MLookupField mField;

	public GridLookupContentProvider(IDataService dataService, MLookupField mField, TranslationService translationService) {
		values = new ArrayList<>();
		this.dataService = dataService;
		this.mField = mField;
		this.translationService = translationService;
		update();
	}

	public List<LookupValue> getValues() {
		return values;
	}

	public void update() {
		CompletableFuture<List<LookupValue>> listLookup = dataService.listLookup(mField, true);

		listLookup.thenAccept(l -> {
			values.clear();
			values.addAll(l);
			translateAllLookups();
		});
	}

	public void translateAllLookups() {
		if (getTableOrProcedureName() != null) {
			for (LookupValue lv : values) {
				translateLookup(lv);
			}
		}
	}

	public void translateLookup(LookupValue lv) {
		String translateKey = getTableOrProcedureName() + ".KeyText." + lv.keyLong;
		String translated = translationService.translate("@" + getTableOrProcedureName() + ".KeyText." + lv.keyLong, null);
		lv.keyText = translateKey.equals(translated) ? lv.keyText : translated;

		translateKey = getTableOrProcedureName() + ".Description." + lv.keyLong;
		translated = translationService.translate("@" + getTableOrProcedureName() + ".Description." + lv.keyLong, null);
		lv.description = translateKey.equals(translated) ? lv.description : translated;
	}

	private String getTableOrProcedureName() {
		if (mField.getLookupTable() != null) {
			return mField.getLookupTable();
		} else {
			return mField.getLookupProcedurePrefix();
		}
	}

}
