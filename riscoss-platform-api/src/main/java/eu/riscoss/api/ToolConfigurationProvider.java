package eu.riscoss.api;

import java.util.Map;

import org.xwiki.component.annotation.Role;

/**
 * The ToolConfigurationProvider provides a way for retrieving the configuration parameters for initializing a tool.
 * These parameters are persisted in the RISCOSS knowledge base.
 *
 * @version $Id$
 */
@Role
public interface ToolConfigurationProvider
{
    /**
     * @param toolId the id of the tool.
     * @return the configuration parameters for the corresponding tool.
     */
    Map<String, String> getConfiguration(String toolId);
}
