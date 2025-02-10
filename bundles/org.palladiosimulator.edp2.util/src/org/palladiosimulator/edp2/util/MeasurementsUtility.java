/**
 * 
 */
package org.palladiosimulator.edp2.util;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.measure.quantity.Dimensionless;
import jakarta.measure.quantity.Quantity;
import jakarta.measure.unit.Unit;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.edp2.dao.BinaryMeasurementsDao;
import org.palladiosimulator.edp2.dao.MeasurementsDao;
import org.palladiosimulator.edp2.dao.MeasurementsDaoFactory;
import org.palladiosimulator.edp2.dao.MeasurementsDaoRegistry;
import org.palladiosimulator.edp2.dao.exception.DataNotAccessibleException;
import org.palladiosimulator.edp2.models.ExperimentData.AggregatedMeasurements;
import org.palladiosimulator.edp2.models.ExperimentData.DataSeries;
import org.palladiosimulator.edp2.models.ExperimentData.DoubleBinaryMeasurements;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentDataFactory;
import org.palladiosimulator.edp2.models.ExperimentData.FixedWidthAggregatedMeasurements;
import org.palladiosimulator.edp2.models.ExperimentData.IdentifierBasedMeasurements;
import org.palladiosimulator.edp2.models.ExperimentData.Measurement;
import org.palladiosimulator.edp2.models.ExperimentData.MeasurementRange;
import org.palladiosimulator.edp2.models.ExperimentData.MeasuringType;
import org.palladiosimulator.edp2.models.ExperimentData.RawMeasurements;
import org.palladiosimulator.edp2.models.ExperimentData.util.ExperimentDataSwitch;
import org.palladiosimulator.edp2.models.Repository.Repository;
import org.palladiosimulator.edp2.util.visitors.DAOFromBelowRawMeasurementSwitch;
import org.palladiosimulator.edp2.util.visitors.DataSeriesFromRawMeasurementsSwitch;
import org.palladiosimulator.edp2.util.visitors.EmfmodelAddMeasurementToDataSeriesSwitch;
import org.palladiosimulator.edp2.util.visitors.EmfmodelDataSeriesFromReferenceSwitch;
import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.metricspec.Identifier;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.metricspec.MetricSetDescription;
import org.palladiosimulator.metricspec.TextualBaseMetricDescription;

/**
 * This class provides utility functions to handle measurements.
 * 
 * @author groenda, Sebastian Lehrig
 */
public class MeasurementsUtility {

    public static final String SLIDING_WINDOW_BASED_MEASUREMENT_TAG_KEY = "SLIDING_WINDOW_BASED";
    public static final Boolean SLIDING_WINDOW_BASED_MEASUREMENT_TAG_VALUE = true;

    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(MeasurementsUtility.class.getCanonicalName());

    /** EMF factory used by this instance. */
    private static final ExperimentDataFactory FACTORY = ExperimentDataFactory.eINSTANCE;

    /**
     * Creates a new MeasurementRange and contained elements if there are already existing elements
     * in another MeasurementRange. Does not set the startTime and endTime properties.
     * 
     * @param measurement
     *            Location where to add the range.
     * @return The newly created measurement range.
     */
    public static MeasurementRange addMeasurementRange(final Measurement measurement) {
        // TODO: check if measurements or setting is better.
        final MeasurementsDaoFactory daoFactory = measurement.getMeasuringType().getExperimentGroup().getRepository()
                .getMeasurementsDaoFactory();
        final MeasurementRange mr = FACTORY.createMeasurementRange(measurement);
        if (measurement.getMeasurementRanges().size() > 1) { // copy contents from existing
                                                             // templates
            final MeasurementRange template = measurement.getMeasurementRanges().get(0);
            // copy raw measurements
            if (template.getRawMeasurements() != null) {
                FACTORY.createRawMeasurements(mr);
            }
            // copy aggregated measurements
            final Iterator<AggregatedMeasurements> iter = template.getAggregatedMeasurements().iterator();
            while (iter.hasNext()) {
                final FixedWidthAggregatedMeasurements fwam = FACTORY.createFixedWidthAggregatedMeasurements();
                final FixedWidthAggregatedMeasurements fwtemplate = (FixedWidthAggregatedMeasurements) iter.next();
                fwam.setIntervals(EcoreUtil.copy(fwtemplate.getIntervals()));
                fwam.setAggregationOn(fwtemplate.getAggregationOn());
                final Iterator<DataSeries> iter2 = fwtemplate.getDataSeries().iterator();
                while (iter2.hasNext()) {
                    fwam.getDataSeries().add(
                            new EmfmodelDataSeriesFromReferenceSwitch<Quantity>(daoFactory).doSwitch(iter2.next()));
                }
            }
        }
        return mr;
        // TODO: Add parameter currentTime to allow
        // range(n-1).endtime=currentTime,range(n).starttime=currentTime
        // TODO: Create MeasurementsRange for all Measurements of an ExperimentRun -> Refactor from
        // Measurements to ExperimentRun
    }

