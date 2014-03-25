package eu.riscoss.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

import eu.riscoss.api.ParameterDescription;
import eu.riscoss.api.RISCOSSPlatform;
import eu.riscoss.api.Tool;
import eu.riscoss.api.ToolConfigurationProvider;
import eu.riscoss.api.ToolFactory;

/**
 * MarkmailMeasurementsToolFactory.
 *
 * @version $Id$
 */
@Component
@Singleton
@Named(MarkmailMeasurementsToolFactory.TOOL_ID)
public class MarkmailMeasurementsToolFactory implements ToolFactory, Initializable
{
    public static final String TOOL_ID = "markmail-measurements";

    protected static final String MARKMAIL_URI_PARAMETER = "markmailURL";

    @Inject
    protected RISCOSSPlatform riscossPlatform;

    @Inject
    protected ToolConfigurationProvider toolConfigurationProvider;

    protected Map<String, ParameterDescription> configurationParameterDescriptions;

    protected Map<String, ParameterDescription> executionParameterDescriptions;

    @Override
    public void initialize() throws InitializationException
    {
        configurationParameterDescriptions = new HashMap<String, ParameterDescription>();

        executionParameterDescriptions = new HashMap<String, ParameterDescription>();

        executionParameterDescriptions.put(MARKMAIL_URI_PARAMETER, new ParameterDescription(MARKMAIL_URI_PARAMETER,
                "Markmail URL"));
    }

    @Override
    public String getToolId()
    {
        return TOOL_ID;
    }

    @Override
    public List<ParameterDescription> getToolConfigurationParametersDescriptions()
    {
        List<ParameterDescription> result =
                new ArrayList<ParameterDescription>(configurationParameterDescriptions.values());

        return result;
    }

    @Override
    public List<ParameterDescription> getToolExecutionParametersDescriptions()
    {
        List<ParameterDescription> result =
                new ArrayList<ParameterDescription>(executionParameterDescriptions.values());

        return result;
    }

    @Override
    public Tool createTool()
    {
        return new MarkmailMeasurementsTool(riscossPlatform);
    }
}
