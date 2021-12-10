package org.palladiosimulator.edp2.visualization.properties.sections;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;

/**
 * @see http://eclipsesource.com/blogs/2013/01/29/emf-itemprovider-magic/
 * @author Sebastian Lehrig
 */
public final class EMFAdapterFactoryHelper {

    public static final AdapterFactory ADAPTER_FACTORY = new ComposedAdapterFactory(
            ComposedAdapterFactory.Descriptor.Registry.INSTANCE);

    public static final AdapterFactoryLabelProvider ADAPTER_FACTORY_LABEL_PROVIDER = new AdapterFactoryLabelProvider(
            ADAPTER_FACTORY);

    public static final AdapterFactoryContentProvider ADPATER_FACTORY_CONTENT_PROVIDER = new AdapterFactoryContentProvider(
            ADAPTER_FACTORY);

    /** Static helper classes shall not be instantiated. */
    private EMFAdapterFactoryHelper() {
    }
}
