package org.palladiosimulator.edp2.impl.resource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentDataPackage;
import org.palladiosimulator.edp2.models.ExperimentData.impl.ExperimentDataPackageImpl;
import org.palladiosimulator.edp2.models.Repository.RepositoryPackage;

public class EmfModelXMIResourceFactoryImpl extends XMIResourceFactoryImpl {
    /** File extension for files containing an ExperimentGroup. */
    public static final String EDP2_EXPERIMENT_GROUP_EXTENSION = "edp2";
    /** File extension for files containing Descriptions. */
    public static final String EDP2_DESCRIPTIONS_EXTENSION = "edp2desc";
    /** File extension for files containing NominalMeasurements. */
    public static final String EDP2_NOMINALMEASUREMENTS_EXTENSION = "edp2nm";

    public EmfModelXMIResourceFactoryImpl() {
        super();
    }

    @Override
    public Resource createResource(final URI uri) {
        return new EmfModelResourceImpl(uri);
    }

    /**
     * Creates and initializes a resource set for edp2 use.
     * 
     * @return New resource set.
     */
    public static ResourceSet createResourceSet() {
        final ResourceSet resourceSet = new ResourceSetImpl();
        final EmfModelXMIResourceFactoryImpl resourceFactoryImpl = new EmfModelXMIResourceFactoryImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
                .put(EmfModelXMIResourceFactoryImpl.EDP2_EXPERIMENT_GROUP_EXTENSION, resourceFactoryImpl);
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
                .put(EmfModelXMIResourceFactoryImpl.EDP2_DESCRIPTIONS_EXTENSION, resourceFactoryImpl);
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
                .put(EmfModelXMIResourceFactoryImpl.EDP2_NOMINALMEASUREMENTS_EXTENSION, resourceFactoryImpl);
        resourceSet.getPackageRegistry().put(ExperimentDataPackageImpl.eNS_URI, ExperimentDataPackage.eINSTANCE);
        resourceSet.getPackageRegistry().put(RepositoryPackage.eNS_URI, RepositoryPackage.eINSTANCE);
        return resourceSet;
    }
}
