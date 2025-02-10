package org.palladiosimulator.edp2.distancemetrics.r.ui;

import jakarta.measure.quantity.Quantity;

import org.jscience.physics.amount.Amount;
import org.palladiosimulator.edp2.datastream.IDataStream;
import org.palladiosimulator.edp2.distancemetrics.DistanceMetric;
import org.palladiosimulator.edp2.distancemetrics.r.EuclideanDistanceR;
import org.palladiosimulator.edp2.distancemetrics.ui.extensions.DistanceMetricExtension;
import org.palladiosimulator.measurementframework.TupleMeasurement;

/**
 * Class which is loaded by the extension point org.palladiosimulator.edp2.distancemetrics.ui
 * at startup. It contains all information needed to make the Euclidean Distance (R) distance metric accessible.
 * 
 * @author Christian Schaf
 */
public class EuclideanDistanceRExtension implements DistanceMetricExtension {

	private final EuclideanDistanceR dm = new EuclideanDistanceR();

	@Override
	public String getDistanceMetricName() {
		return "Euclidean Distance (R)";
	}

	@Override
	public String getDistanceMetricDescription() {
		return "Computes the Euclidean distance between a pair of numeric vectors."
				+ " The two series must have the SAME length.";
	}
	@Override
	public Amount<? extends Quantity> calculateDistance(final IDataStream<TupleMeasurement> s1,
			final IDataStream<TupleMeasurement> s2) {
		Amount<? extends Quantity> result = dm.calculateDistance(s1, s2);
		
		// If result is null, then an error happened!
		if (result == null) {
			ErrorHandler.handleError();
		}
		
		return result;
	}
}
