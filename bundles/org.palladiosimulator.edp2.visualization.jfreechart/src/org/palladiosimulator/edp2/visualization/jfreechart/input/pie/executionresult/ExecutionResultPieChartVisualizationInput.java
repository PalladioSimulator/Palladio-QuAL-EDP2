package org.palladiosimulator.edp2.visualization.jfreechart.input.pie.executionresult;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.mutable.MutableInt;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.palladiosimulator.edp2.datastream.IDataSource;
import org.palladiosimulator.edp2.datastream.IDataStream;
import org.palladiosimulator.edp2.util.MetricDescriptionUtility;
import org.palladiosimulator.edp2.visualization.jfreechart.input.pie.PieChartVisualizationInput;
import org.palladiosimulator.measurementframework.TupleMeasurement;
import org.palladiosimulator.measurementframework.measure.IdentifierMeasure;
import org.palladiosimulator.metricspec.BaseMetricDescription;
import org.palladiosimulator.metricspec.Scale;

public class ExecutionResultPieChartVisualizationInput extends PieChartVisualizationInput {


    /* (non-Javadoc)
     * @see org.palladiosimulator.edp2.visualization.jfreechart.input.pie.PieChartVisualizationInput#generateDataset()
     */
    @Override
    protected AbstractDataset generateDataset() {
        final DefaultPieDataset dataset = new DefaultPieDataset();
        final Map<Comparable<?>, MutableInt> bins = new HashMap<Comparable<?>, MutableInt>();
        final IDataSource datasource = getInputs().get(0).getDataSource();
        final IDataStream<TupleMeasurement> datastream = datasource.getDataStream();

        for (final TupleMeasurement tuple : datastream) {
            final Comparable<?> executionResult = tuple.asList().get(0);
            if (!bins.containsKey(executionResult)) {
                bins.put(executionResult, new MutableInt(0));
            }
            bins.get(executionResult).add(1);
        }

        for (final Comparable<?> o : bins.keySet()) {
            dataset.setValue(((IdentifierMeasure<?>)o).getValue().getLiteral(), bins.get(o).intValue());
        }

        return dataset;
    }

    /* (non-Javadoc)
     * @see org.palladiosimulator.edp2.visualization.jfreechart.input.pie.PieChartVisualizationInput#canAccept(org.palladiosimulator.edp2.datastream.IDataSource)
     */
    @Override
    public boolean canAccept(final IDataSource dataSource) {
        final BaseMetricDescription[] subMetricDescriptions = MetricDescriptionUtility
                .toBaseMetricDescriptions(dataSource.getMetricDesciption());
        if (subMetricDescriptions.length != 2) {
            return false; // two-dimensional data needed
        }

        if (!subMetricDescriptions[0].getName().equals("Execution Result")) {
            return false;
        }

        return subMetricDescriptions[0].getScale() == Scale.NOMINAL;
    }

    /* (non-Javadoc)
     * @see org.palladiosimulator.edp2.visualization.jfreechart.input.JFreeChartVisualizationInput#getName()
     */
    @Override
    public String getName() {
        return "Execution Result occurances";
    }

}
