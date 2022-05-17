package aero.minova.rcp.rcp.util;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.summaryrow.ISummaryProvider;
import org.eclipse.nebula.widgets.nattable.summaryrow.SummaryRowConfigAttributes;
import org.eclipse.nebula.widgets.nattable.summaryrow.SummaryRowLayer;
import org.eclipse.nebula.widgets.nattable.summaryrow.SummationSummaryProvider;

import aero.minova.rcp.constants.AggregateOption;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.nattable.data.MinovaColumnPropertyAccessor;
import ca.odell.glazedlists.SortedList;

public class NattableSummaryUtil {

	private NattableSummaryUtil() {}

	public static void configureSummary(Form form, NatTable natTable, SortedList<Row> sortedList, MinovaColumnPropertyAccessor columnPropertyAccessor) {
		final IDataProvider summaryDataProvider = new ListDataProvider<>(sortedList, columnPropertyAccessor);

		// add summary configuration
		natTable.addConfiguration(new AbstractRegistryConfiguration() {

			@Override
			public void configureRegistry(IConfigRegistry configRegistry) {
				int i = 0;
				for (aero.minova.rcp.form.model.xsd.Column column : form.getIndexView().getColumn()) {
					ISummaryProvider summaryProvider = null;

					if (column.getAggregate() != null) {
						AggregateOption agg = AggregateOption.valueOf(column.getAggregate());
						switch (agg) {
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
							summaryProvider = new SummationSummaryProvider(summaryDataProvider, false);
							break;
						default:
							break;
						}
					}

					// Summe ("total" in .xml)
					if (column.isTotal() != null && column.isTotal()) {
						summaryProvider = new SummationSummaryProvider(summaryDataProvider, false);
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
			return this.dataProvider.getRowCount();
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
				if (dataValue instanceof Number) {
					valueRows++;
					total += ((Number) dataValue).doubleValue();
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

			for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				Object dataValue = this.dataProvider.getDataValue(columnIndex, rowIndex);
				// this check is necessary because of the GroupByObject
				if (dataValue instanceof Number && ((Number) dataValue).doubleValue() < min) {
					min = ((Number) dataValue).doubleValue();
				}
			}
			if (min == Double.MAX_VALUE) {
				return 0;
			}
			return min;
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

			for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				Object dataValue = this.dataProvider.getDataValue(columnIndex, rowIndex);
				// this check is necessary because of the GroupByObject
				if (dataValue instanceof Number && ((Number) dataValue).doubleValue() > max) {
					max = ((Number) dataValue).doubleValue();
				}
			}
			if (max == Double.MIN_VALUE) {
				return 0;
			}
			return max;
		}
	}
}
