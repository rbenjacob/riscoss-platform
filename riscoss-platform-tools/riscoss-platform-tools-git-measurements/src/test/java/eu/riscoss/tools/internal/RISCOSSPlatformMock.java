package eu.riscoss.tools.internal;

import java.io.File;
import java.util.ArrayList;
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
    protected List<Measurement> measurements = new ArrayList<Measurement>();

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
        measurements.add(measurement);
    }

    @Override public File getTempDirectory(String namespace)
    {
        File tempDirectory = new File(System.getProperty("java.io.tmpdir"), namespace);
        tempDirectory.mkdir();

        return tempDirectory;
    }

    public List<Measurement> getMeasurements()
    {
        return measurements;
    }
}
