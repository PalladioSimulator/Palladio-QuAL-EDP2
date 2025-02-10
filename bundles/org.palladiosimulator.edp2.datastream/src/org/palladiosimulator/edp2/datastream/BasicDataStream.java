package org.palladiosimulator.edp2.datastream;

import java.util.Iterator;

import jakarta.measure.quantity.Quantity;

import org.palladiosimulator.measurementframework.BasicMeasurement;
import org.palladiosimulator.metricspec.BaseMetricDescription;

public abstract class BasicDataStream<V, Q extends Quantity> extends DataStream<BasicMeasurement<V, Q>> {

    public BasicDataStream(final BaseMetricDescription metricDescription) {
        super(metricDescription);
    }

    @Override
    public abstract Iterator<BasicMeasurement<V, Q>> iterator();

}
