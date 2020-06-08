package org.palladiosimulator.edp2.impl.resource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

public class EmfModelResourceImpl extends XMIResourceImpl {
    public EmfModelResourceImpl() {
        super();
    }

    public EmfModelResourceImpl(URI uri) {
        super(uri);
    }

    @Override
    protected boolean useUUIDs() {
        // return true;
        return false;
    }

}
