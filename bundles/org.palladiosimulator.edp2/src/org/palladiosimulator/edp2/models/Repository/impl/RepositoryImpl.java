/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.palladiosimulator.edp2.models.Repository.impl;

import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.palladiosimulator.edp2.dao.MeasurementsDaoFactory;
import org.palladiosimulator.edp2.dao.MetaDao;
import org.palladiosimulator.edp2.dao.exception.DataNotAccessibleException;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentDataPackage;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentGroup;
import org.palladiosimulator.edp2.models.Repository.RepositoryPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Repository</b></em>'. <!--
 * end-user-doc -->
 */
public abstract class RepositoryImpl extends RepositoryImplGen {

    /**
     * The DAO to load/access/store the data handled by this repository.
     */
    protected MetaDao metaDao;

    protected RepositoryImpl() {
        super();
        this.metaDao = null;
    }
    
    /**
     * <!-- begin-user-doc --> Resets the experiment groups attribute. <!-- end-user-doc -->
     *
     */
    @Override
    public void resetExperimentGroups() {
        this.eDynamicSet(RepositoryPackage.REPOSITORY__EXPERIMENT_GROUPS,
                RepositoryPackage.Literals.REPOSITORY__EXPERIMENT_GROUPS,
                new EObjectWithInverseResolvingEList<ExperimentGroup>(ExperimentGroup.class, this,
                        RepositoryPackage.REPOSITORY__EXPERIMENT_GROUPS,
                        ExperimentDataPackage.EXPERIMENT_GROUP__REPOSITORY));
    }


    @Override
    public MeasurementsDaoFactory getMeasurementsDaoFactory() {
        if (this.metaDao != null) {
            return this.metaDao.getMeasurementsDaoFactory();
        } else {
            return null;
        }
    }

    @Override
    public boolean canClose() {
    	return (this.metaDao != null) && this.metaDao.canClose();
    }

    @Override
    public boolean canDelete() {
    	return (this.metaDao != null) && this.metaDao.canDelete();
    }

    @Override
    public boolean canOpen() {
    	return (this.metaDao != null) && this.metaDao.canOpen();
    }

    @Override
    public void close() throws DataNotAccessibleException {
        if (this.metaDao != null) {
            this.metaDao.close();
        }
    }

    @Override
    public void delete() throws DataNotAccessibleException {
        if (this.metaDao != null) {
            this.metaDao.delete();
        }
    }

    @Override
    public boolean isDeleted() {
    	return (this.metaDao != null) && this.metaDao.isDeleted();
    }

    @Override
    public boolean isOpen() {
    	return (this.metaDao != null) && this.metaDao.isOpen();
    }

    @Override
    public void open() throws DataNotAccessibleException {
        if (this.metaDao != null) {
            this.metaDao.open();
        }
    }

    @Override
    public void flush() {
        if (this.metaDao != null) {
            this.metaDao.flush();
        }
    }

} // RepositoryImpl
