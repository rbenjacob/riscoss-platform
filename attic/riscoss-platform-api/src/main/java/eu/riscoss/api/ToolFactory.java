package eu.riscoss.api;

import java.util.List;

import org.xwiki.component.annotation.Role;

/**
 * ToolFactory.
 *
 * @version $Id$
 */
@Role
public interface ToolFactory
{
    /**
     * @return the id of the tool created by this factory.
     */
    String getToolId();

    /**
     * Returns the description for the configuration parameters. These parameters are used to initialize the tool before
     * it is executed.
     *
     * @return a list of parameter descriptions.
     */
    List<ParameterDescription> getToolConfigurationParametersDescriptions();

    /**
     * Returns the description for the execution parameters. These parameters are used to customize the execution of
     * tool on a given target.
     *
     * @return a list of parameter descriptions.
     */
    List<ParameterDescription> getToolExecutionParametersDescriptions();

    /**
     * @return an initialized tool instance ready to be executed.
     */
    Tool createTool();
}
