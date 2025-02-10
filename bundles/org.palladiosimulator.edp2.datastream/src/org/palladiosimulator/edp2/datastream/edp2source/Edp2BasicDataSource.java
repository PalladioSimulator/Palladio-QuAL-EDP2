package org.palladiosimulator.edp2.datastream.edp2source;

import jakarta.measure.quantity.Quantity;

import org.palladiosimulator.edp2.datastream.AbstractDataSource;
import org.palladiosimulator.edp2.datastream.IDataSource;
import org.palladiosimulator.edp2.datastream.IDataStream;
import org.palladiosimulator.edp2.datastream.configurable.EmptyConfiguration;
import org.palladiosimulator.edp2.datastream.configurable.PropertyConfigurable;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.measurementframework.BasicMeasurement;
import org.palladiosimulator.metricspec.BaseMetricDescription;

public class Edp2BasicDataSource<V, Q extends Quantity> extends AbstractDataSource implements IDataSource {

    public Edp2BasicDataSource(final BaseMetricDescription metricDescription) {
        super(metricDescription);
    }

    @SuppressWarnings("unchecked")
    @Override
    public IDataStream<BasicMeasurement<V, Q>> getDataStream() {
        return new Edp2BasicDataStream<V, Q>(null, (BaseMetricDescription) getMetricDesciption());
    }

    @Override
    protected PropertyConfigurable createProperties() {
        return new EmptyConfiguration();
    }

    @Override
    public MeasuringPoint getMeasuringPoint() {
        throw new UnsupportedOperationException("Not yet implemented.");
    }
}
