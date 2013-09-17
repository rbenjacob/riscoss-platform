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
    String getToolId();

    List<ParameterDescription> getToolConfigurationParametersDescriptions();

    List<ParameterDescription> getToolExecutionParametersDescriptions();

    Tool createTool();
}
