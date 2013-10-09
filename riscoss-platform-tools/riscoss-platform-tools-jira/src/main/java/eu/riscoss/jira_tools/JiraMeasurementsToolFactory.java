package eu.riscoss.jira_tools;

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
 * GitMeasurementsToolFactory.
 * 
 * @version $Id$
 */
@Component
@Singleton
@Named(JiraMeasurementsToolFactory.TOOL_ID)
public class JiraMeasurementsToolFactory implements ToolFactory, Initializable
{
    public static final String TOOL_ID = "jira-measurements";

    protected static final String JIRA_URL_PARAMETER = "jiraURL";

    protected static final String JIRA_ANONYMOUS_AUTHENTICATION_PARAMETER = "anonymousAuthentication";

    protected static final String JIRA_USERNAME_PARAMETER = "username";

    protected static final String JIRA_PASSWORD_PARAMETER = "password";

    protected static final String INITIAL_DATE_PARAMETER = "initialDate";

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

        executionParameterDescriptions
            .put(JIRA_URL_PARAMETER, new ParameterDescription(JIRA_URL_PARAMETER, "jira URL"));

        executionParameterDescriptions.put(JIRA_ANONYMOUS_AUTHENTICATION_PARAMETER, new ParameterDescription(
            JIRA_ANONYMOUS_AUTHENTICATION_PARAMETER, "anonymous authentication"));

        executionParameterDescriptions.put(JIRA_USERNAME_PARAMETER, new ParameterDescription(JIRA_USERNAME_PARAMETER,
            "username to login into jira"));

        executionParameterDescriptions.put(JIRA_PASSWORD_PARAMETER, new ParameterDescription(JIRA_PASSWORD_PARAMETER,
            "password to login into jira"));

        executionParameterDescriptions.put(INITIAL_DATE_PARAMETER, new ParameterDescription(INITIAL_DATE_PARAMETER,
            "initial date to perform analysis (yyyy/mm/dd)"));
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
        return new JiraMeasurementsTool(riscossPlatform);
    }
}
