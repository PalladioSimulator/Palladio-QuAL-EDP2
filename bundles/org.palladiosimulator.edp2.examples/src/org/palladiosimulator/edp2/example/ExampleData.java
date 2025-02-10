package org.palladiosimulator.edp2.example;jakartajakartajakartajakartajakartajakartajakartajakartajakarta

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Duration;
import javax.measure.unit.BaseUnit;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.edp2.dao.MeasurementsDao;
import org.palladiosimulator.edp2.models.ExperimentData.DataSeries;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentDataFactory;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentGroup;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentRun;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentSetting;
import org.palladiosimulator.edp2.models.ExperimentData.Measurement;
import org.palladiosimulator.edp2.models.ExperimentData.MeasurementRange;
import org.palladiosimulator.edp2.models.ExperimentData.MeasuringType;
import org.palladiosimulator.edp2.models.ExperimentData.RawMeasurements;
import org.palladiosimulator.edp2.models.Repository.Repository;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringpointFactory;
import org.palladiosimulator.edp2.models.measuringpoint.StringMeasuringPoint;
import org.palladiosimulator.edp2.util.MeasurementsUtility;
import org.palladiosimulator.measurementframework.BasicMeasurement;
import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.measurementframework.TupleMeasurement;
import org.palladiosimulator.measurementframework.measure.IdentifierMeasure;
import org.palladiosimulator.metricspec.BaseMetricDescription;
import org.palladiosimulator.metricspec.CaptureType;
import org.palladiosimulator.metricspec.DataType;
import org.palladiosimulator.metricspec.Description;
import org.palladiosimulator.metricspec.Identifier;
import org.palladiosimulator.metricspec.MetricSetDescription;
import org.palladiosimulator.metricspec.NumericalBaseMetricDescription;
import org.palladiosimulator.metricspec.PersistenceKindOptions;
import org.palladiosimulator.metricspec.Scale;
import org.palladiosimulator.metricspec.TextualBaseMetricDescription;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.metricspec.util.builder.IdentifierBuilder;
import org.palladiosimulator.metricspec.util.builder.MetricSetDescriptionBuilder;
import org.palladiosimulator.metricspec.util.builder.NumericalBaseMetricDescriptionBuilder;
import org.palladiosimulator.metricspec.util.builder.TextualBaseMetricDescriptionBuilder;

/**
 * Contains all exemplary including an example model instance. This class also demonstrates the use
 * of EDP2 for storing measurements and creating descriptions. The implemented example follows the
 * EDP2 creation steps: <li>
 * <ul/>
 * Step 1: Build ExperimentSetting
 * <ul/>
 * Step 2: Prepare Experiment Run (Add Raw Measurements, AggregationFunctions)
 * <ul/>
 * Step 3: Run the experiment and generate measurements</li> All Measurements are stored in a
 * Measurements directory. Additionally, all standard descriptions can be retrieved.
 * 
 * @author groenda
 */
public class ExampleData {

    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(StoreExample.class.getCanonicalName());

    private static final Unit<Dimensionless> NUMBER_OF_THREADS_UNIT = new BaseUnit<Dimensionless>("Threads");

    /*
     * Metric descriptions They are only assigned once in the constructor. Declaring the field final
     * does not work as the assignment is delegate to another method which is not recognized as
     * correct initialization in the constructor.
     */
    public static final String AbsoluteFrequencyUUID = "_AiroIZMbEd6Vw8NDgVSYcg";
    public static final String ArchitectureQualityEstimationUUID = "_lss1MEhpEd-SQI4N8E0NHA";
    public static final String ArchitectureQualityEstimationLowUUID = "_BRpvcEigEd-uCvl0Z-GteQ";
    public static final String ArchitectureQualityEstimationMediumUUID = "_JYesoEigEd-4XZQqGmj8Pg";
    public static final String ArchitectureQualityEstimationHighUUID = "_LCpvcEigEd-s193kEND-BA";

    public final BaseMetricDescription absoluteFrequency;
    public final TextualBaseMetricDescription architectureQualityEstimation;
    public final MetricSetDescription serviceCallARTMetric;

