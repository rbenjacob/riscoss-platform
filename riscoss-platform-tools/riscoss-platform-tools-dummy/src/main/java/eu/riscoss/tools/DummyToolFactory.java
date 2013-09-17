package eu.riscoss.tools;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;

import eu.riscoss.api.ParameterDescription;
import eu.riscoss.api.RISCOSSPlatform;
import eu.riscoss.api.Tool;
import eu.riscoss.api.ToolFactory;

/**
 * DummyToolFactory.
 *
 * @version $Id$
 */
@Component
@Singleton
@Named(DummyToolFactory.TOOL_ID)
public class DummyToolFactory implements ToolFactory
{
    public static final String TOOL_ID = "dummy";

    @Inject
    private RISCOSSPlatform riscossPlatform;

    @Override public String getToolId()
    {
        return TOOL_ID;
    }

    @Override public List<ParameterDescription> getToolConfigurationParametersDescriptions()
    {
        List<ParameterDescription> result = new ArrayList<ParameterDescription>();

        result.add(new ParameterDescription("git", "The path to the git tool"));

        return result;
    }

    @Override public List<ParameterDescription> getToolExecutionParametersDescriptions()
    {
        List<ParameterDescription> result = new ArrayList<ParameterDescription>();

        result.add(new ParameterDescription("url", "The target URL to analyse"));

        return result;
    }

    @Override public Tool createTool()
    {
        return new DummyTool(riscossPlatform);
    }
}
