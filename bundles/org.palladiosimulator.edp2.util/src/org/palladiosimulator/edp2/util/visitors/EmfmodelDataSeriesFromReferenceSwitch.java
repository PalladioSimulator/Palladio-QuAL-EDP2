package org.palladiosimulator.edp2.util.visitors;

import jakarta.measure.quantity.Quantity;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.edp2.dao.BinaryMeasurementsDao;
import org.palladiosimulator.edp2.dao.MeasurementsDaoFactory;
import org.palladiosimulator.edp2.models.ExperimentData.DataSeries;
import org.palladiosimulator.edp2.models.ExperimentData.DoubleBinaryMeasurements;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentDataFactory;
import org.palladiosimulator.edp2.models.ExperimentData.IdentifierBasedMeasurements;
import org.palladiosimulator.edp2.models.ExperimentData.JSXmlMeasurements;
import org.palladiosimulator.edp2.models.ExperimentData.LongBinaryMeasurements;
import org.palladiosimulator.edp2.models.ExperimentData.util.ExperimentDataSwitch;
import org.palladiosimulator.edp2.util.MeasurementsUtility;

public class EmfmodelDataSeriesFromReferenceSwitch<Q extends Quantity> extends ExperimentDataSwitch<DataSeries> {

    /** Factory for Emfmodel. */
    private static final ExperimentDataFactory experimentDatafactory = ExperimentDataFactory.eINSTANCE;

    /** Factory which is used to create the DAOs to access data of the DataSeries. */
    private final MeasurementsDaoFactory daoFactory;

    /** String which contains the values uuid for the data series. */
    private final String valuesId = EcoreUtil.generateUUID();

    public EmfmodelDataSeriesFromReferenceSwitch(final MeasurementsDaoFactory daoFactory) {
        super();

        this.daoFactory = daoFactory;
    }

    @Override
    public DataSeries caseIdentifierBasedMeasurements(final IdentifierBasedMeasurements object) {
        final IdentifierBasedMeasurements ibm = experimentDatafactory.createIdentifierBasedMeasurements();
        daoFactory.createNominalMeasurementsDao(valuesId,
                MeasurementsUtility.getTextualBaseMetricDescriptionFromIdentifierMeasurement(object));
        ibm.setValuesUuid(valuesId);
        return ibm;
    }

    @Override
    public DataSeries caseJSXmlMeasurements(final JSXmlMeasurements object) {
        final JSXmlMeasurements jsxml = experimentDatafactory.createJSXmlMeasurements();
        daoFactory.createJScienceXmlMeasurementsDao(valuesId);
        jsxml.setValuesUuid(valuesId);
        return jsxml;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataSeries caseDoubleBinaryMeasurements(final DoubleBinaryMeasurements object) {
        final DoubleBinaryMeasurements dbm = experimentDatafactory.createDoubleBinaryMeasurements();
        dbm.setValuesUuid(valuesId);
        dbm.setStorageUnit(object.getStorageUnit());
        final BinaryMeasurementsDao<Double, Q> bmdao = daoFactory.<Q> createDoubleMeasurementsDao(valuesId);
        bmdao.setUnit(dbm.getStorageUnit());
        return dbm;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataSeries caseLongBinaryMeasurements(final LongBinaryMeasurements object) {
        final LongBinaryMeasurements lbm = experimentDatafactory.createLongBinaryMeasurements();
        lbm.setValuesUuid(valuesId);
        lbm.setStorageUnit(object.getStorageUnit());
        final BinaryMeasurementsDao<Long, Q> bmdao = daoFactory.<Q> createLongMeasurementsDao(valuesId);
        bmdao.setUnit(lbm.getStorageUnit());
        return lbm;
    }

}
