/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.palladiosimulator.edp2.models.Repository.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.palladiosimulator.edp2.impl.resource.EmfModelXMIResourceFactoryImpl;
import org.palladiosimulator.edp2.models.Repository.RepositoryPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Repositories</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>
 * {@link org.palladiosimulator.edp2.models.Repository.impl.RepositoriesImpl#getAvailableRepositories
 * <em>Available Repositories</em>}</li>
 * <li>
 * {@link org.palladiosimulator.edp2.models.Repository.impl.RepositoriesImpl#getCommonResourceSet
 * <em>Common Resource Set</em>}</li>
 * </ul>
 * </p>
 */
public class RepositoriesImpl extends RepositoriesImplGen {

    protected RepositoriesImpl() {
        super();
        // DEFAULT: Generate a new resource set by which all EMF resources are managed.
        this.setCommonResourceSet(EmfModelXMIResourceFactoryImpl.createResourceSet());
        // EmfmodelPackageImpl.eINSTANCE.eClass();
    }

   @Override
    public ResourceSet getCommonResourceSet() {
        // Added "commonResourceSet instanceof EObject && " as this returns an error
        if (this.basicGetCommonResourceSet() != null && this.basicGetCommonResourceSet() instanceof EObject
                && ((EObject) this.basicGetCommonResourceSet()).eIsProxy()) {
            final InternalEObject oldCommonResourceSet = (InternalEObject) this.basicGetCommonResourceSet();
            this.setCommonResourceSet((ResourceSet) this.eResolveProxy(oldCommonResourceSet));
            if (this.basicGetCommonResourceSet() != oldCommonResourceSet) {
                if (this.eNotificationRequired()) {
                    this.eNotify(new ENotificationImpl(this, Notification.RESOLVE,
                            RepositoryPackage.REPOSITORIES__COMMON_RESOURCE_SET, oldCommonResourceSet, this
                            .basicGetCommonResourceSet()));
                }
            }
        }
        return this.basicGetCommonResourceSet();
    }
} // RepositoriesImpl
