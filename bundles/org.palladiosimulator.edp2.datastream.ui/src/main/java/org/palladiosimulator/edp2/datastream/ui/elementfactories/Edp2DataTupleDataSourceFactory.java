package org.palladiosimulator.edp2.datastream.ui.elementfactories;

import org.eclipse.ui.IMemento;
import org.palladiosimulator.edp2.EDP2Plugin;
import org.palladiosimulator.edp2.datastream.configurable.IPropertyConfigurable;
import org.palladiosimulator.edp2.datastream.configurable.PropertyConfigurableElementFactory;
import org.palladiosimulator.edp2.datastream.edp2source.Edp2DataTupleDataSource;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentGroup;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentGroupRun;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentRun;
import org.palladiosimulator.edp2.models.ExperimentData.RawMeasurements;
import org.palladiosimulator.edp2.models.ExperimentData.Run;
import org.palladiosimulator.edp2.models.Repository.Repository;

public class Edp2DataTupleDataSourceFactory extends PropertyConfigurableElementFactory {

    private static final String MEASUREMENTS_UUID = "MeasurementsUUID";
    private static final String REPOSITORY_UUID = "RepositoryUUID";
    public static final String FACTORY_ID = Edp2DataTupleDataSourceFactory.class.getCanonicalName();

    @Override
    protected IPropertyConfigurable createElementInternal(final IMemento memento) {
        final String repoID = memento.getString(REPOSITORY_UUID);
        final String measurementID = memento.getString(MEASUREMENTS_UUID);

        Repository repo = null;
        for (final Repository r : EDP2Plugin.INSTANCE.getRepositories().getAvailableRepositories()) {
            if (r.getId().equals(repoID)) {
                repo = r;
                break;
            }
        }
        final RawMeasurements measurements = (RawMeasurements) repo.getExperimentGroups().get(0).eResource()
                .getEObject(measurementID);
        return new Edp2DataTupleDataSource(measurements);
    }

    public static void saveState(final IMemento memento, final Edp2DataTupleDataSource edp2DataTupleDataSource) {
        final RawMeasurements measurements = edp2DataTupleDataSource.getRawMeasurements();
        final Run run = measurements.getMeasurementRange().getMeasurement().getRun();

        final ExperimentGroup experimentGroup;
        if (run instanceof ExperimentRun) {
            experimentGroup = ((ExperimentRun) run).getExperimentSetting().getExperimentGroup();
        } else if (run instanceof ExperimentGroupRun) {
            experimentGroup = ((ExperimentGroupRun) run).getExperimentgroup();
        } else {
            throw new IllegalArgumentException("Unknown run type");
        }
        if (experimentGroup != null && experimentGroup.getRepository() != null) {
            memento.putString(REPOSITORY_UUID, experimentGroup.getRepository().getId());
        }
        if (measurements != null) {
            memento.putString(MEASUREMENTS_UUID, measurements.getId());
        }
    }

}
