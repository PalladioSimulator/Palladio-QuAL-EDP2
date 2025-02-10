package org.palladiosimulator.edp2.distancemetrics.r;

import java.util.Vector;

import jakarta.measure.quantity.Quantity;
import jakarta.measure.unit.Unit;

import org.apache.log4j.BasicConfigurator;
import org.jscience.physics.amount.Amount;
import org.palladiosimulator.edp2.datastream.IDataStream;
import org.palladiosimulator.edp2.distancemetrics.EditDistanceOnRealSequence;
import org.palladiosimulator.edp2.distancemetrics.util.Utility;
import org.palladiosimulator.measurementframework.TupleMeasurement;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;

/**
 * This class extends EditDistanceOnRealSequence instead of implementing the DistanceMetric interface
 * only. This is useful, so in the UI both EDR's (R and java version) can rely on the same
 * property widget class EditDistanceOnRealSequenceProperties.
 * 
 * @author Christian Schaf
 *
 */
public class EDRDistanceR extends EditDistanceOnRealSequence {
	private double[] epsilon = {0.0};
	@Override
	public Amount<?> calculateDistance(IDataStream<TupleMeasurement> s1, IDataStream<TupleMeasurement> s2) {
	// Configures the log4j Logger, so no warning are given and no external file is needed.
	BasicConfigurator.configure();
	RRegressionConnection rcon = DistanceMetricsConnectionImpl.getRRegressionConnection();
	if (rcon == null) {
		ErrorState.setState(ErrorState.CONNECTION_ERROR);
		return null;
	} 
	rcon.checkPackageAvailability("TSdist");
	double[] s1Array = Utility.streamToDoubleArray(s1);
	double[] s2Array = Utility.streamToDoubleArray(s2);
	Unit<Quantity> baseValueUnit = Utility.getBaseValueUnit(s1);
    String xName = "x";
    String yName = "y";
    String epsName = "eps";
    rcon.assign(xName, s1Array);
    rcon.assign(yName, s2Array);
    rcon.assign(epsName, epsilon);
    // [1] is used in the String, since the connection only allows assiging arrays and not single values.
    Vector<REXP> dtw = rcon.execute("EDRDistance(" + xName + ", " + yName + ", " + epsName + "[1])");
    Amount<?> amnt = Amount.valueOf(0, baseValueUnit);
		try {
		amnt = Amount.valueOf(dtw.firstElement().asDouble(), baseValueUnit);
	} catch (REXPMismatchException e) {
		rcon.close();
		e.printStackTrace();
	}
		rcon.close();
		return amnt;
}
	/**
	 * Sets the epsilon value, which decides when two values
	 * are considered equal.
	 * @param e The epsilon value, at which two values are considered to be equal.
	 */
	@Override
	public void setEpsilon(final double e) {
		this.epsilon[0]  = e;
	}

}
