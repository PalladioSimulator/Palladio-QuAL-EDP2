package org.palladiosimulator.edp2.example;

import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.unit.SI;

import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.edp2.dao.BinaryMeasurementsDao;
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

/**
 * Contains an example how data can be loaded from a file with EDP2.
 * 
 * Files are provided in resources/measures. They were created by running a Simulation with
 * SimuLizar and setting the data source to file data source.
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
        example.loadRepo("measures");
        example.printValues();
        //example.unloadRepo();
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
     * 
     * create and open a LocalDirectoryRepository.
     * 
     * @param meassuresDirectory
     *            path to the directory containing the .edp2 and .edp2bin files.
     */
    private void loadRepo(final String meassuresDirectory) {

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
     * Print some values from the loaded repo to (1) check that the repo got correctly loaded and
     * (2) demonstrate how to access the actual values.
     * 
     */
    private void printValues() {
        List<ExperimentGroup> experGroups = this.repo.getExperimentGroups();

        for (ExperimentGroup group : experGroups) {

            final DataSeries series = getDataSeriese(group);

            MeasurementsDaoFactory factory = repo.getMeasurementsDaoFactory();
            BinaryMeasurementsDao<Double, Duration> dao = null;

            if (factory.getDaoRegistry()
                .isRegistered(series.getValuesUuid())) {
                // if DAO for the data series exists, you can also get it from the registry:
                dao = (BinaryMeasurementsDao<Double, Duration>) factory.getDaoRegistry()
                    .getMeasurementsDao(series.getValuesUuid());
            } else {
                // otherwise create a new one. beware, i already know the type and unit of
                // the example values, if you use other data, you might want to change this.
                dao = factory.createDoubleMeasurementsDao(series.getValuesUuid(), SI.SECOND);
            }

            try {
                dao.open();
                final List<Measure<Double, Duration>> measures = dao.getMeasurements();

                List<Double> values = measures.stream()
                    .map(measure -> measure.getValue())
                    .toList();

                values.stream()
                    .forEach(System.out::println);

            } catch (DataNotAccessibleException e) {
                e.printStackTrace();
            } finally {
                try {
                    dao.close();
                } catch (DataNotAccessibleException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * dig through the experiment group until we get to a data series
     * 
     * for the sake of simplicity, always selects the first of each element and omit any checks for
     * existance.
     * 
     * @param group
     *            experiment group to get the data series from.
     * @return first data series from the group.
     */
    private DataSeries getDataSeriese(final ExperimentGroup group) {
        final ExperimentSetting setting = group.getExperimentSettings()
            .get(0);
        final ExperimentRun run = setting.getExperimentRuns()
            .get(0);
        final Measurement meas = run.getMeasurement()
            .get(0);
        final MeasurementRange range1 = meas.getMeasurementRanges()
            .get(0);
        final RawMeasurements raw = range1.getRawMeasurements();
        final DataSeries series = raw.getDataSeries()
            .get(0);
        return series;
    }

}
