package aero.minova.rcp.dataservice.internal;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.FilterValue;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;

/**
 * Wandelt Tabellen in SQL-Strings um, die in der Datenbank verwendet werden können
 * 
 * @author janiak
 */
public class SQLStringUtil {

	private SQLStringUtil() {}

	private static final String AND_FIELD_NAME = "&";

	public static String prepareViewString(Table params) throws IllegalArgumentException {
		return prepareViewString(params, false, 0, false);
	}

	/**
	 * Diese Methode stammt ursprünglich aus "ch.minova.ncore.data.sql.SQLTools#prepareViewString". Bereitet einen View-String vor und berücksichtigt eine evtl.
	 * angegebene Maximalanzahl Ergebnisse
	 *
	 * @param params
	 *            Suchzeilen (z.B. Suchparameter), wobei auch ein Spezialfeld mit dem Namen 'AND' genutzt werden kann, um die Kriterien zu verknüpfen
	 * @param autoLike
	 *            wenn true, dann werden alle String-Parameter, die noch kein % haben, mit einem '%' am Ende versehen
	 * @param maxRows
	 *            maximale Anzahl Ergebnisse (Zeilen), die die Abfrage liefern soll, 0 für unbegrenzt
	 * @param count
	 *            Gibt an ob nur die Anzahl der Ergebniss (Zeilen), gezählt werden sollen.
	 * @return Präparierter View-String, der ausgeführt werden kann
	 * @throws IllegalArgumentException
	 * @author wild
	 */
	private static String prepareViewString(Table params, boolean autoLike, int maxRows, boolean count) throws IllegalArgumentException {
		final StringBuffer sb = new StringBuffer();
		if (params.getName() == null || params.getName().trim().length() == 0) {
			throw new IllegalArgumentException("msg.ViewNullName");
		}

		if (count) {
			sb.append("select count(1) from ");
		} else {
			if (maxRows > 0) {
				sb.append("select top ").append(maxRows).append(" ");
			} else {
				sb.append("select ");
			}
			List<Column> outputFormat = params.getColumns().stream()//
					.filter(c -> !Objects.equals(c.getName(), AND_FIELD_NAME))//
					.collect(Collectors.toList());
			if (outputFormat.isEmpty()) {
				sb.append("* from ");
			} else {
				sb.append(//
						outputFormat.stream()//
								.map(Column::getName)//
								.collect(Collectors.joining(", ")));
				sb.append(" from ");
			}
		}
		sb.append(params.getName());
		if (!params.getColumns().isEmpty() && !params.getRows().isEmpty()) {
			final String where = prepareWhereClause(params, autoLike);
			sb.append(where);
			if (!where.trim().equals("")) {
				sb.append(")");
			}
		}

		return sb.toString();
	}

