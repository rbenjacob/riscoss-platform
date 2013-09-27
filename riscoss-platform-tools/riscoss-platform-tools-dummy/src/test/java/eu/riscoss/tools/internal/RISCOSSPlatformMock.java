package eu.riscoss.tools.internal;

import java.util.List;

import org.xwiki.component.annotation.Component;

import eu.riscoss.api.RISCOSSPlatform;
import eu.riscoss.api.ToolFactory;
import eu.riscoss.api.model.Measurement;

/**
 * RISCOSSPlatformMock.
 *
 * @version $Id$
 */
@Component
public class RISCOSSPlatformMock implements RISCOSSPlatform
{
    @Override public ToolFactory getToolFactory(String type)
    {
        return null;
    }

    @Override public List<ToolFactory> getToolFactories()
    {
        return null;
    }

    @Override public void storeMeasurement(Measurement measurement)
    {
    }
}
