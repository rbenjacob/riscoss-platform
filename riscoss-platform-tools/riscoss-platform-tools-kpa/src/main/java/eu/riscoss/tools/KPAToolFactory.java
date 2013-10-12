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
 * DummyToolFactory.java
 *
 * @version $Id$
 */
@Component
@Singleton
@Named(KPAToolFactory.TOOL_ID)
public class KPAToolFactory implements ToolFactory
{
    public static final String TOOL_ID = "kpatool";

    @Inject
    private RISCOSSPlatform riscossPlatform;

    @Override public String getToolId()
    {
        return TOOL_ID;
    }

    @Override public List<ParameterDescription> getToolConfigurationParametersDescriptions()
    {
        return new ArrayList<ParameterDescription>();
    }

    @Override public List<ParameterDescription> getToolExecutionParametersDescriptions()
    {
        return new ArrayList<ParameterDescription>();
    }

    @Override public Tool createTool()
    {
        return new KPATool(riscossPlatform);
    }
}
