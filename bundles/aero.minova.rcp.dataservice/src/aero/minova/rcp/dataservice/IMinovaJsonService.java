package aero.minova.rcp.dataservice;

import aero.minova.rcp.model.Table;

public interface IMinovaJsonService {

	String table2Json(Table t);

	Table json2Table(String jsonString);

}