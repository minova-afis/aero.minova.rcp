package aero.minova.rcp.rcp.nattable;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDoubleDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultIntegerDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByConfigAttributes;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.summary.IGroupBySummaryProvider;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.summaryrow.SummaryRowLayer;

import aero.minova.rcp.constants.AggregateOption;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Row;

public class MinovaIndexConfiguration extends MinovaColumnConfiguration {

	public MinovaIndexConfiguration(List<Column> columns, Form form) {
		super(columns, form);
	}

	@Override
	public void configureRegistry(IConfigRegistry configRegistry) {
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.NEVER_EDITABLE);
		configureCells(configRegistry);
		configureGroupSummary(configRegistry);
	}

	private void configureCells(IConfigRegistry configRegistry) {
		int i = 0;
		for (Column column : columns) {
			if (column.getType().equals(DataType.BOOLEAN)) {
				configureIntegerCell(configRegistry, i, SummaryRowLayer.DEFAULT_SUMMARY_COLUMN_CONFIG_LABEL_PREFIX);
				configureBooleanCell(configRegistry, i++, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX);
			} else if (column.getType().equals(DataType.INSTANT) && formColumns.get(column.getName()).getShortDate() != null) {
				configureShortDateCell(configRegistry, i, SummaryRowLayer.DEFAULT_SUMMARY_COLUMN_CONFIG_LABEL_PREFIX);
				configureShortDateCell(configRegistry, i++, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX);
			} else if (column.getType().equals(DataType.INSTANT) && formColumns.get(column.getName()).getShortTime() != null) {
				configureShortTimeCell(configRegistry, i, SummaryRowLayer.DEFAULT_SUMMARY_COLUMN_CONFIG_LABEL_PREFIX);
				configureShortTimeCell(configRegistry, i++, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX);
			} else if (column.getType().equals(DataType.INSTANT) && formColumns.get(column.getName()).getDateTime() != null) {
				configureDateTimeCell(configRegistry, i, SummaryRowLayer.DEFAULT_SUMMARY_COLUMN_CONFIG_LABEL_PREFIX);
				configureDateTimeCell(configRegistry, i++, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX);
			} else if (column.getType().equals(DataType.DOUBLE)) {
				configureDoubleCell(configRegistry, i, formColumns.get(column.getName()), SummaryRowLayer.DEFAULT_SUMMARY_COLUMN_CONFIG_LABEL_PREFIX);
				configureDoubleCell(configRegistry, i++, formColumns.get(column.getName()), ColumnLabelAccumulator.COLUMN_LABEL_PREFIX);
			} else if (column.getType().equals(DataType.INTEGER)) {
				configureIntegerCell(configRegistry, i, SummaryRowLayer.DEFAULT_SUMMARY_COLUMN_CONFIG_LABEL_PREFIX);
				configureIntegerCell(configRegistry, i++, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX);
			} else {
				configureTextCell(configRegistry, i, SummaryRowLayer.DEFAULT_SUMMARY_COLUMN_CONFIG_LABEL_PREFIX);
				configureTextCell(configRegistry, i++, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX);
			}
		}
	}

	private void configureGroupSummary(IConfigRegistry configRegistry) {
		int i = 0;
		for (Column column : columns) {
			IGroupBySummaryProvider<Row> summaryProvider = null;

			if (formColumns.get(column.getName()).getAggregate() != null) {
				AggregateOption agg = AggregateOption.valueOf(formColumns.get(column.getName()).getAggregate());
				switch (agg) {
				case AVERAGE:
					summaryProvider = (columnIndex, children) -> sumRows(columnIndex, children) / children.size();
					break;

				case COUNT:

					if (column.getType().equals(DataType.BOOLEAN)) {
						summaryProvider = this::getBooleanSum;
					} else {
						summaryProvider = (columnIndex, children) -> children.size();
					}
					break;

				case MAX:
					summaryProvider = this::getMax;
					break;

				case MIN:
					summaryProvider = this::getMin;
					break;

				case SUM:
					summaryProvider = this::sumRows;
					break;
				default:
					break;
				}
			}

			// Summe ("total" in .xml)
			if (formColumns.get(column.getName()).isTotal() != null && formColumns.get(column.getName()).isTotal()) {
				summaryProvider = this::sumRows;
			}

			if (summaryProvider != null) {
				configRegistry.registerConfigAttribute(GroupByConfigAttributes.GROUP_BY_SUMMARY_PROVIDER, summaryProvider, DisplayMode.NORMAL,
						ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + i);
			}

			i++;
		}
		configRegistry.registerConfigAttribute(GroupByConfigAttributes.GROUP_BY_CHILD_COUNT_PATTERN, "[{0}]");
	}

	private Object getMin(int columnIndex, List<Row> children) {
		double min = Double.MAX_VALUE;
		for (Row r : children) {
			if (r.getValue(columnIndex) != null && r.getValue(columnIndex).getIntegerValue() != null && r.getValue(columnIndex).getIntegerValue() < min) {
				min = r.getValue(columnIndex).getIntegerValue();
			} else if (r.getValue(columnIndex) != null && r.getValue(columnIndex).getDoubleValue() != null && r.getValue(columnIndex).getDoubleValue() < min) {
				min = r.getValue(columnIndex).getDoubleValue();
			} else if (r.getValue(columnIndex) != null && r.getValue(columnIndex).getBigDecimalValue() != null
					&& r.getValue(columnIndex).getBigDecimalValue() < min) {
				min = r.getValue(columnIndex).getBigDecimalValue();
			}
		}
		return min;
	}

	private Object getMax(int columnIndex, List<Row> children) {
		double max = Double.MIN_VALUE;
		for (Row r : children) {
			if (r.getValue(columnIndex) != null && r.getValue(columnIndex).getIntegerValue() != null && r.getValue(columnIndex).getIntegerValue() > max) {
				max = r.getValue(columnIndex).getIntegerValue();
			} else if (r.getValue(columnIndex) != null && r.getValue(columnIndex).getDoubleValue() != null && r.getValue(columnIndex).getDoubleValue() > max) {
				max = r.getValue(columnIndex).getDoubleValue();
			} else if (r.getValue(columnIndex) != null && r.getValue(columnIndex).getBigDecimalValue() != null
					&& r.getValue(columnIndex).getBigDecimalValue() > max) {
				max = r.getValue(columnIndex).getBigDecimalValue();
			}
		}
		return max;
	}

	private Object getBooleanSum(int columnIndex, List<Row> children) {
		int sum = 0;
		for (Row r : children) {
			if (r.getValue(columnIndex) != null && Boolean.TRUE.equals(r.getValue(columnIndex).getBooleanValue())) {
				sum++;
			}
		}
		return sum;
	}

	private double sumRows(int columnIndex, List<Row> children) {
		double total = 0;
		for (Row r : children) {
			if (r.getValue(columnIndex) != null && r.getValue(columnIndex).getIntegerValue() != null) {
				total += r.getValue(columnIndex).getIntegerValue();
			} else if (r.getValue(columnIndex) != null && r.getValue(columnIndex).getDoubleValue() != null) {
				total += r.getValue(columnIndex).getDoubleValue();
			} else if (r.getValue(columnIndex) != null && r.getValue(columnIndex).getBigDecimalValue() != null) {
				total += r.getValue(columnIndex).getBigDecimalValue();
			}
		}
		return total;
	}

	private void configureIntegerCell(IConfigRegistry configRegistry, int columnIndex, String configLabel) {
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.RIGHT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL, configLabel + columnIndex);

		NumberFormat nf = NumberFormat.getInstance();
		DefaultIntegerDisplayConverter defaultIntegerDisplayConverter = new DefaultIntegerDisplayConverter(true);
		defaultIntegerDisplayConverter.setNumberFormat(nf);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, defaultIntegerDisplayConverter, DisplayMode.NORMAL,
				configLabel + columnIndex);

	}

	private void configureDateTimeCell(IConfigRegistry configRegistry, int columnIndex, String configLabel) {
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL, configLabel + columnIndex);
		if (locale == null) {
			locale = Locale.getDefault();
		}
		DateTimeDisplayConverter dateTimeDisplayConverter = new DateTimeDisplayConverter(locale);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, dateTimeDisplayConverter, DisplayMode.NORMAL, configLabel + columnIndex);

	}

	private void configureShortTimeCell(IConfigRegistry configRegistry, int columnIndex, String configLabel) {
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL, configLabel + columnIndex);
		if (locale == null) {
			locale = Locale.getDefault();
		}
		ShortTimeDisplayConverter shortTimeDisplayConverter = new ShortTimeDisplayConverter(locale);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, shortTimeDisplayConverter, DisplayMode.NORMAL,
				configLabel + columnIndex);

	}

	private void configureShortDateCell(IConfigRegistry configRegistry, int columnIndex, String configLabel) {
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL, configLabel + columnIndex);
		if (locale == null) {
			locale = Locale.getDefault();
		}
		ShortDateDisplayConverter shortDateDisplayConverter = new ShortDateDisplayConverter(locale);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, shortDateDisplayConverter, DisplayMode.NORMAL,
				configLabel + columnIndex);
	}

	private void configureBooleanCell(IConfigRegistry configRegistry, int columnIndex, String configLabel) {
		// visuelle anpassung [x] oder [_] oder [-]
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new TriStateCheckBoxPainter(), DisplayMode.NORMAL, configLabel + columnIndex);

		// using a CheckBoxCellEditor also needs a Boolean conversion to work correctly
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new BooleanDisplayConverter(), DisplayMode.NORMAL,
				configLabel + columnIndex);
	}

	private void configureDoubleCell(IConfigRegistry configRegistry, int columnIndex, aero.minova.rcp.form.model.xsd.Column column, String configLabel) {

		int decimals = 0;
		if (column.getNumber() != null) {
			decimals = column.getNumber().getDecimals();
		} else if (column.getPercentage() != null) {
			decimals = column.getPercentage().getDecimals();
		} else if (column.getMoney() != null) {
			decimals = column.getMoney().getDecimals();
		}

		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.RIGHT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL, configLabel + columnIndex);

		if (locale == null) {
			locale = Locale.getDefault();
		}
		NumberFormat numberFormat = NumberFormat.getInstance(locale);
		numberFormat.setMinimumFractionDigits(decimals);
		numberFormat.setMaximumFractionDigits(decimals);

		DefaultDoubleDisplayConverter defaultDoubleDisplayConverter = new DefaultDoubleDisplayConverter(true);
		defaultDoubleDisplayConverter.setNumberFormat(numberFormat);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, defaultDoubleDisplayConverter, DisplayMode.NORMAL,
				configLabel + columnIndex);

	}

	private void configureTextCell(IConfigRegistry configRegistry, int columnIndex, String configLabel) {
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL, configLabel + columnIndex);
	}
}
