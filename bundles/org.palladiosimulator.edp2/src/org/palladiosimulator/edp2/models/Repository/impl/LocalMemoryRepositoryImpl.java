package org.palladiosimulator.edp2.models.Repository.impl;

import org.palladiosimulator.edp2.dao.MeasurementsDaoFactory;
import org.palladiosimulator.edp2.dao.exception.DataNotAccessibleException;
import org.palladiosimulator.edp2.dao.impl.MetaDaoImpl;
import org.palladiosimulator.edp2.dao.memory.impl.MemoryMeasurementsDaoFactory;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Local Memory Repository</b></em>'. <!-- end-user-doc -->
 */
public class LocalMemoryRepositoryImpl extends LocalMemoryRepositoryImplGen {

    protected LocalMemoryRepositoryImpl() {
        super();
        this.metaDao = new MetaDaoImpl() {

            private final MeasurementsDaoFactory daoFactory = new MemoryMeasurementsDaoFactory();

            @Override
            public void flush() {
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.palladiosimulator.edp2.dao.impl.Edp2DaoImpl#open()
             */
            @Override
            public synchronized void open() throws DataNotAccessibleException {
                super.open();
                this.setOpen();
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.palladiosimulator.edp2.dao.impl.Edp2DaoImpl#close()
             */
            @Override
            public synchronized void close() throws DataNotAccessibleException {
                super.close();
                this.setClosed();
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.palladiosimulator.edp2.dao.impl.MetaDaoImpl#getMeasurementsDaoFactory()
             */
            @Override
            public MeasurementsDaoFactory getMeasurementsDaoFactory() {
                super.getMeasurementsDaoFactory();
                return this.daoFactory;
            }

        };
    }
} // LocalMemoryRepositoryImpl
