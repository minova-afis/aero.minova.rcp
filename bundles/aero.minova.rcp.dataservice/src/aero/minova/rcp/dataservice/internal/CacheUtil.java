package aero.minova.rcp.dataservice.internal;

import aero.minova.rcp.model.form.MField;

public class CacheUtil {

	public static String getNameList(MField field) {
		if (field.getLookupParameters() == null) {
			return "";
		}

		String hashName = field.getLookupProcedurePrefix() + "List[";

		for (String paramName : field.getLookupParameters()) {
			MField paramField = field.getDetail().getField(paramName);
			hashName += "(" + paramField.getValue() + "),";

		}

		if (hashName.endsWith(",")) {
			hashName = hashName.substring(0, hashName.length() - 1);
		}
		hashName += "]";
		return hashName;
	}
}
