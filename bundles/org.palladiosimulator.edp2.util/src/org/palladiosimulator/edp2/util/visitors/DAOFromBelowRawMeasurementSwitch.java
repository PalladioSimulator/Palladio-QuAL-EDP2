package org.palladiosimulator.edp2.util.visitors;

import jakarta.measure.unit.Unit;

import org.palladiosimulator.edp2.dao.MeasurementsDaoFactory;
import org.palladiosimulator.edp2.models.ExperimentData.DataSeries;
import org.palladiosimulator.edp2.models.ExperimentData.DoubleBinaryMeasurements;
import org.palladiosimulator.edp2.models.ExperimentData.IdentifierBasedMeasurements;
import org.palladiosimulator.edp2.models.ExperimentData.JSXmlMeasurements;
import org.palladiosimulator.edp2.models.ExperimentData.LongBinaryMeasurements;
import org.palladiosimulator.edp2.models.ExperimentData.RawMeasurements;
import org.palladiosimulator.edp2.models.ExperimentData.util.ExperimentDataSwitch;
import org.palladiosimulator.edp2.util.MeasurementsUtility;

/**
 * EMF switch to create DAOs based on a raw measurement or a lower element (e.g. data series).
 * 
 * @author groenda
 */
public class DAOFromBelowRawMeasurementSwitch extends ExperimentDataSwitch<Boolean> {
    @Override
    public Boolean caseRawMeasurements(final RawMeasurements object) {
        boolean success = true;
        for (final DataSeries dataSeries : object.getDataSeries()) {
            success &= doSwitch(dataSeries);
        }
        return success;
    }

    @Override
    public Boolean caseIdentifierBasedMeasurements(final IdentifierBasedMeasurements object) {
        final MeasurementsDaoFactory daoFactory = getMeasurementsDaoFactoryFromMeasurements(object);
        return null != daoFactory.createNominalMeasurementsDao(object.getValuesUuid(),
                MeasurementsUtility.getTextualBaseMetricDescriptionFromIdentifierMeasurement(object), Unit.ONE /*
                                                                                                                * TODO
                                                                                                                * object
                                                                                                                * .
                                                                                                                * getStorageUnit
                                                                                                                * (
                                                                                                                * )
                                                                                                                */);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Boolean caseLongBinaryMeasurements(final LongBinaryMeasurements object) {
        final MeasurementsDaoFactory daoFactory = getMeasurementsDaoFactoryFromMeasurements(object);
        return null != daoFactory.createLongMeasurementsDao(object.getValuesUuid(), object.getStorageUnit());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Boolean caseDoubleBinaryMeasurements(final DoubleBinaryMeasurements object) {
        final MeasurementsDaoFactory daoFactory = getMeasurementsDaoFactoryFromMeasurements(object);
        return null != daoFactory.createDoubleMeasurementsDao(object.getValuesUuid(), object.getStorageUnit());
    }

    @Override
    public Boolean caseJSXmlMeasurements(final JSXmlMeasurements object) {
        final MeasurementsDaoFactory daoFactory = getMeasurementsDaoFactoryFromMeasurements(object);
        return null != daoFactory.createJScienceXmlMeasurementsDao(object.getValuesUuid());
    }

    /**
     * @param dataSeries
     * @return
     */
    protected MeasurementsDaoFactory getMeasurementsDaoFactoryFromMeasurements(final DataSeries dataSeries) {
        return dataSeries.getRawMeasurements().getMeasurementRange().getMeasurement().getMeasuringType()
                .getExperimentGroup().getRepository().getMeasurementsDaoFactory();
    }
}
