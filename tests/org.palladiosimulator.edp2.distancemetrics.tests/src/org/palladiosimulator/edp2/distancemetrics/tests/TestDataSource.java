package org.palladiosimulator.edp2.distancemetrics.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Random;

import static org.junit.Assert.assertTrue;

import jakarta.measure.Measure;
import jakarta.measure.quantity.Dimensionless;
import jakarta.measure.quantity.Duration;
import jakarta.measure.unit.SI;
import jakarta.measure.unit.Unit;

import org.palladiosimulator.edp2.datastream.AbstractDataSource;
import org.palladiosimulator.edp2.datastream.IDataStream;
import org.palladiosimulator.edp2.datastream.configurable.PropertyConfigurable;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.measurementframework.TupleMeasurement;
import org.palladiosimulator.measurementframework.measureprovider.IMeasureProvider;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.metricspec.MetricSetDescription;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;

/**
 * This class generates test data for the JUnit Tests of the distance metrics.
 * 
 * @author Christian Schaf
 */
public class TestDataSource extends AbstractDataSource {

    private List<MeasuringValue> data = new ArrayList<>();
//    private final MeasuringPoint point;
    private static final MetricSetDescription expectedInputDataMetric
    	= MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE;
    
    /**
     * Creates an empty TestDataSource. Measurement tuples have to be added
     * manually with addTupleMeasurement(TupleMeasurement tupleMeasurement).
     */
    TestDataSource() {
    	// This constructor should be empty.
    }
    
    /**
     * Creates test data with values based on a pseudo random number generator and contains @param length elements.
     * @param randomGenInit Initialization value of the random number generator.
     * @param length Number of elements the to be generated measurement series should have.
     */
    TestDataSource(int randomGenInit, int length) {
    	assertTrue(length >= 0);
    	double pitCounter = 0.0;
    	double factor = 1000.0;
    	Random randomGenerator = new Random(randomGenInit);
    	
    	for (int i = 0; i < length; i++) {
            Measure<Double, Duration> pointInTimeMeasure = Measure.valueOf(pitCounter, SI.SECOND);
            double randomDouble = randomGenerator.nextDouble() * factor;
            Measure<Double, Dimensionless> testMeasure = Measure.valueOf(randomDouble, Unit.ONE);
            pitCounter += 1.0;
            this.data.add(new TupleMeasurement(expectedInputDataMetric, pointInTimeMeasure, testMeasure));
    	}
    }

	@Override
	public <M extends IMeasureProvider> IDataStream<M> getDataStream() {
        return new IDataStream<M>() {

            @Override
            public Iterator<M> iterator() {
                @SuppressWarnings("unchecked")
                // that cast is type safe as Measurement implements the IMeasureProvider interface
                Iterator<M> result = (Iterator<M>) TestDataSource.this.data.iterator();
                return result;
            }

            @Override
            public MetricDescription getMetricDesciption() {
                return expectedInputDataMetric;
            }

            @Override
            public boolean isCompatibleWith(MetricDescription other) {
                return getMetricDesciption().equals(other);
            }

            @Override
            public void close() {
            	TestDataSource.this.data.clear();
            }

            @Override
            public int size() {
                return TestDataSource.this.data.size();
            }

        };
	}

	@Override
	public MeasuringPoint getMeasuringPoint() {
        return null;
	}

	@Override
	protected PropertyConfigurable createProperties() {
        return new PropertyConfigurable() {

            @Override
            public Class<?> getPropertyType(String key) {
                return null;
            }

            @Override
            public Set<String> getKeys() {
                return Collections.emptySet();
            }

            @Override
            public Map<String, Object> getDefaultConfiguration() {
                return Collections.emptyMap();
            }
        };
	}
	
	/**
	 * Add a TupleMeasurement to the data series.
	 * @param tupleMeasurement You can pass your own UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE
	 * which consists of the measures of the type Measure<Double, Duration> 
	 * and Measure<Double, Dimensionless>
	 * @return true (as specified by Collection.add(E))
	 */
	public boolean addTupleMeasurement(TupleMeasurement tupleMeasurement) {
		return this.data.add(tupleMeasurement);
	}
}
