package aero.minova.rcp.rcp.nattable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.e4.core.services.translation.TranslationService;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.model.LookupValue;

public class GridLookupContentProvider {

	TranslationService translationService;
	List<LookupValue> values;
	IDataService dataService;
	String tableName;

	public GridLookupContentProvider(IDataService dataService, String tablename, TranslationService translationService) {
		values = new ArrayList<>();
		this.dataService = dataService;
		this.tableName = tablename;
		this.translationService = translationService;
		update();
	}

	public List<LookupValue> getValues() {
		return values;
	}

	public void update() {
		CompletableFuture<List<LookupValue>> listLookup = dataService.resolveGridLookup(tableName, true);

		listLookup.thenAccept(l -> {
			values.clear();
			values.addAll(l);
			translateAllLookups();
		});
	}

	public void translateAllLookups() {
		if (tableName != null) {
			for (LookupValue lv : values) {
				translateLookup(lv);
			}
		}
	}

	public void translateLookup(LookupValue lv) {
		String translateKey = tableName + ".KeyText." + lv.keyLong;
		String translated = translationService.translate("@" + tableName + ".KeyText." + lv.keyLong, null);
		lv.keyText = translateKey.equals(translated) ? lv.keyText : translated;

		translateKey = tableName + ".Description." + lv.keyLong;
		translated = translationService.translate("@" + tableName + ".Description." + lv.keyLong, null);
		lv.description = translateKey.equals(translated) ? lv.description : translated;
	}

}
