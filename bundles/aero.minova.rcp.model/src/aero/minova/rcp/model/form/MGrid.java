package aero.minova.rcp.model.form;

import java.util.List;

import org.eclipse.swt.graphics.Image;

import aero.minova.rcp.form.model.xsd.Grid;
import aero.minova.rcp.model.Table;

public class MGrid {

	public MGrid(String procedureSuffix) {
		super();
		this.procedureSuffix = procedureSuffix;
	}

	private String title;
	private String procedureSuffix;
	private String procedurePrefix;
	private String helperClass;
	private Image icon;
	private IGridAccessor gridAccessor;
	private boolean delReqAllParams;
	private String fill;
	private Grid grid;
	private List<MField> fields;
	private MSection mSection;
	private Table dataTable;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getProcedureSuffix() {
		return procedureSuffix;
	}

	public void setProcedureSuffix(String procedureSuffix) {
		this.procedureSuffix = procedureSuffix;
	}

	public String getProcedurePrefix() {
		return procedurePrefix;
	}

	public void setProcedurePrefix(String procedurePrefix) {
		this.procedurePrefix = procedurePrefix;
	}

	public String getHelperClass() {
		return helperClass;
	}

	public void setHelperClass(String helperClass) {
		this.helperClass = helperClass;
	}

	public Image getIcon() {
		return icon;
	}

	public void setIcon(Image icon) {
		this.icon = icon;
	}

	public IGridAccessor getGridAccessor() {
		return gridAccessor;
	}

	public void setGridAccessor(IGridAccessor gridAccessor) {
		this.gridAccessor = gridAccessor;
	}

	public boolean isDelReqAllParams() {
		return delReqAllParams;
	}

	public void setDelReqAllParams(boolean delReqAllParams) {
		this.delReqAllParams = delReqAllParams;
	}

	public String getFill() {
		return fill;
	}

	public void setFill(String fill) {
		this.fill = fill;
	}

	public Table getDataTable() {
		return dataTable;
	}

	public void setDataTable(Table dataTable) {
		this.dataTable = dataTable;
	}

	public MSection getmSection() {
		return mSection;
	}

	public void setmSection(MSection mSection) {
		this.mSection = mSection;
	}

	public List<MField> getFields() {
		return fields;
	}

	public void setFields(List<MField> fields) {
		this.fields = fields;
	}

	public void setGrid(Grid grid) {
		this.grid = grid;
	}

	public Grid getGrid() {
		return this.grid;
	}
}