    public final Identifier architectureQualityIdentifierLow;
    public final Identifier architectureQualityIdentifierMedium;
    public final Identifier architectureQualityIdentifierHigh;

    // Experiment data
    private ExperimentGroup groupA;
    private MeasuringType serviceCallA;
    private MeasuringType cpuA;
    private MeasuringType architectureQualityA;
    private ExperimentSetting settingA;

    // specific for each simulated run
    private ExperimentRun runA0;
    private Measurement measurementServiceCallA;
    private Measurement measurementArchitectureQuality;
    private MeasurementRange range;
    private RawMeasurements rawMeasurements;

    private final ExperimentDataFactory EXPERIMENT_DATA_FACTORY = ExperimentDataFactory.eINSTANCE;
    private final MeasuringpointFactory MEASURING_POINT_FACTORY = MeasuringpointFactory.eINSTANCE;

    public ExampleData(final EList<Description> existingDescriptions) {
        super();
        absoluteFrequency = checkExistingNumericalBaseMetricDescription(existingDescriptions, AbsoluteFrequencyUUID,
                "Frequency", "Absolute frequency of measurements or events. For example, of "
                        + "measurements lying within an interval of a histogram.", CaptureType.INTEGER_NUMBER,
                Scale.RATIO, NUMBER_OF_THREADS_UNIT, DataType.QUANTITATIVE);
        architectureQualityEstimation = checkExistingTextualBaseMetricDescription(existingDescriptions,
                ArchitectureQualityEstimationUUID, "Architecture Quality Estimation",
                "Expresses a subjective expert estimation on a software architecture's overall quality.",
                Scale.ORDINAL, DataType.QUALITATIVE);
        architectureQualityIdentifierLow = checkExistingIdentifier(architectureQualityEstimation,
                ArchitectureQualityEstimationLowUUID, "Low");
        architectureQualityIdentifierMedium = checkExistingIdentifier(architectureQualityEstimation,
                ArchitectureQualityEstimationMediumUUID, "Medium");
        architectureQualityIdentifierHigh = checkExistingIdentifier(architectureQualityEstimation,
                ArchitectureQualityEstimationHighUUID, "High");
        serviceCallARTMetric = checkExistingMetricSetDescription(existingDescriptions, "_LCpvcEigEd-s193kEND-BE",
                "RT of Call A", "RT of call A", MetricDescriptionConstants.POINT_IN_TIME_METRIC,
                MetricDescriptionConstants.RESPONSE_TIME_METRIC);
        architectureQualityEstimation.getIdentifiers().addAll(
                Arrays.asList(architectureQualityIdentifierLow, architectureQualityIdentifierMedium,
                        architectureQualityIdentifierHigh));
    }

    /**
     * Checks if a list of metric descriptions contains a descriptions and creates it if not.
     * 
     * @param existingDescriptions
     *            List of existing descriptions.
     * @param newDescriptions
     *            List of descriptions newly created.
     * @param uuid
     *            UUID of the description.
     * @param name
     *            Name of the description.
     * @param textualDescription
     *            Textual description of the description.
     * @param submetric1
     *            First sub metric of the metric set.
     * @param submetric2
     *            Second sub metric of the metric set.
     * @return the (existing or created) metric description.
     */
    private MetricSetDescription checkExistingMetricSetDescription(final EList<Description> existingDescriptions,
            final String uuid, final String name, final String textualDescription,
            final BaseMetricDescription submetric1, final BaseMetricDescription submetric2) {
        if (existingDescriptions != null) {
            // look for metric with UUID
            for (final Description description : existingDescriptions) {
                if (description.getId().equals(uuid)) {
                    return (MetricSetDescription) description;
                }
            }
        }
        // existing descriptions is null OR the description was not found
        final MetricSetDescription description = MetricSetDescriptionBuilder.newMetricSetDescriptionBuilder()
                .name(name).textualDescription(textualDescription).id(uuid).subsumedMetrics(submetric1)
                .subsumedMetrics(submetric2).build();

        existingDescriptions.add(description);
        return description;
    }

