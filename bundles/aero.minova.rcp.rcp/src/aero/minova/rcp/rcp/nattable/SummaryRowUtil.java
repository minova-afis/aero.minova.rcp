package aero.minova.rcp.rcp.nattable;

import java.time.Instant;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.summaryrow.ISummaryProvider;
import org.eclipse.nebula.widgets.nattable.summaryrow.SummaryRowConfigAttributes;
import org.eclipse.nebula.widgets.nattable.summaryrow.SummaryRowLayer;

import aero.minova.rcp.constants.AggregateOption;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.form.model.xsd.Grid;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.nattable.data.MinovaColumnPropertyAccessor;
import ca.odell.glazedlists.SortedList;

public class SummaryRowUtil {

	private SummaryRowUtil() {}

	public static void configureSummary(Form form, NatTable natTable, SortedList<Row> sortedList, MinovaColumnPropertyAccessor columnPropertyAccessor) {
		List<ColumnWrapper> columns = form.getIndexView().getColumn().stream().map(c -> new ColumnWrapper(c.getAggregate(), c.isTotal())).toList();
		configureSummary(columns, natTable, sortedList, columnPropertyAccessor);
	}

	public static void configureSummary(Grid grid, NatTable natTable, SortedList<Row> sortedList, MinovaColumnPropertyAccessor columnPropertyAccessor) {
		List<ColumnWrapper> columns = grid.getField().stream().map(f -> new ColumnWrapper(f.getAggregate(), f.isTotal())).toList();
		configureSummary(columns, natTable, sortedList, columnPropertyAccessor);
	}

	public static boolean needsSummary(Grid grid) {
		return grid.getField().stream().anyMatch(f -> f.isTotal() || f.getAggregate() != null);
	}

	private static void configureSummary(List<ColumnWrapper> columns, NatTable natTable, SortedList<Row> sortedList,
			MinovaColumnPropertyAccessor columnPropertyAccessor) {
		final IDataProvider summaryDataProvider = new ListDataProvider<>(sortedList, columnPropertyAccessor);

		// add summary configuration
		natTable.addConfiguration(new AbstractRegistryConfiguration() {

			@Override
			public void configureRegistry(IConfigRegistry configRegistry) {
				int i = 0;
				for (ColumnWrapper cw : columns) {
					ISummaryProvider summaryProvider = null;

					if (cw.aggregateOption != null) {
						switch (cw.aggregateOption) {
						case AVERAGE:
							summaryProvider = new AverageSummaryProvider(summaryDataProvider);
							break;
						case COUNT:
							summaryProvider = new CountSummaryProvider(summaryDataProvider);
							break;
						case MAX:
							summaryProvider = new MaxSummaryProvider(summaryDataProvider);
							break;
						case MIN:
							summaryProvider = new MinSummaryProvider(summaryDataProvider);
							break;
						case SUM:
							summaryProvider = new SumSummaryProvider(summaryDataProvider);
							break;
						default:
							break;
						}
					}

					// Summe ("total" in .xml)
					if (cw.total) {
						summaryProvider = new SumSummaryProvider(summaryDataProvider);
					}

					if (summaryProvider != null) {
						configRegistry.registerConfigAttribute(SummaryRowConfigAttributes.SUMMARY_PROVIDER, summaryProvider, DisplayMode.NORMAL,
								SummaryRowLayer.DEFAULT_SUMMARY_COLUMN_CONFIG_LABEL_PREFIX + i);
					}

					i++;
				}
			}
		});
	}

	static class CountSummaryProvider implements ISummaryProvider {

		private IDataProvider dataProvider;

		public CountSummaryProvider(IDataProvider dataProvider) {
			this.dataProvider = dataProvider;
		}

		@Override
		public Object summarize(int columnIndex) {
			int rowCount = this.dataProvider.getRowCount();
			int valueRows = 0;
			int count = 0;

			// Überprüfen ob Boolean-Spalte -> nur TRUE Werte
			for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				Object dataValue = this.dataProvider.getDataValue(columnIndex, rowIndex);
				if (dataValue instanceof Boolean b) {
					valueRows++;
					if (Boolean.TRUE.equals(b)) {
						count++;
					}
				}
			}

