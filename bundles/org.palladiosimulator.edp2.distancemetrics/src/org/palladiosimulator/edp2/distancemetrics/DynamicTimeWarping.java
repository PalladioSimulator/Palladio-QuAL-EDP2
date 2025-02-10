package org.palladiosimulator.edp2.distancemetrics;

import jakarta.measure.quantity.Quantity;
import jakarta.measure.unit.Unit;

import org.jscience.physics.amount.Amount;
import org.palladiosimulator.edp2.datastream.IDataStream;
import org.palladiosimulator.edp2.distancemetrics.util.Utility;
import org.palladiosimulator.measurementframework.TupleMeasurement;

/**
 * This algorithm calculates the distance with the Dynamic Time Warping principle.
 * This implementation is based on the pseudo code of the windowed variant of 
 * the following wikipedia article:
 * https://en.wikipedia.org/wiki/Dynamic_time_warping
 * Note: This class doesn't use the TupleWrapper class from the Utility package
 * because it doesn't need the time values and it would so cause an overhead.
 * Besides that an array with infinity values needs to be initialized which
 * would make it neccessary to create TupleMeasurements initialized to infinity
 * but with unknown time values.
 * 
 * @author Christian Schaf
 */
public class DynamicTimeWarping implements DistanceMetric {
	
	private int window = 50;
	
	@Override
	public Amount<?> calculateDistance(IDataStream<TupleMeasurement> s1, IDataStream<TupleMeasurement> s2) {
		Utility.legalStreamComparison(s1, s2);
		final int numOfElements1 = s1.size();
        final int numOfElements2 = s2.size();
        @SuppressWarnings("unchecked")
		Amount<Quantity>[] s = (Amount<Quantity>[]) Utility.streamToArray(s1);
        @SuppressWarnings("unchecked")
		Amount<Quantity>[] t = (Amount<Quantity>[]) Utility.streamToArray(s2);
        
    	final Unit<Quantity> valueBaseUnit = Utility.getBaseValueUnit(s1);

        Utility.parametersAllowed(numOfElements1, numOfElements2);
        if (numOfElements1 == 0 || numOfElements2 == 0) {
        	return Amount.valueOf(0.0, valueBaseUnit);
        }

    	
        @SuppressWarnings("unchecked")
		Amount<Quantity>[][] dtw = new Amount[numOfElements1 + 1][numOfElements2 + 1];
        
        for (int i = 0; i <= numOfElements1; i++) {
            for (int j = 0; j < numOfElements2 + 1; j++) {
            	dtw[i][j] = Amount.valueOf(Double.POSITIVE_INFINITY, valueBaseUnit);
            }
        }
        dtw[0][0] = Amount.valueOf(0.0, valueBaseUnit);

        // Windowed DTW
        for (int i = 1; i <= numOfElements1; i++) {
        	for (int j = Math.max(1, i - window); j < Math.min(numOfElements2 + 1, i + window); j++) {
        		@SuppressWarnings("unchecked")
				Amount<Quantity> cost = (Amount<Quantity>) Utility.euclid(s[i - 1], t[j - 1]);
        		Amount<Quantity> minCost = Amount.valueOf(Math.min(dtw[i - 1][j].doubleValue(valueBaseUnit),
        				Math.min(dtw[i][j - 1].doubleValue(valueBaseUnit), dtw[i - 1][j - 1].doubleValue(valueBaseUnit))),
        				valueBaseUnit);
        		dtw[i][j] = cost.plus(minCost);
        	}
        }
        return dtw[numOfElements1][numOfElements2];        
	}
	
	/**
	 * Sets the window size which is the search space
	 * the DTW algorithm should look for shortest distance.
	 * @param window The size of the window.
	 */
	public void setWindow(int window) {
		this.window = window;
	}
}
