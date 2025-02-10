package org.palladiosimulator.edp2.distancemetrics.tests;

import static org.junit.Assert.assertEquals;

import jakarta.measure.Measure;
import jakarta.measure.quantity.Dimensionless;
import jakarta.measure.quantity.Duration;
import jakarta.measure.quantity.Quantity;
import jakarta.measure.unit.SI;
import jakarta.measure.unit.Unit;

import org.jscience.physics.amount.Amount;
import org.junit.Test;
import org.palladiosimulator.edp2.distancemetrics.EditDistanceOnRealSequence;
import org.palladiosimulator.edp2.distancemetrics.util.Utility;
import org.palladiosimulator.measurementframework.TupleMeasurement;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;

/**
 * This class contains test cases for the Edit Distance on Real Sequence distance metric.
 * 
 * @author Christian Schaf
 */
public class TestEDR {
	private final EditDistanceOnRealSequence edr = new EditDistanceOnRealSequence();
	
	/**
	 * Tests the EDR distance metric with different epsilon values. The epsilon value
	 * describes the max. distance two measurements can have to be considered as equal
	 * which means, their distance is zero.
	 * @param epsilon The epsilon value, that  EDR should use for considering two values
	 * as equal.
	 * @return Returns the calculated value given as an Amount object.
	 */
	private Amount<?> epsilonTest(double epsilon) {
		edr.setEpsilon(epsilon);
		final TestDataSource tds1 = new TestDataSource();
		final TestDataSource tds2 = new TestDataSource();
		
		Measure<Double, Duration> t1 = Measure.valueOf(0.0, SI.SECOND);
		Measure<Double, Duration> t2 = Measure.valueOf(1.0, SI.SECOND);
		Measure<Double, Duration> t3 = Measure.valueOf(2.0, SI.SECOND);
		Measure<Double, Duration> t4 = Measure.valueOf(3.0, SI.SECOND);
        
		Measure<Double, Dimensionless> m11 = Measure.valueOf(1.0, Unit.ONE);
		Measure<Double, Dimensionless> m12 = Measure.valueOf(5.0, Unit.ONE);
		Measure<Double, Dimensionless> m13 = Measure.valueOf(4.0, Unit.ONE);
		Measure<Double, Dimensionless> m14 = Measure.valueOf(2.0, Unit.ONE);
        
		tds1.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t1, m11));
		tds1.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t2, m12));
		tds1.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t3, m13));
		tds1.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t4, m14));
		
		Measure<Double, Dimensionless> m21 = Measure.valueOf(1.0, Unit.ONE);
		Measure<Double, Dimensionless> m22 = Measure.valueOf(2.0, Unit.ONE);
		Measure<Double, Dimensionless> m23 = Measure.valueOf(4.0, Unit.ONE);
		Measure<Double, Dimensionless> m24 = Measure.valueOf(1.0, Unit.ONE);
		
		tds2.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t1, m21));
		tds2.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t2, m22));
		tds2.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t3, m23));
		tds2.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t4, m24));
		
		Amount<?> result = edr.calculateDistance(tds1.getDataStream(), tds2.getDataStream());
		return result;
	}
	
	/**
	 * Instance of epsilonTest with epsilon := 0
	 */
	@Test
	public void epsilon0Test() {
		assertEquals(2.0, epsilonTest(0).getEstimatedValue(), Utility.DELTA);
	}
	
	/**
	 * Instance of epsilonTest with epsilon := 1
	 */
	@Test
	public void epsilon1Test() {
		assertEquals(1.0, epsilonTest(1).getEstimatedValue(), Utility.DELTA);
	}
	
	/**
	 * Instance of epsilonTest with epsilon := 3
	 */
	@Test
	public void epsilon3Test() {
		assertEquals(0.0, epsilonTest(3).getEstimatedValue(), Utility.DELTA);
	}

	/**
	 * Tests the EDR distance metric with different epsilon values. The epsilon value
	 * describes the max. distance two measurements can have to be considered as equal
	 * which means, their distance is zero.
	 * Additionally, the measurements have different numbers of elements and time values differ
	 * (which shouldn't be relevant to this algorithm anyway).
	 * 
	 * @param epsilon The epsilon value, that  EDR should use for considering two values
	 * as equal.
	 * @return Returns the calculated value given as an Amount object.
	 */
	private Amount<?> epsilonExtendedTest(double epsilon) {
		edr.setEpsilon(epsilon);
		final TestDataSource tds1 = new TestDataSource();
		final TestDataSource tds2 = new TestDataSource();
		
		Measure<Double, Duration> t11 = Measure.valueOf(0.0, SI.SECOND);
		Measure<Double, Duration> t12 = Measure.valueOf(1.0, SI.SECOND);
		Measure<Double, Duration> t13 = Measure.valueOf(2.0, SI.SECOND);
		Measure<Double, Duration> t14 = Measure.valueOf(3.0, SI.SECOND);
		Measure<Double, Duration> t15 = Measure.valueOf(4.0, SI.SECOND);
		Measure<Double, Duration> t16 = Measure.valueOf(5.0, SI.SECOND);
		Measure<Double, Duration> t17 = Measure.valueOf(6.0, SI.SECOND);
        
		Measure<Double, Dimensionless> m11 = Measure.valueOf(1.0, Unit.ONE);
		Measure<Double, Dimensionless> m12 = Measure.valueOf(5.0, Unit.ONE);
		Measure<Double, Dimensionless> m13 = Measure.valueOf(4.0, Unit.ONE);
		Measure<Double, Dimensionless> m14 = Measure.valueOf(2.0, Unit.ONE);
		Measure<Double, Dimensionless> m15 = Measure.valueOf(17.0, Unit.ONE);
		Measure<Double, Dimensionless> m16 = Measure.valueOf(22.0, Unit.ONE);
		Measure<Double, Dimensionless> m17 = Measure.valueOf(11.0, Unit.ONE);
        
		tds1.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t11, m11));
		tds1.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t12, m12));
		tds1.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t13, m13));
		tds1.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t14, m14));
		tds1.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t15, m15));
		tds1.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t16, m16));
		tds1.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t17, m17));
		
		Measure<Double, Duration> t21 = Measure.valueOf(1.0, SI.SECOND);
		Measure<Double, Duration> t22 = Measure.valueOf(2.5, SI.SECOND);
		Measure<Double, Duration> t23 = Measure.valueOf(3.5, SI.SECOND);
		Measure<Double, Duration> t24 = Measure.valueOf(5.0, SI.SECOND);
		
		Measure<Double, Dimensionless> m21 = Measure.valueOf(1.0, Unit.ONE);
		Measure<Double, Dimensionless> m22 = Measure.valueOf(2.0, Unit.ONE);
		Measure<Double, Dimensionless> m23 = Measure.valueOf(4.0, Unit.ONE);
		Measure<Double, Dimensionless> m24 = Measure.valueOf(1.0, Unit.ONE);
		
		tds2.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t21, m21));
		tds2.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t22, m22));
		tds2.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t23, m23));
		tds2.addTupleMeasurement(new TupleMeasurement(MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE, t24, m24));
		
		Amount<?> result = edr.calculateDistance(tds1.getDataStream(), tds2.getDataStream());
		return result;
	}
	
	/**
	 * Instance of epsilonExtendedTest with epsilon := 0
	 */
	@Test
	public void epsilonExtended0Test() {
		assertEquals(5.0, epsilonExtendedTest(0).getEstimatedValue(), Utility.DELTA);
	}
	
	/**
	 * Instance of epsilonExtendedTest with epsilon := 1
	 */
	@Test
	public void epsilonExtended1Test() {
		assertEquals(4.0, epsilonExtendedTest(1).getEstimatedValue(), Utility.DELTA);
	}
}
