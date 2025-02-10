package org.palladiosimulator.edp2.distancemetrics.util;


import jakarta.measure.Measure;
import jakarta.measure.quantity.Quantity;
import jakarta.measure.unit.Unit;

import org.palladiosimulator.measurementframework.TupleMeasurement;

/**
 * This class is a wrapper for a tuple measurement which contains the point in time of the measured
 * value and the measured value it self. To increase readability of code this class offers
 * methods to access the values of the tuple.
 * 
 * @author Christian Schaf
 *
 */
public class TupleWrapper {

	private TupleMeasurement tuple;
	
	/**
	 * Creates a wrapper object of a TupleMeasurement.
	 * @param tuple The TupleMeasurement that should be wrapped.
	 */
	public TupleWrapper(final TupleMeasurement tuple) {
		this.tuple = tuple;
	}
	
	/**
	 * Gives back the point in time of the measurement.
	 * @param unit The returned value will be stated in this unit.
	 * @return Returns the time value of the TupleMeasurement.
	 */
	public double getTime(final Unit<Quantity> unit) {
		@SuppressWarnings("unchecked")
		Measure<Double, Quantity>[] measurement = (Measure<Double, Quantity>[]) tuple.asArray();
		return measurement[0].doubleValue(unit);
	}
	
	/**
	 * Returns the measured value of the TupleMeasurement.
	 * @param unit The returned value will be stated in this unit.
	 * @return Returns the measured value at a specified point in time of the TupleMeasurement.
	 */
	public double getValue(final Unit<Quantity> unit) {
		
		@SuppressWarnings("unchecked")
		Measure<Double, Quantity>[] measurement = (Measure<Double, Quantity>[]) tuple.asArray();
		return measurement[1].doubleValue(unit);
	}
}