    /**
     * Checks if a list of metric descriptions contains a descriptions and creates it if not.
     * 
     * @param existingDescriptions
     *            List of existing descriptions.
     * @param newDescriptions
     *            List of descriptions newly created.
     * @param uuid
     *            UUID of the description.
     * @param name
     *            Name of the description.
     * @param textualDescription
     *            Textual description of the description.
     * @param captureType
     *            Capture type of the description.
     * @param scale
     *            Scale of the description.
     * @param defaultUnit
     *            Default unit of the description.
     * @param monotonic
     *            Property monotonic of the description.
     * @param dataType
     *            Data type.
     * @return the (existing or created) metric description.
     */
    private NumericalBaseMetricDescription checkExistingNumericalBaseMetricDescription(
            final EList<Description> existingDescriptions, final String uuid, final String name,
            final String textualDescription, final CaptureType captureType, final Scale scale,
            final Unit<?> defaultUnit, final DataType dataType) {
        if (existingDescriptions != null) {
            // look for metric with UUID
            for (final Description description : existingDescriptions) {
                if (description.getId().equals(uuid)) {
                    return (NumericalBaseMetricDescription) description;
                }
            }
        }
        // existing descriptions is null OR the description was not found
        final NumericalBaseMetricDescription description = NumericalBaseMetricDescriptionBuilder
                .newNumericalBaseMetricDescriptionBuilder().name(name).textualDescription(textualDescription)
                .captureType(captureType).scale(scale).dataType(dataType).defaultUnit(defaultUnit)
                .persistenceKind(PersistenceKindOptions.BINARY_PREFERRED).id(uuid).build();
        existingDescriptions.add(description);
        return description;
    }

    /**
     * Checks if a list of metric descriptions contains a descriptions and creates it if not.
     * 
     * @param existingDescriptions
     *            List of existing descriptions.
     * @param newDescriptions
     *            List of descriptions newly created.
     * @param uuid
     *            UUID of the description.
     * @param name
     *            Name of the description.
     * @param textualDescription
     *            Textual description of the description.
     * @param captureType
     *            Capture type of the description.
     * @param scale
     *            Scale of the description.
     * @param defaultUnit
     *            Default unit of the description.
     * @param monotonic
     *            Property monotonic of the description.
     * @param dataType
     *            Data type.
     * @return the (existing or created) metric description.
     */
    private TextualBaseMetricDescription checkExistingTextualBaseMetricDescription(
            final EList<Description> existingDescriptions, final String uuid, final String name,
            final String textualDescription, final Scale scale, final DataType dataType) {
        if (existingDescriptions != null) {
            // look for metric with UUID
            for (final Description description : existingDescriptions) {
                if (description.getId().equals(uuid)) {
                    return (TextualBaseMetricDescription) description;
                }
            }
        }
        // existing descriptions is null OR the description was not found
        final TextualBaseMetricDescription description = TextualBaseMetricDescriptionBuilder
                .newTextualBaseMetricDescriptionBuilder().name(name).textualDescription(textualDescription)
                .scale(scale).dataType(dataType).id(uuid).build();
        existingDescriptions.add(description);
        return description;
    }

    /**
     * Checks if the provided identifier is part of the textual description. If not, the identifier
     * is created and added to the description.
     * 
     * @param description
     *            The description to check.
     * @param uuid
     *            UUID of the identifier.
     * @param literal
     *            The literal (if the identifier is created).
     * @return The found or created identifier.
     */
    private Identifier checkExistingIdentifier(final TextualBaseMetricDescription description, final String uuid,
            final String literal) {
        if (description != null) {
            // look for identifier and return
            for (final Identifier identifier : description.getIdentifiers()) {
                if (identifier.getId().equals(uuid)) {
                    return identifier;
                }
            }
            // create identifier if not found
            final Identifier identifier = IdentifierBuilder.newIdentifierBuilder().id(uuid).literal(literal).build();
            description.getIdentifiers().add(identifier);
            return identifier;
        }
        throw new IllegalArgumentException("Description must not be null.");
    }