    /**
     * Creates the DAOs for the data series of a raw measurement.
     * 
     * @param rm
     *            The raw measurements containing the data series.
     */
    public static void createDAOsForRawMeasurements(final RawMeasurements rm) {
        // input validation
        String errorMsg = "Could not create DAOs for raw measurements. A link to the DAO FACTORY was missing: ";
        if (rm.getMeasurementRange() == null) {
            errorMsg = "RawMeasurements must be assigned to a measurement range.";
        } else if (rm.getMeasurementRange().getMeasurement() == null) {
            errorMsg = "RawMeasurements must be (indirectly) assigned to a measurement.";
        } else if (rm.getMeasurementRange().getMeasurement().getMeasuringType() == null) {
            errorMsg = "RawMeasuremnts must be (indirectly) assigned to a measure (definition).";
        } else if (rm.getMeasurementRange().getMeasurement().getMeasuringType().getExperimentGroup() == null) {
            errorMsg = "RawMeasuremnts must be (indirectly) assigned to an experiment group.";
        } else if (rm.getMeasurementRange().getMeasurement().getMeasuringType().getExperimentGroup()
                .getRepository() == null) {
            errorMsg = "RawMeasuremnts must be (indirectly) assigned to an experiment group which must be assigned to a repository.";
        } else {
            errorMsg = null;
        }
        if (errorMsg != null) {
            LOGGER.log(Level.SEVERE, errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        // creation
        new DataSeriesFromRawMeasurementsSwitch(rm)
                .doSwitch(rm.getMeasurementRange().getMeasurement().getMeasuringType().getMetric());
        new DAOFromBelowRawMeasurementSwitch().doSwitch(rm);
    }

    /**
     * Stores a new measurement at the last existing range.
     * 
     * @param measuringValue
     *            The measurement of the experiment run for which a new measurement exists.
     * @param data
     *            The measurement (data) itself.
     */
    public static void storeMeasurement(final Measurement measurement, final MeasuringValue measuringValue) {
        final int size = measurement.getMeasurementRanges().size();
        if (size == 0) {
            throw new IllegalArgumentException("Measurements have to include measurements ranges");
        }

        final MeasurementRange lastRange = measurement.getMeasurementRanges().get(size - 1);
        final RawMeasurements rm = lastRange.getRawMeasurements();
        if (rm != null) { // Add raw measurements
            if (!measuringValue.getMetricDesciption().getId()
                    .equals(measurement.getMeasuringType().getMetric().getId())) {
                final String msg = "Tried to store measurement with a wrong metric. Expected: "
                        + measurement.getMeasuringType().getMetric().getName() + ", provided: "
                        + measuringValue.getMetricDesciption().getName() + ".";
                LOGGER.log(Level.SEVERE, msg);
                throw new IllegalArgumentException(msg);
            }
            final Iterator<DataSeries> iter = rm.getDataSeries().iterator();
            DataSeries ds;
            int index = -1;
            final MeasurementsDaoRegistry daoRegistry = measurement.getMeasuringType().getExperimentGroup()
                    .getRepository().getMeasurementsDaoFactory().getDaoRegistry();
            final EmfmodelAddMeasurementToDataSeriesSwitch addMmt = new EmfmodelAddMeasurementToDataSeriesSwitch(
                    daoRegistry);
            while (iter.hasNext()) {
                ds = iter.next();
                index++;
                addMmt.setMeasurementToAdd(measuringValue.asList().get(index));
                addMmt.doSwitch(ds);
                // invalidate statistics as they do not include the added value
                if (ds.getNumericalStatistics() != null) {
                    ds.setNumericalStatistics(null);
                }
                if (ds.getTextualStatistics() != null) {
                    ds.setTextualStatistics(null);
                }
            }
        }
        // TODO handle aggregated measurements
    }

    /**
     * Requests a DAO for a ordinal measurement. If the DAO does not exists it is created and opened
     * automatically (if possible).
     * 
     * @param ds
     *            The data series for which the DAO should be created.
     * @return DAO for ordinal measurements.
     */
    @SuppressWarnings("unchecked")
    public static <Q extends Quantity> MeasurementsDao<?, Q> getMeasurementsDao(final DataSeries ds) {
        final MeasurementsDaoFactory daoFactory = ds.getRawMeasurements().getMeasurementRange().getMeasurement()
                .getMeasuringType().getExperimentGroup().getRepository().getMeasurementsDaoFactory();
        final MeasurementsDao<?, Q> omd;
        if (daoFactory.getDaoRegistry().isRegistered(ds.getValuesUuid())) {
            omd = (MeasurementsDao<?, Q>) daoFactory.getDaoRegistry().getMeasurementsDao(ds.getValuesUuid());
        } else {
            omd = new ExperimentDataSwitch<MeasurementsDao<?, Q>>() {
                @Override
                public MeasurementsDao<?, Q> caseIdentifierBasedMeasurements(
                        final org.palladiosimulator.edp2.models.ExperimentData.IdentifierBasedMeasurements object) {
                    final BinaryMeasurementsDao<Identifier, Dimensionless> bmd = daoFactory
                            .createNominalMeasurementsDao(ds.getValuesUuid(),
                                    getTextualBaseMetricDescriptionFromIdentifierMeasurement(object));
                    bmd.setUnit(Unit.ONE);
                    return (MeasurementsDao<?, Q>) bmd;
                };

                @Override
                public MeasurementsDao<?, Q> caseJSXmlMeasurements(
                        final org.palladiosimulator.edp2.models.ExperimentData.JSXmlMeasurements object) {
                    return daoFactory.createJScienceXmlMeasurementsDao(ds.getValuesUuid());
                };

                @Override
                public MeasurementsDao<?, Q> caseDoubleBinaryMeasurements(final DoubleBinaryMeasurements object) {
                    final BinaryMeasurementsDao<Double, Q> bmd = daoFactory
                            .createDoubleMeasurementsDao(ds.getValuesUuid());
                    bmd.setUnit(object.getStorageUnit());
                    return bmd;
                };

                @Override
                public MeasurementsDao<?, Q> caseLongBinaryMeasurements(
                        final org.palladiosimulator.edp2.models.ExperimentData.LongBinaryMeasurements object) {
                    final BinaryMeasurementsDao<Long, Q> bmd = daoFactory.createLongMeasurementsDao(ds.getValuesUuid());
                    bmd.setUnit(object.getStorageUnit());
                    return bmd;
                };
            }.doSwitch(ds);
        }
        if (!omd.isOpen() && omd.canOpen()) {
            try {
                omd.open();
            } catch (final DataNotAccessibleException e) {
                throw new RuntimeException(e);
            }
        }
        return omd;
    }

    public static MeasuringType getMeasuringTypeFromRawMeasurements(final RawMeasurements rawMeasurements) {
        return rawMeasurements.getMeasurementRange().getMeasurement().getMeasuringType();
    }

    public static MetricDescription getMetricDescriptionFromRawMeasurements(final RawMeasurements rawMeasurements) {
        return getMeasuringTypeFromRawMeasurements(rawMeasurements).getMetric();
    }

    public static TextualBaseMetricDescription getTextualBaseMetricDescriptionFromIdentifierMeasurement(
            final IdentifierBasedMeasurements idBasedMeasurement) {
        final RawMeasurements rawMeasurements = idBasedMeasurement.getRawMeasurements();
        final int position = rawMeasurements.getDataSeries().indexOf(idBasedMeasurement);
        final MetricDescription metricDescription = getMetricDescriptionFromRawMeasurements(rawMeasurements);
        if (metricDescription instanceof MetricSetDescription) {
            final MetricSetDescription msd = (MetricSetDescription) metricDescription;
            return (TextualBaseMetricDescription) msd.getSubsumedMetrics().get(position);
        } else {
            return (TextualBaseMetricDescription) metricDescription;
        }

    }

    /**
     * Opens the data store behind the repository if necessary. Access is only allowed to opened
     * repositories. Repositories may be reopened (and the also reclosed).
     * 
     * @param repo
     *            Repository which should be opened.
     * @throws DataNotAccessibleException
     *             if access to the repository fails.
     */
    public static void ensureOpenRepository(final Repository repo) throws DataNotAccessibleException {
        /*
         * Attention: Using addRepository() of RepositoryManager already opens the DAO. open() is
         * only necessary if you don't use the convenience function or closed the repository and
         * want to reopen it.
         */
        if (!repo.isOpen()) {
            repo.open();
        }
        if (!repo.isOpen()) {
            final String msg = "Repository could not be opened.";
            LOGGER.severe(msg);
            throw new DataNotAccessibleException(msg, null);
        }
    }

    /**
     * Closes the data store behind the repository if necessary. Access is only allowed to opened
     * repositories. Repositories may be reopened (and the also reclosed).
     * 
     * @param repo
     *            Repository which should be closed.
     * @throws DataNotAccessibleException
     *             if access to the repository fails.
     */
    public static void ensureClosedRepository(final Repository repo) throws DataNotAccessibleException {
        if (repo.isOpen()) {
            repo.close();
        }
        if (repo.isOpen()) {
            final String msg = "Repository could not be closed.";
            LOGGER.severe(msg);
            throw new DataNotAccessibleException(msg, null);
        }
    }

}
