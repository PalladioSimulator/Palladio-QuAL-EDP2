package org.palladiosimulator.edp2.example;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.unit.SI;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.palladiosimulator.edp2.dao.MeasurementsDao;
import org.palladiosimulator.edp2.dao.MeasurementsDaoFactory;
import org.palladiosimulator.edp2.dao.exception.DataNotAccessibleException;
import org.palladiosimulator.edp2.impl.RepositoryManager;
import org.palladiosimulator.edp2.local.LocalDirectoryRepository;
import org.palladiosimulator.edp2.local.localFactory;
import org.palladiosimulator.edp2.models.ExperimentData.DataSeries;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentGroup;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentRun;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentSetting;
import org.palladiosimulator.edp2.models.ExperimentData.Measurement;
import org.palladiosimulator.edp2.models.ExperimentData.MeasurementRange;
import org.palladiosimulator.edp2.models.ExperimentData.RawMeasurements;
import org.palladiosimulator.edp2.models.Repository.Repositories;
import org.palladiosimulator.edp2.util.MeasurementsUtility;
import org.palladiosimulator.edp2.util.MetricDescriptionUtility;
import org.palladiosimulator.metricspec.BaseMetricDescription;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.pcmmeasuringpoint.PcmmeasuringpointPackage;

import simulizarmeasuringpoint.SimulizarmeasuringpointPackage;

/**
 * Contains an example how data can be loaded from a file with EDP2.
 * 
 * Files are provided in resources/measures. They were created by running a Simulation with
 * SimuLizar and setting the data source to file data source.
 * 
 * Additional information to be found in ./doc/LoadFromFileExample_description.md.
 * 
 */
public class LoadFromFileExample {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(LoadFromFileExample.class.getCanonicalName());

    private LocalDirectoryRepository repo;

    /**
     * Initializes an instance of this class.
     */
    public LoadFromFileExample() {
        super();
    }

    /**
     * Main method to run the example.
     *
     * @param args
     *            Not used.
     */
    public static void main(final String[] args) {
        final LoadFromFileExample example = new LoadFromFileExample();
        example.initPathmap();
        example.loadRepo("measures");
        example.printValues();
        // example.unloadRepo();
        // skip unload 'cause it tries to save the repo, but fails and corrupts my example files :(

    }

    /**
     * close the repo by removing it from the manager.
     */
    private void unloadRepo() {
        Repositories repos = RepositoryManager.getCentralRepository();
        RepositoryManager.removeRepository(repos, this.repo);
    }

    /**
     * Put the common metrics into the pathmap.
     * 
     * Without the common metrics, the metric description is all wrong.
     */
    private void initPathmap() {
        final URL url = getClass().getClassLoader()
            .getResource("commonMetrics.metricspec"); // c.f. plugin dependecies, it's really there
        final URI uri = URI.createURI(url.toString())
            .trimSegments(1);

        URIConverter.URI_MAP.put(URI.createURI("pathmap://METRIC_SPEC_MODELS/"), uri);

        final Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
        final Map<String, Object> m = reg.getExtensionToFactoryMap();
        m.put("metricspec", new XMIResourceFactoryImpl());
    }

    /**
     * 
     * create and open a {@link LocalDirectoryRepository}.
     * 
     * @param meassuresDirectory
     *            path to the directory containing the .edp2 and .edp2bin files.
     */
    private void loadRepo(final String meassuresDirectory) {

        // initialize these, because the edp2 model depends on them.
        PcmmeasuringpointPackage pcmMeasuringpointPackage = PcmmeasuringpointPackage.eINSTANCE;
        SimulizarmeasuringpointPackage simulizarMeasuringPointPackage = SimulizarmeasuringpointPackage.eINSTANCE;

        this.repo = localFactory.eINSTANCE.createLocalDirectoryRepository();
        // the lowercase 'l' might be confusing, but localFactory is still a class.

        final URL url = getClass().getClassLoader()
            .getResource(meassuresDirectory);

        final URI uri = URI.createURI(url.toString());
        this.repo.setUri(uri.toString());

        Repositories repos = RepositoryManager.getCentralRepository();
        RepositoryManager.addRepository(repos, this.repo);
        // adding the repo to the manager also opens the repo.
    }

