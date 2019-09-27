package org.palladiosimulator.edp2.distancemetrics;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;
import org.palladiosimulator.edp2.datastream.IDataStream;
import org.palladiosimulator.edp2.distancemetrics.util.Utility;
import org.palladiosimulator.measurementframework.TupleMeasurement;

/**
 * This algorithm is based on the description of the algorithm described in chapter 3.1
 * in the paper "Robust and Fast Similarity Search for Moving Object
 * Trajectories" by Lei Chen, M. Tamer Özsu, Vincent Oria
 * See: http://www.cs.ucr.edu/~ravi/CS260-ST/Oszu_SIGMOD_05.pdf
 * 
 * @author Christian Schaf
 */
public class EditDistanceOnRealSequence implements DistanceMetric {
	private final double initialEpsilon = 25;
	private final double initialSubcost = 1;
	private double epsilon = initialEpsilon;
	private double subcost = initialSubcost;


	@Override
	public Amount<?> calculateDistance(IDataStream<TupleMeasurement> s1, IDataStream<TupleMeasurement> s2) {   
		Utility.legalStreamComparison(s1, s2);

        final int numOfElements1 = s1.size();
        final int numOfElements2 = s2.size();
        Amount<?>[] s = Utility.streamToArray(s1);
        Amount<?>[] t = Utility.streamToArray(s2);
        
    	final Unit<Quantity> valueBaseUnit = Utility.getBaseValueUnit(s1);

        Utility.parametersAllowed(numOfElements1, numOfElements2);
        Utility.getBaseValueUnit(s1);
        if (numOfElements1 == 0 && numOfElements2 == 0) {
        	return Amount.valueOf(0.0, valueBaseUnit);
        } else if (numOfElements1 == 0) {
        	return Amount.valueOf((double) numOfElements2, valueBaseUnit);
        } else if (numOfElements2 == 0) {
        	return Amount.valueOf((double) numOfElements1, valueBaseUnit);
        }
        
        // This is the iterative version of EDR
        int[][] edr = new int[numOfElements1 + 1][numOfElements2 + 1];
        
        // Initialize edit distance array
        edr[0][0] = 0;
        for (int i = 1; i < numOfElements1 + 1; i++) {
        	edr[i][0] = i;
        }
        for (int j = 1; j < numOfElements2 + 1; j++) {
        	edr[0][j] = j;
        }

        // EDR
        for (int i = 1; i < numOfElements1 + 1; i++) {
        	for (int j = 1; j < numOfElements2 + 1; j++) {
        		double result = calcSubcost(s[i - 1], t[j - 1], epsilon);
        		edr[i][j] = (int) Math.min(edr[i - 1][j] + 1, Math.min(edr[i][j - 1], edr[i - 1][j - 1] + result));
        	}
        }
        return Amount.valueOf(edr[numOfElements1][numOfElements2], valueBaseUnit);

	}

	/**
	 * Calculates the subcost for two values based on the epsilon value. 
	 * @param a1 First value for comparison.
	 * @param a2 Second value for comparison.
	 * @param epsilon The epsilon value, that defines when two values are to be considered equal or not.
	 * @return The subcosts calculated between the two given values.
	 */
	private double calcSubcost(Amount<?> a1, Amount<?> a2, double epsilon) {
		if (match(a1, a2, epsilon)) {
			return 0;
		} else {
			return subcost;
		}
	}
	
	/**
	 * Helper function that checks if the difference of the given two values is
	 * smaller than the epsilon value.
	 * @param a1 First value for comparison.
	 * @param a2 Second value for comparison.
	 * @param epsilon The epsilon value, that defines when two values are to be considered equal or not.
	 * @return Returns true, if the difference is smaller or equal the epsilon value.
	 * Returns false, if the difference is bigger than the epsilon value.
	 */
	private boolean match(Amount<?> a1, Amount<?> a2, double epsilon) {
		return Math.abs(a1.getEstimatedValue() - a2.getEstimatedValue()) <= epsilon;
	}
	
	/**
	 * Sets the epsilon value, which decides, if two values
	 * are considered equal.
	 * @param e The new epsilon value.
	 */
	public void setEpsilon(double e) {
		this.epsilon = e;
	}
	
	/**
	 * Sets the subcost, the calculation should use as penalty for not equal values.
	 * @param subcost The penalty costs, if two values are not to be considered equal.
	 */
	public void setSubcost(double subcost) {
		this.subcost = subcost;
	}
	
	/**
	 * Gives you the initial epsilon value, when no epsilon value
	 * was set.
	 * @return Returns the pre-defined initial epsilon value.
	 */
	public double getInitialEpsilon() {
		return initialEpsilon;
	}
}
