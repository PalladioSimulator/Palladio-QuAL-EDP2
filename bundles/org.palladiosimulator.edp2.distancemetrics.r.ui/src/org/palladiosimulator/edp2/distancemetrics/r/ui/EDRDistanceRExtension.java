package org.palladiosimulator.edp2.distancemetrics.r.ui;

import jakarta.measure.quantity.Quantity;

import org.eclipse.swt.widgets.Composite;
import org.jscience.physics.amount.Amount;
import org.palladiosimulator.edp2.datastream.IDataStream;
import org.palladiosimulator.edp2.distancemetrics.r.EDRDistanceR;
import org.palladiosimulator.edp2.distancemetrics.ui.extensions.DistanceMetricExtension;
import org.palladiosimulator.edp2.distancemetrics.ui.extensions.properties.EditDistanceOnRealSequenceProperties;
import org.palladiosimulator.edp2.distancemetrics.ui.extensions.properties.PropertiesWidget;
import org.palladiosimulator.edp2.distancemetrics.ui.extensions.properties.PropertyAccess;
import org.palladiosimulator.measurementframework.TupleMeasurement;

/**
 * Class which is loaded by the extension point org.palladiosimulator.edp2.distancemetrics.ui
 * at startup. It contains all information needed to make the EDR (R) distance metric accessible.
 * 
 * @author Christian Schaf
 */
public class EDRDistanceRExtension implements DistanceMetricExtension, PropertyAccess {
	private final EDRDistanceR dm = new EDRDistanceR();

	@Override
	public String getDistanceMetricName() {
		return "EDR Distance (R)";
	}

	@Override
	public String getDistanceMetricDescription() {
		return "Computes the Edit Distance for Real Sequences between a pair of numeric time series.";
	}

	@Override
	public PropertiesWidget getPropertiesWidget(final Composite parent, final int style) {
		return new EditDistanceOnRealSequenceProperties(parent, style, dm);
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
