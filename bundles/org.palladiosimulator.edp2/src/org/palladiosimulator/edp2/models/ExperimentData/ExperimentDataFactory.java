package org.palladiosimulator.edp2.models.ExperimentData;

import javax.measure.unit.Unit;

import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.metricspec.MetricDescription;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each
 * non-abstract class of the model. <!-- end-user-doc -->
 *
 * @see org.palladiosimulator.edp2.models.ExperimentData.ExperimentDataPackage
 */
public interface ExperimentDataFactory extends ExperimentDataFactoryGen {

     /**
     * Factory method to create a new number-based measure with the given arguments.
     *
     * @param persistenceOptions
     *            Persistence kind options of the measure.
     * @param measuringPoint
     *            Name of the measured object.
     * @param metricDescription
     *            Metric description used by this measure.
     * @return New number-based measure.
     */
    default public MeasuringType createMeasuringType(MeasuringPoint measuringPoint, MetricDescription metricDescription) {
    	final MeasuringType measure = this.createMeasuringType();
        measure.setMeasuringPoint(measuringPoint);
        measure.setMetric(metricDescription);
        return measure;
    }

    /**
     * Factory method to create a new experiment setting with the given arguments.
     *
     * @param experimentGroup
     *            The experiment group which the new setting is assigned to.
     * @param description
     *            Description of the setting.
     * @return New experiment setting.
     */
    default ExperimentSetting createExperimentSetting(ExperimentGroup experimentGroup, String description) {
    	final ExperimentSetting es = this.createExperimentSetting();
        experimentGroup.getExperimentSettings().add(es);
        es.setDescription(description);
        return es;
    }

    /**
     * Factory method to create a new experiment group with the given arguments.
     *
     * @param purpose
     *            The purpose of the group.
     * @return New experiment group.
     */
    default ExperimentGroup createExperimentGroup(String purpose) {
        final ExperimentGroup eg = this.createExperimentGroup();
        eg.setPurpose(purpose);
        return eg;
    }

    /**
     * Factory method to create a new measurement with the given arguments.
     *
     * @param forMeasuringType
     *            The measure for which the measurement should be created.
     * @return New measurement.
     */
    default Measurement createMeasurement(MeasuringType forMeasuringType) {
        final Measurement mms = this.createMeasurement();
        mms.setMeasuringType(forMeasuringType);
        return mms;
    }

    /**
     * Factory method to create a new experiment run with the given arguments.
     *
     * @param forExperimentSetting
     *            The setting for which the run should be created.
     * @return New experiment run.
     */
    default ExperimentRun createExperimentRun(ExperimentSetting forExperimentSetting) {
    	final ExperimentRun er = this.createExperimentRun();
        forExperimentSetting.getExperimentRuns().add(er);
        return er;
    }

    /**
     * Factory method to create a new raw measurements with the given arguments. The corresponding
     * data series are automatically created from the measure definitions.
     *
     * @param forMeasurementRange
     *            The measurement range for which the run should be created.
     * @return New raw measurements.
     */
    default RawMeasurements createRawMeasurements(MeasurementRange forMeasurementsRange) {
        final RawMeasurements rm = this.createRawMeasurements();
        forMeasurementsRange.setRawMeasurements(rm);
        return rm;
    }

    /**
     * Factory method to create a new measurements range with the given arguments.
     *
     * @param forMeasurement
     *            The measurements for which the range should be created.
     * @return New measurements range.
     */
    default MeasurementRange createMeasurementRange(Measurement forMeasurement) {
        final MeasurementRange mr = this.createMeasurementRange();
        mr.setMeasurement(forMeasurement);
        return mr;
    }

    /**
     * Factory method to create a new identifier-based measurements with the given arguments.
     *
     * @param forRawMeasurement
     *            The raw measurement for which the measurements should be created.
     * @param valesUuid
     *            The UUID for the values.
     * @return New identifier-based measurements.
     */
    default IdentifierBasedMeasurements createIdentifierBasedMeasurements(RawMeasurements forRawMeasurements,
            String valuesUuid) {
        final IdentifierBasedMeasurements ibm = this.createIdentifierBasedMeasurements();
        ibm.setValuesUuid(valuesUuid);
        forRawMeasurements.getDataSeries().add(ibm);
        return ibm;
    }

    /**
     * Factory method to create a new long binary measurements with the given arguments.
     *
     * @param forRawMeasurement
     *            The raw measurement for which the measurements should be created.
     * @param valuesUuid
     *            The UUID for the values.
     * @param defaultUnit
     *            The default unit of the values.
     * @return New long binary measurements.
     */
    default LongBinaryMeasurements createLongBinaryMeasurements(RawMeasurements forRawMeasurements, String valuesUuid,
            Unit defaultUnit) {
        final LongBinaryMeasurements lbm = this.createLongBinaryMeasurements();
        lbm.setValuesUuid(valuesUuid);
        lbm.setStorageUnit(defaultUnit);
        forRawMeasurements.getDataSeries().add(lbm);
        return lbm;
    }

    /**
     * Factory method to create a new double binary measurements with the given arguments.
     *
     * @param forRawMeasurement
     *            The raw measurement for which the measurements should be created.
     * @param valuesUuid
     *            The UUID for the values.
     * @param defaultUnit
     *            The default unit of the values.
     * @return New double binary measurements.
     */
    default DoubleBinaryMeasurements createDoubleBinaryMeasurements(RawMeasurements forRawMeasurements,
            String valuesUuid, Unit defaultUnit) {
        final DoubleBinaryMeasurements dbm = this.createDoubleBinaryMeasurements();
        dbm.setValuesUuid(valuesUuid);
        dbm.setStorageUnit(defaultUnit);
        forRawMeasurements.getDataSeries().add(dbm);
        return dbm;
    }

    /**
     * Factory method to create a new JScience XML measurements with the given arguments.
     *
     * @param forRawMeasurements
     *            The raw measurement for which the measurements should be created.
     * @param valuesUuid
     *            The UUID for the values.
     * @return New JScience XML measurements.
     */
    default JSXmlMeasurements createJSXmlMeasurements(RawMeasurements forRawMeasurements, String valuesUuid) {
        final JSXmlMeasurements jsxm = this.createJSXmlMeasurements();
        jsxm.setValuesUuid(valuesUuid);
        forRawMeasurements.getDataSeries().add(jsxm);
        return jsxm;
    }

} // EmfmodelFactory
