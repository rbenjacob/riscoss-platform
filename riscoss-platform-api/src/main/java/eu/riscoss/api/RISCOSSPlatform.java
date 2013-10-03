package eu.riscoss.api;

import java.io.File;
import java.util.List;

import org.xwiki.component.annotation.Role;

import eu.riscoss.api.model.Measurement;

/**
 * RISCOSSPlatform. This interface provides all the methods for accessing all the functionalities provided by the
 * RISCOSS platform. such as methods for storing/retrieving entities stored in the "knowledge base".
 *
 * @version $Id$
 */
@Role
public interface RISCOSSPlatform
{
    /**
     * Retrieve the tool factory for creating instances of a given tool.
     *
     * @param toolId the id of the tool.
     * @return the factory for instantiating the tool.
     */
    ToolFactory getToolFactory(String toolId);

    /**
     * @return all the tool factories registered in the system.
     */
    List<ToolFactory> getToolFactories();

    /**
     * Store a measurement entity object in the platform knowledge base.
     *
     * @param measurement the measurement to be stored.
     */
    void storeMeasurement(Measurement measurement);

    /**
     * Return a directory where to store temporary files.
     *
     * @param namespace the namespace used for isolating temporary files.
     * @return the temporary directory.
     */
    File getTempDirectory(String namespace);
}
