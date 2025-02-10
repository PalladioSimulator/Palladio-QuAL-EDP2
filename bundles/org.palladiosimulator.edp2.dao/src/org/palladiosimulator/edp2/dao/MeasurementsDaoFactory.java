/**
 * 
 */
package org.palladiosimulator.edp2.dao;

import jakarta.measure.quantity.Dimensionless;
import jakarta.measure.quantity.Quantity;
import jakarta.measure.unit.Unit;

import org.palladiosimulator.edp2.dao.jscience.JScienceXmlMeasurementsDao;
import org.palladiosimulator.metricspec.Identifier;
import org.palladiosimulator.metricspec.TextualBaseMetricDescription;

/**
 * Factory for creating DataAccessObjects (DAOs) for measurement data.
 * 
 * @author groenda
 */
public interface MeasurementsDaoFactory {
    // Creating DAOs

    /**
     * Creates a DAO to access measured data of type double.
     * 
     * @param uuid
     *            UUID of the AbstractMeasureProvider.
     * @return DAO for the measurements with the specified uuid.
     */
    public <Q extends Quantity> BinaryMeasurementsDao<Double, Q> createDoubleMeasurementsDao(String uuid);

    /**
     * Creates a DAO to access measured data of type double.
     * 
     * @param uuid
     *            UUID of the AbstractMeasureProvider.
     * @return DAO for the measurements with the specified uuid.
     * @param storageUnit
     *            Unit in which the measurements will be stored.
     */
    public <Q extends Quantity> BinaryMeasurementsDao<Double, Q> createDoubleMeasurementsDao(String uuid,
            Unit<Q> storageUnit);

    /**
     * Creates a DAO to access measured data of type long.
     * 
     * @param uuid
     *            UUID of the AbstractMeasureProvider.
     * @return DAO for the measurements with the specified uuid.
     */
    public <Q extends Quantity> BinaryMeasurementsDao<Long, Q> createLongMeasurementsDao(String uuid);

    /**
     * Creates a DAO to access measured data of type long.
     * 
     * @param uuid
     *            UUID of the AbstractMeasureProvider.
     * @param storageUnit
     *            Unit in which the measurements will be stored.
     * @return DAO for the measurements with the specified uuid.
     */
    public <Q extends Quantity> BinaryMeasurementsDao<Long, Q> createLongMeasurementsDao(String uuid,
            Unit<Q> storageUnit);

    /**
     * Creates a DAO to access measured data of type nominal measurement.
     * 
     * @param uuid
     *            UUID of the AbstractMeasureProvider.
     * @param one
     * @return DAO for the measurements with the specified uuid.
     */
    public BinaryMeasurementsDao<Identifier, Dimensionless> createNominalMeasurementsDao(String uuid,
            TextualBaseMetricDescription metric, Unit<Dimensionless> one);

    public BinaryMeasurementsDao<Identifier, Dimensionless> createNominalMeasurementsDao(String uuid,
            TextualBaseMetricDescription metric);

    /**
     * Creates a DAO to access measured data of type nominal measurement.
     * 
     * @param uuid
     *            UUID of the AbstractMeasureProvider.
     * @return DAO for the measurements with the specified uuid.
     */
    public <Q extends Quantity> JScienceXmlMeasurementsDao<?, Q> createJScienceXmlMeasurementsDao(String uuid);

    /**
     * Returns the DAO registry of this factory.
     * 
     * @return The registry in which all elements of this factory are registered.
     */
    public MeasurementsDaoRegistry getDaoRegistry();

    // Life cycle

    /**
     * Checks if this factory is active. Active means it can be used to generate the requested DAOs.
     * If the connection to the data store is closed it should become inactive.
     * 
     * @return <code>true></code> if this factory is active and all method can be used.
     */
    public boolean isActive();

    /**
     * Allows to activate the factory.
     * 
     * @param newValue
     *            <code>true</code> if the factory should be activated.
     */
    public void setActive(boolean newValue);
}
