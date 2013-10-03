package eu.riscoss.tools;

import java.io.File;
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
@Named(GitMeasurementsToolFactory.TOOL_ID)
public class GitMeasurementsToolFactory implements ToolFactory, Initializable
{
    public static final String TOOL_ID = "git-measurements";

    protected static final String GIT_PATH_PARAMETER = "gitPath";

    protected static final String BASH_PATH_PARAMETER = "bashPath";

    protected static final String AWK_PATH_PARAMETER = "awkPath";

    protected static final String REPOSITORY_URI_PARAMETER = "repositoryURL";

    @Inject
    protected RISCOSSPlatform riscossPlatform;

    @Inject
    protected ToolConfigurationProvider toolConfigurationProvider;

    protected Map<String, ParameterDescription> configurationParameterDescriptions;

    protected Map<String, ParameterDescription> executionParameterDescriptions;

    @Override public void initialize() throws InitializationException
    {
        configurationParameterDescriptions = new HashMap<String, ParameterDescription>();

        String GIT_DEFAULT_PATH = "/usr/bin/git";
        String BASH_DEFAULT_PATH = "/bin/bash";
        String AWK_DEFAULT_PATH = "/usr/bin/gawk";

        /* Try reasonable defaults */
        configurationParameterDescriptions.put(GIT_PATH_PARAMETER,
                new ParameterDescription(GIT_PATH_PARAMETER, "The path to the git tool",
                        new File(GIT_DEFAULT_PATH).exists() ? GIT_DEFAULT_PATH : null));

        configurationParameterDescriptions.put(BASH_PATH_PARAMETER,
                new ParameterDescription(BASH_PATH_PARAMETER, "The path to the bash tool",
                        new File(BASH_DEFAULT_PATH).exists() ? BASH_DEFAULT_PATH : null));

        configurationParameterDescriptions.put(AWK_PATH_PARAMETER,
                new ParameterDescription(AWK_PATH_PARAMETER, "The path to the awk tool",
                        new File(AWK_DEFAULT_PATH).exists() ? AWK_DEFAULT_PATH : null));

        executionParameterDescriptions = new HashMap<String, ParameterDescription>();

        executionParameterDescriptions.put(
                REPOSITORY_URI_PARAMETER, new ParameterDescription(REPOSITORY_URI_PARAMETER, "Git repository URL"));
    }

    @Override public String getToolId()
    {
        return TOOL_ID;
    }

    @Override public List<ParameterDescription> getToolConfigurationParametersDescriptions()
    {
        List<ParameterDescription> result =
                new ArrayList<ParameterDescription>(configurationParameterDescriptions.values());

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
        /* Set defaults */
        String gitPath = configurationParameterDescriptions.get(GIT_PATH_PARAMETER).getDefaultValue();

        String bashPath = configurationParameterDescriptions.get(BASH_PATH_PARAMETER).getDefaultValue();

        String awkPath = configurationParameterDescriptions.get(AWK_PATH_PARAMETER).getDefaultValue();

        /* Override with configuration values if present */
        Map<String, String> configuration = toolConfigurationProvider.getConfiguration(TOOL_ID);
        if (configuration != null) {
            if (configuration.get(GIT_PATH_PARAMETER) != null) {
                gitPath = configuration.get(GIT_PATH_PARAMETER);
            }

            if (configuration.get(BASH_PATH_PARAMETER) != null) {
                bashPath = configuration.get(BASH_PATH_PARAMETER);
            }

            if (configuration.get(AWK_PATH_PARAMETER) != null) {
                awkPath = configuration.get(AWK_PATH_PARAMETER);
            }
        }

        return new GitMeasurementsTool(gitPath, bashPath, awkPath, riscossPlatform);
    }
}
