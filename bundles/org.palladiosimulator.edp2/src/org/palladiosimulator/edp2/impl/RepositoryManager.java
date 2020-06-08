/**
 *
 */
package org.palladiosimulator.edp2.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.palladiosimulator.edp2.dao.exception.DataNotAccessibleException;
import org.palladiosimulator.edp2.models.Repository.Repositories;
import org.palladiosimulator.edp2.models.Repository.Repository;
import org.palladiosimulator.edp2.models.Repository.RepositoryFactory;

/**
 * Utility class to manage repositories.
 *
 * @author groenda, Sebastian Lehrig
 *
 */
public class RepositoryManager {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(RepositoryManager.class.getCanonicalName());

    /** Central instance of a repository (singleton). */
    private static Repositories centralRepository = RepositoryFactory.eINSTANCE.createRepositories();

    /**
     * Adds a repository to the list of repositories. The repository is automatically opened, if
     * possible.
     *
     * @param repos
     *            The currently available repositories.
     * @param newRepo
     *            The new repository to add.
     */
    public static void addRepository(final Repositories repos, final Repository newRepo) {
        repos.getAvailableRepositories().add(newRepo);
        if (newRepo.canOpen()) {
            try {
                newRepo.open();
            } catch (final DataNotAccessibleException e) {
                LOGGER.log(Level.WARNING, "Could not open repository after adding it to repositories.", e);
            }
        }
    }

    /**
     * Removes a repository from the given list of the available repositories. The repository is
     * automatically closed, if possible.
     *
     * @param repos
     *            The currently available repositories.
     * @param repo
     *            The repository to remove.
     */
    public static void removeRepository(final Repositories repos, final Repository repo) {
        assert (repos.getAvailableRepositories().indexOf(repo) > -1); // repo must be in list
        if (repo.canClose()) {
            try {
                repo.close();
            } catch (final DataNotAccessibleException e) {
                LOGGER.log(Level.WARNING, "Could not close repository before removing it from the list.", e);
            }
        }
        repos.getAvailableRepositories().remove(repo);
        LOGGER.log(Level.INFO, "Repository is removed.");
    }

    /**
     * Provides access to a central repository. Convenience function to provide access to a
     * singleton which can then be managed.
     *
     * @return Reference to the central repository.
     */
    public static Repositories getCentralRepository() {
        return centralRepository;
    }

    /**
     * Returns Repository with given UUID from central repository.
     *
     * @return Reference to the repository with given UUID. Null if not found.
     */
    public static Repository getRepositoryFromUUID(final String uuid) {
        for (final Repository repository : centralRepository.getAvailableRepositories()) {
            if (repository.getId().equals(uuid)) {
                return repository;
            }
        }
        return null;
    }
}
