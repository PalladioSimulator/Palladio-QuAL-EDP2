/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.palladiosimulator.edp2.local.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.palladiosimulator.edp2.dao.MetaDaoDelegate;
import org.palladiosimulator.edp2.dao.exception.DataNotAccessibleException;
import org.palladiosimulator.edp2.local.LocalDirectoryRepository;
import org.palladiosimulator.edp2.local.localPackage;
import org.palladiosimulator.edp2.models.Repository.impl.RepositoryImpl;
import org.palladiosimulator.edp2.repository.local.dao.LocalDirectoryMetaDao;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Local Directory Repository</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.palladiosimulator.edp2.local.impl.LocalDirectoryRepositoryImpl#getUri <em>Uri
 * </em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class LocalDirectoryRepositoryImpl extends RepositoryImpl implements LocalDirectoryRepository {

    /**
     * The default value of the '{@link #getUri() <em>Uri</em>}' attribute. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @see #getUri()
     * @generated
     * @ordered
     */
    protected static final String URI_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getUri() <em>Uri</em>}' attribute. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @see #getUri()
     * @generated
     * @ordered
     */
    protected String uri = URI_EDEFAULT;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    protected LocalDirectoryRepositoryImpl() {
        super();
        final MetaDaoDelegate delegate = new LocalDirectoryMetaDao();
        delegate.setParent(this);

        this.metaDao = delegate;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return localPackage.Literals.LOCAL_DIRECTORY_REPOSITORY;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public String getUri() {
        return this.uri;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated not
     */
    @Override
    public void setUri(final String newUri) {
        final String oldUri = this.uri;
        this.uri = newUri;
        this.setId(newUri);
        if (this.eNotificationRequired()) {
            this.eNotify(new ENotificationImpl(this, Notification.SET, localPackage.LOCAL_DIRECTORY_REPOSITORY__URI,
                    oldUri,
                    this.uri));
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @throws DataNotAccessibleException
     * @generated NOT
     */
    @Override
    public File convertUriStringToFile(final String uriString) throws DataNotAccessibleException {
        return this.convertUriStringToFileInternal(uriString);
    }

    /**
     * Converts a supplied URI to a file on the local file system, if possible.
     *
     * @param uri
     *            The URI to convert.
     * @return Local file.
     * @throws DataNotAccessibleException
     *             For conversion errors. Details are provided in the message.
     */
    private File convertUriStringToFileInternal(final String uriString) throws DataNotAccessibleException {
        final URI uri = URI.createURI(uriString);
        File directory;
        String fileLocation;
        if (uri.isPlatform()) {
            URL urlToFoo = null;
            try {
                urlToFoo = FileLocator.toFileURL(new URL(uri.toString()));
                fileLocation = urlToFoo.getFile();
            } catch (final MalformedURLException e) {
                throw new DataNotAccessibleException("The URI is not well-formed.", e);
            } catch (final IOException e) {
                throw new DataNotAccessibleException("The URI could not be converted.", e);
            }
        } else {
            fileLocation = uri.toFileString();
        }
        if (fileLocation == null) {
            // URI is valid but does not point to a file
            throw new DataNotAccessibleException("The URI could not be converted to a local file.", null);
        } else {
            directory = new File(fileLocation);
            if (!directory.isDirectory()) {
                // URI does not point to a directory.
                throw new DataNotAccessibleException("The URI does not point to a directory.", null);
            }
        }
        return directory;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public Object eGet(final int featureID, final boolean resolve, final boolean coreType) {
        switch (featureID)
        {
        case localPackage.LOCAL_DIRECTORY_REPOSITORY__URI:
            return this.getUri();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public void eSet(final int featureID, final Object newValue) {
        switch (featureID)
        {
        case localPackage.LOCAL_DIRECTORY_REPOSITORY__URI:
            this.setUri((String) newValue);
            return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public void eUnset(final int featureID) {
        switch (featureID)
        {
        case localPackage.LOCAL_DIRECTORY_REPOSITORY__URI:
            this.setUri(URI_EDEFAULT);
            return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public boolean eIsSet(final int featureID) {
        switch (featureID)
        {
        case localPackage.LOCAL_DIRECTORY_REPOSITORY__URI:
            return URI_EDEFAULT == null ? this.uri != null : !URI_EDEFAULT.equals(this.uri);
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public String toString()
    {
        if (this.eIsProxy()) {
            return super.toString();
        }

        final StringBuffer result = new StringBuffer(super.toString());
        result.append(" (uri: ");
        result.append(this.uri);
        result.append(')');
        return result.toString();
    }

} // LocalDirectoryRepositoryImpl
