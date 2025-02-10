/**
 * 
 */
package org.palladiosimulator.edp2.util.visitors;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.measure.Measure;
import jakarta.measure.quantity.Dimensionless;
import jakarta.measure.quantity.Quantity;
import jakarta.measure.unit.Unit;

import org.palladiosimulator.edp2.dao.Edp2Dao;
import org.palladiosimulator.edp2.dao.MeasurementsDao;
import org.palladiosimulator.edp2.dao.MeasurementsDaoRegistry;
import org.palladiosimulator.edp2.dao.exception.DataNotAccessibleException;
import org.palladiosimulator.edp2.models.ExperimentData.DataSeries;
import org.palladiosimulator.edp2.models.ExperimentData.DoubleBinaryMeasurements;
import org.palladiosimulator.edp2.models.ExperimentData.IdentifierBasedMeasurements;
import org.palladiosimulator.edp2.models.ExperimentData.JSXmlMeasurements;
import org.palladiosimulator.edp2.models.ExperimentData.LongBinaryMeasurements;
import org.palladiosimulator.edp2.models.ExperimentData.util.ExperimentDataSwitch;
import org.palladiosimulator.metricspec.Identifier;

/**
 * Switch class to add a provided measurement to a data series.
 * 
 * @author groenda
 */
public class EmfmodelAddMeasurementToDataSeriesSwitch extends ExperimentDataSwitch<Boolean> {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(EmfmodelAddMeasurementToDataSeriesSwitch.class
            .getCanonicalName());
    /** Registry for the DAOs. */
    private final MeasurementsDaoRegistry daoRegistry;
    /** Measure to add. */
    private Measure<?, ?> measurementToAdd;
    
    public EmfmodelAddMeasurementToDataSeriesSwitch(final MeasurementsDaoRegistry daoRegistry) {
        this.daoRegistry = daoRegistry;
    }

    private static boolean openDaoForDataSeries(Edp2Dao dao, DataSeries dataSeries) {
        if (!dao.isOpen()) {
            try {
                dao.open();
            } catch (DataNotAccessibleException e) {
                LOGGER.log(Level.SEVERE, "Could not access DAO with valuesUuid=" + dataSeries.getValuesUuid());
                return false;
            }
        }
        return true;
    }
    
    private <V, Q extends Quantity> MeasurementsDao<V, Q> getMeasurementsDaoByValuesUuid(String valuesUuid) {
        MeasurementsDao<?, ?> dao = this.daoRegistry.getMeasurementsDao(valuesUuid);
        @SuppressWarnings("unchecked")
        MeasurementsDao<V, Q> result = (MeasurementsDao<V, Q>) dao;
        return result;
    }
    
    @Override
    public Boolean caseIdentifierBasedMeasurements(final IdentifierBasedMeasurements object) {
        MeasurementsDao<Identifier, Dimensionless> dao = getMeasurementsDaoByValuesUuid(object.getValuesUuid());
        if (!openDaoForDataSeries(dao, object)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        Measure<Identifier, Dimensionless> measurement = (Measure<Identifier, Dimensionless>) this.measurementToAdd;
        dao.getMeasurements().add(measurement);
        return true;
    }

    @Override
    public Boolean caseJSXmlMeasurements(final JSXmlMeasurements object) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public <Q extends Quantity> Boolean caseDoubleBinaryMeasurements(final DoubleBinaryMeasurements<Q> object) {
        MeasurementsDao<Double, Q> dao = getMeasurementsDaoByValuesUuid(object.getValuesUuid());
        if (dao == null) {
            throw new RuntimeException("Could not find DoubleBinaryMeasurement with unit "
                  + object.getStorageUnit().toString() + " (ID " + object.getValuesUuid() + ")");
        }
        if (!openDaoForDataSeries(dao, object)) {
            return false;
        }
        Unit<Q> storageUnit = object.getStorageUnit();
        @SuppressWarnings("unchecked")
        Measure<Double, Q> measureToAdd = (Measure<Double, Q>) this.measurementToAdd;
        //convert into desired unit as defined by the corresponding metric
        if (!storageUnit.equals(measureToAdd.getUnit())) {
            measureToAdd = Measure.valueOf(measureToAdd.doubleValue(storageUnit), storageUnit);
        }
        dao.getMeasurements().add(measureToAdd);
        return true;
    }

    @Override
    public Boolean caseLongBinaryMeasurements(final LongBinaryMeasurements object) {
        MeasurementsDao<Long, Quantity> dao = getMeasurementsDaoByValuesUuid(object.getValuesUuid());
        if (!openDaoForDataSeries(dao, object)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        Unit<Quantity> storageUnit = object.getStorageUnit();
        @SuppressWarnings("unchecked")
        Measure<Long, Quantity> measureToAdd = (Measure<Long, Quantity>) this.measurementToAdd;
        //convert into desired unit as defined by the corresponding metric
        if (!storageUnit.equals(measureToAdd.getUnit())) {
            measureToAdd = Measure.valueOf(measureToAdd.longValue(storageUnit), storageUnit);
        }
        dao.getMeasurements().add(measureToAdd);
        return true;
    }

    /**
     * @return the measurementToAdd
     */
    public Object getMeasurementToAdd() {
        return this.measurementToAdd;
    }

    /**
     * @param measurementToAdd
     *            the measurementToAdd to set
     */
    public void setMeasurementToAdd(final Measure<?, ?> measurementToAdd) {
        this.measurementToAdd = measurementToAdd;
    }

}
