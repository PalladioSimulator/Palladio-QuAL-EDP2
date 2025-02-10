package org.palladiosimulator.edp2.distancemetrics;

import jakarta.measure.quantity.Quantity;

import org.jscience.physics.amount.Amount;
import org.palladiosimulator.edp2.datastream.IDataStream;
import org.palladiosimulator.measurementframework.TupleMeasurement;
/**
 * This is the interface, that every distance metric has to implement in order to be integrated in the UI.
 * 
 * @author Christian Schaf
 */
public interface DistanceMetric {

	/**
	 * This method contains the distance metric algorithm.
	 * @param s1 First stream of measurement to compare.
	 * @param s2 Second stream of measurement to compare.
	 * @return Return a generic Amount which can contain any Quantity. Returns null, 
	 * if calcuation couldn't be executed.
	 */
	public Amount<? extends Quantity> calculateDistance(final IDataStream<TupleMeasurement> s1, 
	        final IDataStream<TupleMeasurement> s2);
}
