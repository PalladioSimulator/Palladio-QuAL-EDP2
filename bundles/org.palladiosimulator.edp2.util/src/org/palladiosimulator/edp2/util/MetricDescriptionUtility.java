/**
 * 
 */
package org.palladiosimulator.edp2.util;

import jakarta.measure.Measure;
import jakarta.measure.quantity.Quantity;
import jakarta.measure.unit.Unit;

import org.palladiosimulator.edp2.util.visitors.BaseMetricFromMetricSwitch;
import org.palladiosimulator.metricspec.BaseMetricDescription;
import org.palladiosimulator.metricspec.CaptureType;
import org.palladiosimulator.metricspec.Identifier;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.metricspec.NumericalBaseMetricDescription;
import org.palladiosimulator.metricspec.TextualBaseMetricDescription;

/**
 * Utility class to handle MetricDescription(s).
 * 
 * @author groenda, partially extended by Florian Rosenthal
 */
public final class MetricDescriptionUtility {

    private MetricDescriptionUtility() {
        
    }
    
    /**
     * Gets whether the given {@link MetricDescription}s can be considered equal with respect to their ids.
     * @param first A {@link MetricDescription} instance.
     * @param second A {@link MetricDescription} instance.
     * @return {@code true} whether the ids of the given metric descriptions are equal
     * (i.e., {@code first.getId().equals(second.getId())} yields true), {@code false} otherwise.
     * @throws IllegalArgumentException In case either argument is {@code null}. 
     */
    public static boolean metricDescriptionIdsEqual(MetricDescription first, MetricDescription second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Either of the given instances is null.");
        }
        return first.getId().equals(second.getId());
    }
    
    /**
     * Convenience method to get the default unit of a numerical metric description in a typesafe way.
     * @param baseMetric The {@link NumericalBaseMetricDescription} whose default unit is to be retrieved.
     * @param quantityDimension A {@link Class} object denoting the expected {@link Quantity} of the default unit.
     * @return The default {@link Unit} of the given metric description as defined in the meta-model,
     * expressed in the desired quantity.
     * @throws ClassCastException If the dimension of the unit is different from the specified quantity dimension.<br>
     * For instance, if {@code Power.class} is passed and the actual dimension is {@link Duration}, this exception is thrown.
     * @see Unit#asType(Class)
     * @see NumericalBaseMetricDescription#getDefaultUnit()
     */
    public static <T extends Quantity> Unit<T> getDefaultUnit(NumericalBaseMetricDescription baseMetric,
            Class<T> quantityDimension) {
        if (baseMetric == null || quantityDimension == null) {
            throw new IllegalAccessError("Both arguments must not be null.");
        }
        return baseMetric.getDefaultUnit().asType(quantityDimension);
    }
    
    /**
     * Indicates whether a particular base metric description is specified/subsumed by a metric description.
     * @param soughtFor The {@link BaseMetricDescription} which is looked for.
     * @param description An arbitrary {@link MetricDescription} that might subsume more than one base metric descriptions.
     * @return {@code true} whether the given base metric description is subsumed, {@code false} otherwise.
     * @see #toBaseMetricDescriptions(MetricDescription)
     */
    public static boolean isBaseMetricDescriptionSubsumedByMetricDescription(BaseMetricDescription soughtFor, MetricDescription description) {
        for (BaseMetricDescription baseMetric : toBaseMetricDescriptions(description)) {
            if (metricDescriptionIdsEqual(soughtFor, baseMetric)) {
                return true;
            }
        }
        return false;
    }
    
   
    /**
     * Returns all base metric descriptions in the order they are specified in a metric description.
     * 
     * @param description
     *            The description which is processed.
     * @return Ordered array of base metrics.
     */
    public static BaseMetricDescription[] toBaseMetricDescriptions(final MetricDescription description) {
        return new BaseMetricFromMetricSwitch().doSwitch(description);
    }

    /**
     * Checks if a provided object is valid according to the specified metric.
     * 
     * @param description
     *            The description to use for the validation.
     * @param value
     *            The value to check.
     * @return <code>true</code> if the object has a valid type, <code>false</code> otherwise.
     */
    public static boolean isValidValue(final BaseMetricDescription description, final Object value) {
        if (description.getCaptureType() == CaptureType.IDENTIFIER) {
            if (value instanceof Measure && ((Measure) value).getValue() instanceof Identifier) {
                boolean isDefinedAtMetric = false;
                for (final Identifier ident : ((TextualBaseMetricDescription) description).getIdentifiers()) {
                    if (ident.getId().equals(((Measure<Identifier, ?>) value).getValue().getId())) {
                        isDefinedAtMetric = true;
                        break;
                    }
                }
                if (!isDefinedAtMetric) {
                    throw new IllegalArgumentException("The provided identifier " + value
                            + " does not exists for the metric " + description + ".");
                }
                return true;
            } else {
                return false;
            }
        } else { // CaptureType.{INTEGER_NUMBER|REAL_NUMBER}
            if (value instanceof Measure) {
                return true;
            } else {
                return false;
            }
        }
    }
}