    /**
     * Creates the exemplary experiment meta data.
     */
    public void createExampleExperimentMetadata() {
        // create simple String measuring points
        final StringMeasuringPoint serviceCallAMeasuringPoint = MEASURING_POINT_FACTORY.createStringMeasuringPoint();
        serviceCallAMeasuringPoint.setMeasuringPoint("Service Call A");
        final StringMeasuringPoint cpuAMeasuringPoint = MEASURING_POINT_FACTORY.createStringMeasuringPoint();
        serviceCallAMeasuringPoint.setMeasuringPoint("CPU A");
        final StringMeasuringPoint architectureAMeasuringPoint = MEASURING_POINT_FACTORY.createStringMeasuringPoint();
        serviceCallAMeasuringPoint.setMeasuringPoint("Architecture A");

        // create measures
        serviceCallA = EXPERIMENT_DATA_FACTORY.createMeasuringType(serviceCallAMeasuringPoint, serviceCallARTMetric);
        cpuA = EXPERIMENT_DATA_FACTORY.createMeasuringType(cpuAMeasuringPoint,
                MetricDescriptionConstants.STATE_OF_ACTIVE_RESOURCE_OVER_TIME_METRIC);
        architectureQualityA = EXPERIMENT_DATA_FACTORY.createMeasuringType(architectureAMeasuringPoint,
                architectureQualityEstimation);
        // create experiment group
        groupA = EXPERIMENT_DATA_FACTORY.createExperimentGroup("Exemplary use of EDP2");
        groupA.getMeasuringTypes().add(serviceCallA); // belongs to the group
        groupA.getMeasuringTypes().add(cpuA); // belongs to the group
        groupA.getMeasuringTypes().add(architectureQualityA); // belongs to the group

        // create experiment settings
        settingA = EXPERIMENT_DATA_FACTORY.createExperimentSetting(groupA, "Experiment Setting #1");
        settingA.getMeasuringTypes().add(serviceCallA); // used in this setting
        settingA.getMeasuringTypes().add(cpuA); // used in this setting
        settingA.getMeasuringTypes().add(architectureQualityA); // used in this setting

        LOGGER.info("Example metadata created.");
    }

    /**
     * Returns the exemplary experiment meta data.
     * 
     * @return the meta data.
     */
    public ExperimentGroup getExampleExperimentGroup() {
        return groupA;
    }

    /**
     * Prepares all data series for a run of experiment setting A.
     */
    private void prepareExperimentRunForSettingA() {
        runA0 = EXPERIMENT_DATA_FACTORY.createExperimentRun(settingA);

        // service call a
        measurementServiceCallA = EXPERIMENT_DATA_FACTORY.createMeasurement(serviceCallA);
        runA0.getMeasurement().add(measurementServiceCallA);
        range = EXPERIMENT_DATA_FACTORY.createMeasurementRange(measurementServiceCallA);
        rawMeasurements = EXPERIMENT_DATA_FACTORY.createRawMeasurements(range);
        MeasurementsUtility.createDAOsForRawMeasurements(rawMeasurements);
        // architecture quality estimation a
        measurementArchitectureQuality = EXPERIMENT_DATA_FACTORY.createMeasurement(architectureQualityA);
        runA0.getMeasurement().add(measurementArchitectureQuality);
        MeasurementsUtility.createDAOsForRawMeasurements(EXPERIMENT_DATA_FACTORY
                .createRawMeasurements(EXPERIMENT_DATA_FACTORY.createMeasurementRange(measurementArchitectureQuality)));
        LOGGER.info("Experiment run for setting A prepared.");
    }

    /**
     * Simulates the run of an experiment and generates (dummy) measurements.
     */
    private void runExperimentForSettingA() {
        runA0.setStartTime(new Date());
        final Random random = new Random();
        MeasuringValue measurement;
        // create 1000 dummy measurements for service call A.
        for (int i = 0; i < 1000; i++) {
            measurement = new TupleMeasurement((MetricSetDescription) measurementServiceCallA.getMeasuringType()
                    .getMetric(), javax.measure.Measure.valueOf((double) i, SI.SECOND), javax.measure.Measure.valueOf(
                    random.nextDouble() * 10.0, SI.SECOND));
            MeasurementsUtility.storeMeasurement(measurementServiceCallA, measurement);
        }
        // create
        // TODO zufallszahl assigned threads
        LOGGER.info("Measurements for service call A generated.");
        // create one architecture quality estimation of architecture quality estimation A.
        measurement = new BasicMeasurement<Identifier, Dimensionless>(new IdentifierMeasure<Dimensionless>(
                architectureQualityIdentifierLow, Unit.ONE), (BaseMetricDescription) measurementArchitectureQuality
                .getMeasuringType().getMetric());
        MeasurementsUtility.storeMeasurement(measurementArchitectureQuality, measurement);
        LOGGER.info("Measurements for architecture quality A generated.");
    }

