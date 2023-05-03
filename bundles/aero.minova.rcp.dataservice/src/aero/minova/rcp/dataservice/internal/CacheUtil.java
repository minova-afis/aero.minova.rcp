package aero.minova.rcp.dataservice.internal;

import aero.minova.rcp.model.form.MField;

public class CacheUtil {

	private CacheUtil() {}

	public static String getNameList(MField field) {
		if (field.getLookupParameters() == null) {
			return field.getLookupProcedurePrefix();
		}

		StringBuilder hashName = new StringBuilder(field.getLookupProcedurePrefix() + "[");

		for (String paramName : field.getLookupParameters()) {
			MField paramField = field.getDetail().getField(paramName);
			hashName.append("(" + paramField.getValue() + "),");
		}

		String res = hashName.toString();
		if (res.endsWith(",")) {
			res = res.substring(0, res.length() - 1);
		}
		res += "]";
		return res;
	}
}
