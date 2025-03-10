package org.palladiosimulator.edp2.datastream.ui.edp2source;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.palladiosimulator.edp2.datastream.edp2source.Edp2DataTupleDataSource;
import org.palladiosimulator.edp2.datastream.ui.elementfactories.Edp2DataTupleDataSourceFactory;
import org.palladiosimulator.edp2.models.ExperimentData.RawMeasurements;

public class Edp2DataTupleDataSourceElement extends Edp2DataTupleDataSource implements IPersistableElement {
    public Edp2DataTupleDataSourceElement(RawMeasurements measurements) {
        super(measurements);
    }

    @Override
    public void saveState(final IMemento memento) {
        Edp2DataTupleDataSourceFactory.saveState(memento, this);
    }

    @Override
    public String getFactoryId() {
        return Edp2DataTupleDataSourceFactory.FACTORY_ID;
    }
}