			if (valueRows == 0) {
				// Keine Boolean Spalte -> Anzahl Zeilen mit Wert != null
				count = 0;
				for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
					Object dataValue = this.dataProvider.getDataValue(columnIndex, rowIndex);
					if (dataValue != null && !(dataValue instanceof String s && s.isBlank())) {
						count++;
					}
				}
			}
			return count;
		}
	}

	static class AverageSummaryProvider implements ISummaryProvider {

		private IDataProvider dataProvider;

		public AverageSummaryProvider(IDataProvider dataProvider) {
			this.dataProvider = dataProvider;
		}

		@Override
		public Object summarize(int columnIndex) {
			int rowCount = this.dataProvider.getRowCount();
			int valueRows = 0;
			double total = 0;

			for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				Object dataValue = this.dataProvider.getDataValue(columnIndex, rowIndex);
				// this check is necessary because of the GroupByObject
				if (dataValue instanceof Number n) {
					valueRows++;
					total += n.doubleValue();
				}
			}
			if (valueRows == 0) {
				return 0;
			}
			return total / valueRows;
		}
	}

	static class MinSummaryProvider implements ISummaryProvider {

		private IDataProvider dataProvider;

		public MinSummaryProvider(IDataProvider dataProvider) {
			this.dataProvider = dataProvider;
		}

		@Override
		public Object summarize(int columnIndex) {
			int rowCount = this.dataProvider.getRowCount();
			double min = Double.MAX_VALUE;
			Instant minInstant = Instant.MAX;

			for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				Object dataValue = this.dataProvider.getDataValue(columnIndex, rowIndex);
				// this check is necessary because of the GroupByObject
				if (dataValue instanceof Number n && n.doubleValue() < min) {
					min = n.doubleValue();
				}
				if (dataValue instanceof Instant i && i.isBefore(minInstant)) {
					minInstant = i;
				}
			}

			if (minInstant.equals(Instant.MAX)) {
				if (min == Double.MAX_VALUE) {
					return 0;
				}
				return min;
			}
			return minInstant;
		}
	}

	static class MaxSummaryProvider implements ISummaryProvider {

		private IDataProvider dataProvider;

		public MaxSummaryProvider(IDataProvider dataProvider) {
			this.dataProvider = dataProvider;
		}

		@Override
		public Object summarize(int columnIndex) {
			int rowCount = this.dataProvider.getRowCount();
			double max = Double.MIN_VALUE;

			Instant maxInstant = Instant.MIN;

			for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				Object dataValue = this.dataProvider.getDataValue(columnIndex, rowIndex);
				// this check is necessary because of the GroupByObject
				if (dataValue instanceof Number n && n.doubleValue() > max) {
					max = n.doubleValue();
				}
				if (dataValue instanceof Instant i && i.isAfter(maxInstant)) {
					maxInstant = i;
				}
			}

			if (maxInstant.equals(Instant.MIN)) {
				if (max == Double.MIN_VALUE) {
					return 0;
				}
				return max;
			}
			return maxInstant;
		}
	}

	static class SumSummaryProvider implements ISummaryProvider {

		private IDataProvider dataProvider;

		public SumSummaryProvider(IDataProvider dataProvider) {
			this.dataProvider = dataProvider;
		}

		@Override
		public Object summarize(int columnIndex) {
			int rowCount = this.dataProvider.getRowCount();
			double summaryValue = 0;

			for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				Object dataValue = this.dataProvider.getDataValue(columnIndex, rowIndex);

				if (dataValue instanceof Number n) {
					summaryValue += n.doubleValue();
				} else if (dataValue instanceof Boolean b && Boolean.TRUE.equals(b)) {
					summaryValue++;
				}
			}

			return summaryValue;
		}
	}

	static class ColumnWrapper {
		AggregateOption aggregateOption;
		boolean total;

		ColumnWrapper(String aggregateOption, Boolean total) {
			this.aggregateOption = aggregateOption != null ? AggregateOption.valueOf(aggregateOption) : null;
			this.total = total != null && total;
		}
	}

}