	/**
	 * @param params
	 *            Suchzeilen (z.B. Suchparameter), wobei auch ein Spezialfeld mit dem Namen 'AND' genutzt werden kann, um die Kriterien zu verknüpfen
	 * @param autoLike
	 *            wenn true, dann werden alle String-Parameter, die noch kein % haben, mit einem '%' am Ende versehen
	 * @return die Where-Klausel für die angegebenen Parameter
	 * @author wild
	 */
	private static String prepareWhereClause(Table params, boolean autoLike) {
		final StringBuffer where = new StringBuffer();
		final boolean hasAndClause;
		List<Column> andFields = params.getColumns().stream()//
				.filter(c -> Objects.equals(c.getName(), AND_FIELD_NAME))//
				.collect(Collectors.toList());
		final Column andField;
		if (andFields.isEmpty()) {
			hasAndClause = false;
			andField = null;
		} else {
			hasAndClause = true;
			andField = andFields.get(0);
		}
		int andFieldIndex = params.getColumns().indexOf(andField);
		for (int rowI = 0; rowI < params.getRows().size(); rowI++) {
			final Row r = params.getRows().get(rowI);
			final boolean and;
			if (hasAndClause) {
				and = r.getValues().get(andFieldIndex).getBooleanValue();
			} else {
				and = false;
			}

			// Eine where Zeile aufbauen
			final StringBuffer clause = new StringBuffer();
			for (int colI = 0; colI < r.getValues().size(); ++colI) {
				Value def = r.getValues().get(colI);
				Column col = params.getColumns().get(colI);
				if (AND_FIELD_NAME.equalsIgnoreCase(col.getName()) || r.getValues().get(colI) == null || r.getValues().get(colI).getValue() == null) {
					continue;
				}

				final Object valObj = r.getValues().get(colI).getValue();
				String strValue = valObj.toString().trim();
				String ruleValue = "";

				Value v = r.getValues().get(colI);
				if (v instanceof FilterValue) {
					ruleValue = v.getValue().toString();
					ruleValue = ruleToString(ruleValue);
					if (((FilterValue) v).getFilterValue() != null) {
						strValue = ((FilterValue) v).getFilterValue().getValue().toString();
					} else {
						strValue = "";
					}
				}

				if (strValue != null && strValue.length() != 0) {
					if (clause.length() > 0) {
						clause.append(" and ");
					}
					clause.append(col.getName());

					if (ruleValue != null && ruleValue.length() != 0) {
						if (ruleValue.contains("in")) {
							clause.append(" in(");

							// für jeden der Komma-getrennten Werte muss ein Fragezeichen da sein
							String valuesSeperatedByString = Stream.of(strValue.split(",")) //
									.map(s -> "?").collect(Collectors.joining(", "));

							clause.append(valuesSeperatedByString).append(")");
						} else if (ruleValue.contains("between")) {
							clause.append(" between ? and ?");
						} else {
							clause.append(" ").append(ruleValue).append(" '").append(strValue).append("'");
						}
					} else {
						if (autoLike && valObj instanceof String && def.getType() == DataType.STRING && (!strValue.contains("%"))) {
							strValue += "%";
							Value newVal = new Value(strValue);
							params.getRows().get(rowI).getValues().set(colI, newVal);
						}
						if (def.getType() == DataType.STRING && (strValue.contains("%") || strValue.contains("_"))) {
							clause.append(" like");
						} else {
							clause.append(" =");
						}
						clause.append(" '").append(strValue).append("'");
					}
					// falls im Wert-Feld nichts steht, könnte immer noch die Regel is null oder is not null angefragt werden
				} else if (ruleValue != null) {
					if (ruleValue.contains("!null")) {
						if (clause.length() > 0) {
							clause.append(" and ");
						}
						clause.append(col.getName()).append(" is not null");
					} else if (ruleValue.contains("null")) {
						if (clause.length() > 0) {
							clause.append(" and ");
						}
						clause.append(col.getName()).append(" is null");
					}
				}
			}

			// Wenn es etwas gab, dann fügen wir diese Zeile der kompletten WHERE-clause hinzu.
			if (clause.length() > 0) {
				if (where.length() == 0) {
					where.append("\r\nwhere (");
				} else {
					where.append(and ? "\r\n  and " : "\r\n   or ");
				}
				where.append('(').append(clause.toString()).append(')');
			}
		}
		return where.toString();
	}

	private static String ruleToString(String rule) {
		if (rule.contains("!~")) {
			rule = "not like";
		} else if (rule.contains("~")) {
			rule = "like";
		}
		return rule;
	}

	/**
	 * Bereitet einen Prozedur-String vor
	 *
	 * @param params
	 *            SQL-Call-Parameter
	 * @return SQL-Code
	 * @throws IllegalArgumentException
	 *             Fehler, wenn die Daten in params nicht richtig sind.
	 */
	public static String prepareProcedureString(Table params) throws IllegalArgumentException {
		if (params.getName() == null || params.getName().trim().length() == 0) {
			throw new IllegalArgumentException("msg.ProcedureNullName");
		}
		final int paramCount = params.getColumns().size();

		final StringBuilder sb = new StringBuilder();
		for (Row r : params.getRows()) {
			sb.append("exec ").append(params.getName()).append(" ");
			for (int i = 0; i < paramCount; i++) {
				String val = "null";
				if (r.getValue(i) != null && r.getValue(i).getValue() != null) {
					val = r.getValue(i).getValue().toString();
					val = "'" + val + "'";
				}
				sb.append(i == 0 ? val : "," + val);
			}
			sb.append("\n");
		}
		return sb.toString().strip();
	}

}
