package org.palladiosimulator.edp2.datastream.edp2source;

import org.palladiosimulator.edp2.datastream.AbstractDataSource;
import org.palladiosimulator.edp2.datastream.IDataSource;
import org.palladiosimulator.edp2.datastream.IDataStream;
import org.palladiosimulator.edp2.datastream.configurable.EmptyConfiguration;
import org.palladiosimulator.edp2.datastream.configurable.PropertyConfigurable;
import org.palladiosimulator.edp2.models.ExperimentData.RawMeasurements;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.edp2.util.MeasurementsUtility;
import org.palladiosimulator.measurementframework.measureprovider.IMeasureProvider;
import org.palladiosimulator.metricspec.MetricSetDescription;

public class Edp2DataTupleDataSource extends AbstractDataSource implements IDataSource {

    private final RawMeasurements rawMeasurements;

    public Edp2DataTupleDataSource(final RawMeasurements measurements) {
        super(MeasurementsUtility.getMetricDescriptionFromRawMeasurements(measurements));
        rawMeasurements = measurements;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <M extends IMeasureProvider> IDataStream<M> getDataStream() {
        return (IDataStream<M>) new Edp2DataTupleStreamForRawMeasurements(rawMeasurements,
                (MetricSetDescription) this.getMetricDesciption());
    }

    public RawMeasurements getRawMeasurements() {
        return rawMeasurements;
    }

    @Override
    protected PropertyConfigurable createProperties() {
        return new EmptyConfiguration();
    }

    @Override
    public MeasuringPoint getMeasuringPoint() {
        return MeasurementsUtility.getMeasuringTypeFromRawMeasurements(this.rawMeasurements)
            .getMeasuringPoint();
    }
}
