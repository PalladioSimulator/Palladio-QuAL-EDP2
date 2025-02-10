package org.palladiosimulator.edp2.example;jakartajakartajakartajakartajakartajakartajakartajakartajakarta

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Vector;
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
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentDataPackage;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentGroup;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentRun;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentSetting;
import org.palladiosimulator.edp2.models.ExperimentData.Measurement;
import org.palladiosimulator.edp2.models.ExperimentData.MeasurementRange;
import org.palladiosimulator.edp2.models.ExperimentData.MeasuringType;
import org.palladiosimulator.edp2.models.ExperimentData.RawMeasurements;
import org.palladiosimulator.edp2.models.Repository.Repository;
import org.palladiosimulator.edp2.util.MeasurementsUtility;
import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.measurementframework.TupleMeasurement;
import org.palladiosimulator.metricspec.BaseMetricDescription;
import org.palladiosimulator.metricspec.CaptureType;
import org.palladiosimulator.metricspec.DataType;
import org.palladiosimulator.metricspec.Description;
import org.palladiosimulator.metricspec.Identifier;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.metricspec.MetricSetDescription;
import org.palladiosimulator.metricspec.NumericalBaseMetricDescription;
import org.palladiosimulator.metricspec.PersistenceKindOptions;
import org.palladiosimulator.metricspec.Scale;
import org.palladiosimulator.metricspec.TextualBaseMetricDescription;
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
public class PieChartExampleData {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(StoreExample.class.getCanonicalName());
    /** EMF initialization. Must exist but not be used in the further code. */
    @SuppressWarnings("unused")
    private static final ExperimentDataPackage experimentDataPackage = ExperimentDataPackage.eINSTANCE;
    /** Shortcut to experiment data factory. */
    private final ExperimentDataFactory experimentDataFactory = ExperimentDataFactory.eINSTANCE;

    // Create default units
    private final Unit<Duration> timeUnit = SI.SECOND;
    private final Unit<Dimensionless> numberUnit = new BaseUnit<Dimensionless>("Threads");

    /*
     * Metric descriptions They are only assigned once in the constructor. Declaring the field final
     * does not work as the assignment is delegate to another method which is not recognized as
     * correct initialization in the constructor.
     */
    public static final String SimTimeUUID = "_38mSASUPEd6gmLudJva2Dw";
    public static final String NumberOfActiveJobsUUID = "_fvrNghUQEd8fmLudJva3De";
    private BaseMetricDescription SimTime;
    private BaseMetricDescription NumberOfActiveJobs;
    private MetricSetDescription NumberOfActiveJobsSimulated;

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

    public PieChartExampleData() {
    }

    /**
     * Returns all exemplary descriptions.
     * 
     * @return A collection containing all descriptions.
     */
    public Collection<Description> getDescriptions() {

        final Vector<Description> list = new Vector<Description>();
        list.add(SimTime);
        list.add(NumberOfActiveJobs);
        return list;
    }

    /**
     * Creates all exemplary metric set descriptions.
     */
    public Collection<MetricDescription> createExampleMetricSetDescriptions(
            final EList<Description> existingDescriptions) {
        final Collection<MetricDescription> newDescriptions = new Vector<MetricDescription>();
        NumberOfActiveJobsSimulated = checkExistingMetricSetDescription(
                existingDescriptions,
                newDescriptions,
                "_26mSASnPEt8gmLuDJva2Dw",
                "Number of active jobs (Simulated)",
                "The currently active number of jobs, determined in a simulation. Consists of the simulation time and a number corresponding to the amount of active jobs.",
                SimTime, NumberOfActiveJobs);
        LOGGER.info("Metric descriptions created.");
        return newDescriptions;
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
            final Collection<MetricDescription> newDescriptions, final String uuid, final String name,
            final String textualDescription, final BaseMetricDescription submetric1,
            final BaseMetricDescription submetric2) {
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

        newDescriptions.add(description);
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
            final EList<Description> existingDescriptions, final Collection<MetricDescription> newDescriptions,
            final String uuid, final String name, final String textualDescription, final CaptureType captureType,
            final Scale scale, final Unit<?> defaultUnit, final DataType dataType) {
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
        newDescriptions.add(description);
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
            final EList<Description> existingDescriptions, final Collection<MetricDescription> newDescriptions,
            final String uuid, final String name, final String textualDescription, final Scale scale,
            final DataType dataType) {
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
        newDescriptions.add(description);
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
     * Creates all exemplary base metric descriptions. Reuses existing ones (if available).
     * 
     * @param existingDescriptions
     *            List of all existing metric descriptions.
     */
    public Collection<MetricDescription> createExampleBaseMetricDescriptions(
            final EList<Description> existingDescriptions) {
        final Collection<MetricDescription> newDescriptions = new Vector<MetricDescription>();
        SimTime = checkExistingNumericalBaseMetricDescription(existingDescriptions, newDescriptions, SimTimeUUID,
                "Simulation Time", "Time passed within a simulation. Starting with 0.0.", CaptureType.REAL_NUMBER,
                Scale.INTERVAL, timeUnit, DataType.QUANTITATIVE);
        NumberOfActiveJobs = checkExistingNumericalBaseMetricDescription(existingDescriptions, newDescriptions,
                NumberOfActiveJobsUUID, "Active Jobs", "Number of Jobs, the CPU is currently working on.",
                CaptureType.INTEGER_NUMBER, Scale.INTERVAL, numberUnit, DataType.QUANTITATIVE);
        return newDescriptions;
    }

    /**
     * Creates the exemplary experiment meta data.
     */
    public void createExampleExperimentMetadata() {
        // create measures

        /*
         * serviceCallA = experimentDataFactory.createEdp2Measure( "Service Call A",
         * ResponseTimeSimulated); cpuA = experimentDataFactory.createEdp2Measure("CPU A",
         * AssignedThreadsSimulated); architectureQualityA =
         * experimentDataFactory.createEdp2Measure( "Architecture A",
         * ArchitectureQualityEstimation); // create experiment group
         */
        groupA = experimentDataFactory.createExperimentGroup("Exemplary use of EDP2");
        groupA.getMeasuringTypes().add(serviceCallA); // belongs to the group
        groupA.getMeasuringTypes().add(cpuA); // belongs to the group
        groupA.getMeasuringTypes().add(architectureQualityA); // belongs to the group

        // create experiment settings
        settingA = experimentDataFactory.createExperimentSetting(groupA, "Experiment Setting #1");
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
        // service call a
        measurementServiceCallA = experimentDataFactory.createMeasurement(serviceCallA);
        runA0 = experimentDataFactory.createExperimentRun(settingA);
        runA0.getMeasurement().add(measurementServiceCallA);
        range = experimentDataFactory.createMeasurementRange(measurementServiceCallA);
        rawMeasurements = experimentDataFactory.createRawMeasurements(range);
        MeasurementsUtility.createDAOsForRawMeasurements(rawMeasurements);
        // architecture quality estimation a
        measurementArchitectureQuality = experimentDataFactory.createMeasurement(architectureQualityA);
        runA0.getMeasurement().add(measurementArchitectureQuality);
        MeasurementsUtility.createDAOsForRawMeasurements(experimentDataFactory
                .createRawMeasurements(experimentDataFactory.createMeasurementRange(measurementArchitectureQuality)));
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
                    .getMetric(), javax.measure.Measure.valueOf(i, timeUnit), javax.measure.Measure.valueOf(
                    random.nextDouble() * 10.0, timeUnit));
            MeasurementsUtility.storeMeasurement(measurementServiceCallA, measurement);
        }
        LOGGER.info("Measurements for service call A generated.");

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
