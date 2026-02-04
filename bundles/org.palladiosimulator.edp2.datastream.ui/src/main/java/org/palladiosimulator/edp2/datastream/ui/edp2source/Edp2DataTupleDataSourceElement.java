package org.palladiosimulator.edp2.datastream.ui.edp2source;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.palladiosimulator.edp2.datastream.edp2source.Edp2DataTupleDataSource;
import org.palladiosimulator.edp2.datastream.ui.elementfactories.Edp2DataTupleDataSourceFactory;

public class Edp2DataTupleDataSourceElement implements IPersistableElement {
	
	private Edp2DataTupleDataSource source;
	
    public Edp2DataTupleDataSourceElement(Edp2DataTupleDataSource source) {
    	this.source = source;
    }

    @Override
    public void saveState(final IMemento memento) {
        Edp2DataTupleDataSourceFactory.saveState(memento, source);
    }

    @Override
    public String getFactoryId() {
        return Edp2DataTupleDataSourceFactory.FACTORY_ID;
    }
}
