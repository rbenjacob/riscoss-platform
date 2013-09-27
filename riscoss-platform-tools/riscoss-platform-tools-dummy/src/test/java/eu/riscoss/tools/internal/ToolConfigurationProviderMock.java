package eu.riscoss.tools.internal;

import java.util.HashMap;
import java.util.Map;

import org.xwiki.component.annotation.Component;

import eu.riscoss.api.ToolConfigurationProvider;

/**
 * ToolConfigurationProviderMock.
 *
 * @version $Id$
 */
@Component
public class ToolConfigurationProviderMock implements ToolConfigurationProvider
{
    @Override public Map<String, String> getConfiguration(String type)
    {
        /* Here we can return whatever we want for our tests. */
        return new HashMap<String, String>();
    }
}