    /**
     * Simulates running an experiment.
     */
    public void simulateExperimentRun() {
        prepareExperimentRunForSettingA();
        runExperimentForSettingA();
        LOGGER.info("Experiment run finished. Example data created.");
    }

    /**
     * Reads and prints the exemplary data stored for the experiment group in the repository. Does
     * only print the generated example data in the DAOs. All data must be created by the other
     * example data code to ensure correct functionality.
     * 
     * @param repo
     *            The repository.
     * @param experimentGroupUuid
     *            UUID of the experiment group which is queried.
     * @return Formatted data.
     */
    @SuppressWarnings("unchecked")
    public String printStoredMeasurements(final Repository repo, final String experimentGroupUuid) {
        try {
            String result = "";
            // get DAOs
            for (final ExperimentGroup group : repo.getExperimentGroups()) {
                if (group.getId().equals(experimentGroupUuid)) {
                    EList<DataSeries> dataSeries = group.getExperimentSettings().get(0).getExperimentRuns().get(0)
                            .getMeasurement().get(0).getMeasurementRanges().get(0).getRawMeasurements().getDataSeries();
                    final MeasurementsDao<Double, Duration> omdSeries1 = (MeasurementsDao<Double, Duration>) MeasurementsUtility
                            .<Duration> getMeasurementsDao(dataSeries.get(0));
                    final MeasurementsDao<Double, Duration> omdSeries2 = (MeasurementsDao<Double, Duration>) MeasurementsUtility
                            .<Duration> getMeasurementsDao(dataSeries.get(1));
                    // print stored data
                    result += "Stored service call example data\n";
                    result += "--------------------------------\n\n";
                    // label data series according to metric definitions
                    final MetricSetDescription md = (MetricSetDescription) dataSeries.get(0).getRawMeasurements()
                            .getMeasurementRange().getMeasurement().getMeasuringType().getMetric();
                    result += md.getSubsumedMetrics().get(0).getName() + "\t"
                            + md.getSubsumedMetrics().get(1).getName() + "\n";
                    // list data
                    final List<javax.measure.Measure<Double, Duration>> list1 = omdSeries1.getMeasurements();
                    final List<javax.measure.Measure<Double, Duration>> list2 = omdSeries2.getMeasurements();
                    for (int pos = 0; pos < list1.size(); pos++) {
                        result += list1.get(pos) + "\t" + list2.get(pos) + "\n";
                    }
                    dataSeries = group.getExperimentSettings().get(0).getExperimentRuns().get(0).getMeasurement()
                            .get(1).getMeasurementRanges().get(0).getRawMeasurements().getDataSeries();
                    final MeasurementsDao<Identifier, Dimensionless> nmd = (MeasurementsDao<Identifier, Dimensionless>) MeasurementsUtility
                            .<Dimensionless> getMeasurementsDao(dataSeries.get(0));
                    result += "Architecture Quality Estimation was: "
                            + nmd.getMeasurements().get(0).getValue().getLiteral();
                }
            }
            return result;
        } catch (final NullPointerException npe) {
            LOGGER.log(
                    Level.SEVERE,
                    "Access to created example measurements failed. The data is either not created by the example code or there are store/load errors.");
            return "";
        }
    }

    /**
     * Reads and prints the example data from a repository.
     * 
     * @param repo
     *            The repository.
     * @return Formatted data or <code>null</code> if there is no data.
     */
    public String printStoredMeasurements(final Repository repo) {
        return printStoredMeasurements(repo, groupA.getId());
    }
}
