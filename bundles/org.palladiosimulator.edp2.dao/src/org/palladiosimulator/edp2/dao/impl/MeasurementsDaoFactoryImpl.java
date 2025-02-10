package org.palladiosimulator.edp2.dao.impl;

import jakarta.measure.quantity.Dimensionless;
import jakarta.measure.quantity.Quantity;

import org.palladiosimulator.edp2.dao.BinaryMeasurementsDao;
import org.palladiosimulator.edp2.dao.MeasurementsDaoFactory;
import org.palladiosimulator.edp2.dao.jscience.JScienceXmlMeasurementsDao;
import org.palladiosimulator.metricspec.Identifier;
import org.palladiosimulator.metricspec.TextualBaseMetricDescription;

/**
 * @author groenda
 *
 */
public abstract class MeasurementsDaoFactoryImpl implements MeasurementsDaoFactory {

    /** Determines if this DAO is active (connection to data store available) or not. */
    private boolean active = true;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.palladiosimulator.edp2.IMeasurementsDaoFactory#createDoubleMeasurementsDao(java.lang.
     * String)
     */
    @Override
    public <Q extends Quantity> BinaryMeasurementsDao<Double, Q> createDoubleMeasurementsDao(final String uuid) {
        if (!active) {
            throw new IllegalStateException("Must be in active state to create DAOs.");
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.palladiosimulator.edp2.IMeasurementsDaoFactory#createJScienceXmlMeasurementsDao(java.
     * lang.String)
     */
    @Override
    public <Q extends Quantity> JScienceXmlMeasurementsDao<?, Q> createJScienceXmlMeasurementsDao(final String uuid) {
        if (!active) {
            throw new IllegalStateException("Must be in active state to create DAOs.");
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.palladiosimulator.edp2.IMeasurementsDaoFactory#createLongMeasurementsDao(java.lang.String
     * )
     */
    @Override
    public <Q extends Quantity> BinaryMeasurementsDao<Long, Q> createLongMeasurementsDao(final String uuid) {
        if (!active) {
            throw new IllegalStateException("Must be in active state to create DAOs.");
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.palladiosimulator.edp2.IMeasurementsDaoFactory#createNominalMeasurementsDao(java.lang
     * .String)
     */
    @Override
    public BinaryMeasurementsDao<Identifier, Dimensionless> createNominalMeasurementsDao(final String uuid,
            final TextualBaseMetricDescription metric) {
        if (!active) {
            throw new IllegalStateException("Must be in active state to create DAOs.");
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.palladiosimulator.edp2.IMeasurementsDaoFactory#isActive()
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.palladiosimulator.edp2.IMeasurementsDaoFactory#setActive(boolean)
     */
    @Override
    public void setActive(final boolean newValue) {
        if ((active && newValue) || (!active && !newValue)) {
            throw new IllegalStateException("Can not change state to current state.");
        }
        active = newValue;
    }

}
