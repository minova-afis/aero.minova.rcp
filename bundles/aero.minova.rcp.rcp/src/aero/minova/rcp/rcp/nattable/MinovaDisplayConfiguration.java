package aero.minova.rcp.rcp.nattable;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultBooleanDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDoubleDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultIntegerDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.TextCellEditor;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByConfigAttributes;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.summary.IGroupBySummaryProvider;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.painter.cell.CheckBoxPainter;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;

import aero.minova.rcp.constants.AggregateOption;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.rcp.widgets.TriStateCheckBoxPainter;

public class MinovaDisplayConfiguration extends AbstractRegistryConfiguration {

	private List<Column> columns;
	private Locale locale;
	private TranslationService translationService;
	private Form form;
	private Map<String, aero.minova.rcp.form.model.xsd.Column> formColumns;

	public MinovaDisplayConfiguration(List<Column> columns, TranslationService translationService, Form form) {
		this.columns = columns;
		this.translationService = translationService;
		this.form = form;
		initFormFields();
	}

	public void initFormFields() {
		formColumns = new HashMap<>();
		List<aero.minova.rcp.form.model.xsd.Column> column = form.getIndexView().getColumn();
		for (aero.minova.rcp.form.model.xsd.Column column2 : column) {
			formColumns.put(column2.getName(), column2);
		}
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
				configureBooleanCell(configRegistry, i++);
			} else if (column.getType().equals(DataType.INSTANT) && formColumns.get(column.getName()).getShortDate() != null) {
				configureShortDateCell(configRegistry, i++);
			} else if (column.getType().equals(DataType.INSTANT) && formColumns.get(column.getName()).getShortTime() != null) {
				configureShortTimeCell(configRegistry, i++);
			} else if (column.getType().equals(DataType.INSTANT) && formColumns.get(column.getName()).getDateTime() != null) {
				configureDateTimeCell(configRegistry, i++);
			} else if (column.getType().equals(DataType.DOUBLE)) {
				configureDoubleCell(configRegistry, i++, formColumns.get(column.getName()).getNumber().getDecimals());
			} else if (column.getType().equals(DataType.INTEGER)) {
				configureIntegerCell(configRegistry, i++);
			} else {
				configureTextCell(configRegistry, i++);
			}
		}
	}

	private void configureGroupSummary(IConfigRegistry configRegistry) {
		int i = 0;
		for (Column column : columns) {
			IGroupBySummaryProvider summaryProvider = null;

			if (formColumns.get(column.getName()).getAggregate() != null) {
				AggregateOption agg = AggregateOption.valueOf(formColumns.get(column.getName()).getAggregate());
				switch (agg) {
				case AVERAGE:
					summaryProvider = new IGroupBySummaryProvider() {
						@Override
						public Object summarize(int columnIndex, List children) {
							double total = 0;
							for (Object c : children) {
								Row r = (Row) c;
								if (r.getValue(columnIndex) != null && r.getValue(columnIndex).getIntegerValue() != null)
									total += r.getValue(columnIndex).getIntegerValue();
								else if (r.getValue(columnIndex) != null && r.getValue(columnIndex).getDoubleValue() != null)
									total += r.getValue(columnIndex).getDoubleValue();
							}
							return total / children.size();
						}
					};
					break;

				case COUNT:
					summaryProvider = new IGroupBySummaryProvider() {
						@Override
						public Object summarize(int columnIndex, List children) {
							return children.size();
						}
					};
					break;

				case MAX:
					summaryProvider = new IGroupBySummaryProvider() {
						@Override
						public Object summarize(int columnIndex, List children) {
							double max = Double.MIN_VALUE;
							for (Object c : children) {
								Row r = (Row) c;
								if (r.getValue(columnIndex) != null && r.getValue(columnIndex).getIntegerValue() != null) {
									if (r.getValue(columnIndex).getIntegerValue() > max)
										max = r.getValue(columnIndex).getIntegerValue();
								} else if (r.getValue(columnIndex) != null && r.getValue(columnIndex).getDoubleValue() != null) {
									if (r.getValue(columnIndex).getDoubleValue() > max)
										max = r.getValue(columnIndex).getDoubleValue();
								}
							}
							return max;
						}
					};
					break;

				case MIN:
					summaryProvider = new IGroupBySummaryProvider() {
						@Override
						public Object summarize(int columnIndex, List children) {
							double min = Double.MAX_VALUE;
							for (Object c : children) {
								Row r = (Row) c;
								if (r.getValue(columnIndex) != null && r.getValue(columnIndex).getIntegerValue() != null) {
									if (r.getValue(columnIndex).getIntegerValue() < min)
										min = r.getValue(columnIndex).getIntegerValue();
								} else if (r.getValue(columnIndex) != null && r.getValue(columnIndex).getDoubleValue() != null) {
									if (r.getValue(columnIndex).getDoubleValue() < min)
										min = r.getValue(columnIndex).getDoubleValue();
								}
							}
							return min;
						}
					};
					break;

				case SUM:
					summaryProvider = new IGroupBySummaryProvider() {
						@Override
						public Object summarize(int columnIndex, List children) {
							double total = 0;
							for (Object c : children) {
								Row r = (Row) c;
								if (r.getValue(columnIndex) != null && r.getValue(columnIndex).getIntegerValue() != null)
									total += r.getValue(columnIndex).getIntegerValue();
								else if (r.getValue(columnIndex) != null && r.getValue(columnIndex).getDoubleValue() != null)
									total += r.getValue(columnIndex).getDoubleValue();
							}
							return total;
						}
					};
					break;
				default:
					break;
				}

			}

			// Summe ("total" in .xml)
			if (formColumns.get(column.getName()).isTotal() != null && formColumns.get(column.getName()).isTotal()) {
				summaryProvider = new IGroupBySummaryProvider() {
					@Override
					public Object summarize(int columnIndex, List children) {
						double total = 0;
						for (Object c : children) {
							Row r = (Row) c;
							if (r.getValue(columnIndex) != null && r.getValue(columnIndex).getIntegerValue() != null)
								total += r.getValue(columnIndex).getIntegerValue();
							else if (r.getValue(columnIndex) != null && r.getValue(columnIndex).getDoubleValue() != null)
								total += r.getValue(columnIndex).getDoubleValue();
						}
						return total;
					}
				};
			}

			if (summaryProvider != null) {
				configRegistry.registerConfigAttribute(GroupByConfigAttributes.GROUP_BY_SUMMARY_PROVIDER, summaryProvider, DisplayMode.NORMAL,
						ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + i);
			}

			i++;
		}
		configRegistry.registerConfigAttribute(GroupByConfigAttributes.GROUP_BY_CHILD_COUNT_PATTERN, "[{0}]");
	}

	private void configureIntegerCell(IConfigRegistry configRegistry, int columnIndex) {
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.RIGHT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

		NumberFormat nf = NumberFormat.getInstance();
		DefaultIntegerDisplayConverter defaultIntegerDisplayConverter = new DefaultIntegerDisplayConverter(true);
		defaultIntegerDisplayConverter.setNumberFormat(nf);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, defaultIntegerDisplayConverter, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

	}

	private void configureDateTimeCell(IConfigRegistry configRegistry, int columnIndex) {
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		if (locale == null) {
			locale = Locale.getDefault();
		}
		ShortDateTimeDisplayConverter shortDateTimeDisplayConverter = new ShortDateTimeDisplayConverter(locale);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, shortDateTimeDisplayConverter, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

	}

	private void configureShortTimeCell(IConfigRegistry configRegistry, int columnIndex) {
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		if (locale == null) {
			locale = Locale.getDefault();
		}
		ShortTimeDisplayConverter shortTimeDisplayConverter = new ShortTimeDisplayConverter(locale);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, shortTimeDisplayConverter, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

	}

	private void configureShortDateCell(IConfigRegistry configRegistry, int columnIndex) {
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		if (locale == null) {
			locale = Locale.getDefault();
		}
		ShortDateDisplayConverter shortDateDisplayConverter = new ShortDateDisplayConverter(locale);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, shortDateDisplayConverter, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
	}

	private void configureBooleanCell(IConfigRegistry configRegistry, int columnIndex) {
		// visuelle anpassung [x] oder [_] oder [-]
		//
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new TriStateCheckBoxPainter(), DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

		// using a CheckBoxCellEditor also needs a Boolean conversion to work
		// correctly
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new BooleanDisplayConverter(), DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
	}

	private void configureDoubleCell(IConfigRegistry configRegistry, int columnIndex, int decimals) {
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.RIGHT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

		if (locale == null) {
			locale = Locale.getDefault();
		}
		NumberFormat numberFormat = NumberFormat.getInstance(locale);
		numberFormat.setMinimumFractionDigits(decimals);
		numberFormat.setMaximumFractionDigits(decimals);

		DefaultDoubleDisplayConverter defaultDoubleDisplayConverter = new DefaultDoubleDisplayConverter(true);
		defaultDoubleDisplayConverter.setNumberFormat(numberFormat);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, defaultDoubleDisplayConverter, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

	}

	private void configureInstantCell(IConfigRegistry configRegistry, int columnIndex) {
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

	}

	private void configureTextCell(IConfigRegistry configRegistry, int columnIndex) {
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
	}

	private void registerDoubleEditor(IConfigRegistry configRegistry, int columnIndex) {
		// register a TextCellEditor for column two that commits on key up/down
		// moves the selection after commit by enter
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new TextCellEditor(true, true), DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

		// configure to open the adjacent editor after commit
		// default behavior - enter - down, tab - right, shift+tab - left, arrows -
		// arrow direction
		configRegistry.registerConfigAttribute(EditConfigAttributes.OPEN_ADJACENT_EDITOR, Boolean.TRUE, DisplayMode.EDIT,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

		// TODO get the number format of the user
		NumberFormat nf = NumberFormat.getInstance();
//		NumberFormat nf = NumberFormat.getInstance(new Locale("en", "EN"));
		DefaultDoubleDisplayConverter defaultDoubleDisplayConverter = new DefaultDoubleDisplayConverter(true);
		defaultDoubleDisplayConverter.setNumberFormat(nf);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, defaultDoubleDisplayConverter, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
	}

	private void registerBooleanEditor(IConfigRegistry configRegistry, int columnIndex) {

		// Das hier würde einen kundenspezifischen Checkbox editor nutzen
		// configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new
		// ExampleCheckBoxPainter(),
		// DisplayMode.NORMAL, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX +
		// columnIndex);

		// The CheckBoxCellEditor can also be visualized like a check button
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new CheckBoxPainter(), DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

		// using a CheckBoxCellEditor also needs a Boolean conversion to work
		// correctly
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new DefaultBooleanDisplayConverter(), DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
	}

}
