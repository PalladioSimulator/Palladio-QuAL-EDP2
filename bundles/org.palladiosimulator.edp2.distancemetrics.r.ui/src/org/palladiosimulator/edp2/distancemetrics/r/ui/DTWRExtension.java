package org.palladiosimulator.edp2.distancemetrics.r.ui;

import jakarta.measure.quantity.Quantity;

import org.jscience.physics.amount.Amount;
import org.palladiosimulator.edp2.datastream.IDataStream;
import org.palladiosimulator.edp2.distancemetrics.r.DTWR;
import org.palladiosimulator.edp2.distancemetrics.ui.extensions.DistanceMetricExtension;
import org.palladiosimulator.measurementframework.TupleMeasurement;

/**
 * Class which is loaded by the extension point org.palladiosimulator.edp2.distancemetrics.ui
 * at startup. It contains all information needed to make the DTW (R) distance metric accessible.
 * 
 * @author Christian Schaf
 */
public class DTWRExtension implements DistanceMetricExtension {

	private final DTWR dm = new DTWR();

	@Override
	public String getDistanceMetricName() {
		return "Dynamic Time Warping (R)";
	}

	@Override
	public String getDistanceMetricDescription() {
		return "Computes the Dynamic Time Warping distance between a pair of numeric time series.";
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
