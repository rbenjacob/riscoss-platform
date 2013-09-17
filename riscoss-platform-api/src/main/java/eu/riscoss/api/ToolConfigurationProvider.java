package eu.riscoss.api;

import java.util.Map;

import org.xwiki.component.annotation.Role;

/**
 * ToolConfigurationProvider.
 *
 * @version $Id$
 */
@Role
public interface ToolConfigurationProvider
{
    Map<String, String> getConfiguration(String toolId);
}