    /**
     * Print some values from the loaded repository.
     * 
     * The purpose is twofold : (1) check that the repository got correctly loaded and (2)
     * demonstrate how to access the measured values.
     * 
     */
    private void printValues() {
        List<ExperimentGroup> experGroups = this.repo.getExperimentGroups();

        for (ExperimentGroup group : experGroups) {
            final List<DataSeries> seriess = this.getDataSeriese(group);

            for (DataSeries series : seriess) {
                final MetricDescription desc = this.getMetric(series);

                System.out.format("\n%s :\n", desc.getName());

                // MeasurementsDao<?, Duration> dao = getDaoManually(series);
                MeasurementsDao<Double, Duration> dao = getDaoWithUtility(series);

                List<Measure<Double, Duration>> measures = dao.getMeasurements();

                measures.stream()
                    .map(measure -> measure.getValue())
                    .forEach(v -> System.out.format("%.2f, ", v));

                try {
                    dao.close();
                } catch (DataNotAccessibleException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Create a DAO using the MeasurementsUtility.
     * 
     * Advantage : less code, utility also takes care of opening the dao and using the correct
     * quantity and unit.
     * 
     * @param series
     *            the series to get a DAO for
     * @return DAO for the series
     */
    private MeasurementsDao<Double, Duration> getDaoWithUtility(final DataSeries series) {
        return (MeasurementsDao<Double, Duration>) MeasurementsUtility.<Duration> getMeasurementsDao(series);
    }

    /**
     * 
     * Create a DAO manually.
     * 
     * Advantage : you understand what's going on. Disadvantage : you must know the quantity and
     * unit.
     *
     * In general, it probably recommendable to use the Utility.
     * 
     * @param series
     *            the series to get a DAO for
     * @return DAO for the series
     */
    private MeasurementsDao<Double, Duration> getDaoManually(final DataSeries series) {
        final MeasurementsDaoFactory factory = repo.getMeasurementsDaoFactory();
        MeasurementsDao<Double, Duration> dao = null;

        if (factory.getDaoRegistry()
            .isRegistered(series.getValuesUuid())) {
            // if DAO for the data series exists, you can also get it from the registry:
            dao = (MeasurementsDao<Double, Duration>) factory.getDaoRegistry()
                .getMeasurementsDao(series.getValuesUuid());
        } else {
            // otherwise create a new one. beware, you must already know the type and unit of
            // the example values.
            dao = factory.createDoubleMeasurementsDao(series.getValuesUuid(), SI.SECOND);
        }

        try {
            dao.open();
        } catch (DataNotAccessibleException e) {
            e.printStackTrace();
        }

        return dao;
    }

    /**
     * dig through the experiment group until we get to a data series.
     * 
     * for the sake of simplicity, always selects the first of each element and omit any checks for
     * existence.
     * 
     * @param group
     *            experiment group to get the data series from.
     * @return first data series from the group.
     */
    private List<DataSeries> getDataSeriese(final ExperimentGroup group) {
        final ExperimentSetting setting = group.getExperimentSettings()
            .get(0);
        final ExperimentRun run = setting.getExperimentRuns()
            .get(0);
        final Measurement meas = run.getMeasurement()
            .get(0);
        final MeasurementRange range1 = meas.getMeasurementRanges()
            .get(0);
        final RawMeasurements raw = range1.getRawMeasurements();
        return raw.getDataSeries();
    }

    /**
     * dig through the meta data until we get to the data series' metric description.
     * 
     * Assumption: data series and base metric descriptions have the same order, i.e. the i-th
     * description describes the i-th data series.
     *
     * @param series
     * @return base metric description for the series.
     */
    private BaseMetricDescription getMetric(final DataSeries series) {

        final RawMeasurements raw = series.getRawMeasurements();
        final int index = raw.getDataSeries()
            .indexOf(series);

        final MetricDescription description = series.getRawMeasurements()
            .getMeasurementRange()
            .getMeasurement()
            .getMeasuringType()
            .getMetric();
        if (description.eContainer() == null) {
            throw new IllegalStateException("MetricDescription not the one from common metrics.");
        }

        return MetricDescriptionUtility.toBaseMetricDescriptions(description)[index];
    }

}
