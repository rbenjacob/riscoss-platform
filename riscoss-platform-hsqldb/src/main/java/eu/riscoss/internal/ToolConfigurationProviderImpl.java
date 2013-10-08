package eu.riscoss.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

import eu.riscoss.api.ToolConfigurationProvider;

/**
 * ToolConfigurationProvider.
 *
 * @version $Id$
 */
@Component
@Singleton
public class ToolConfigurationProviderImpl implements ToolConfigurationProvider, Initializable
{
    @Inject
    private Logger logger;

    private Properties configurationProperties;

    public ToolConfigurationProviderImpl()
    {
    }

    @Override public void initialize() throws InitializationException
    {
        File configurationPropertiesFile;

        if (System.getProperty("riscoss.config") != null) {
            configurationPropertiesFile = new File(System.getProperty("riscoss.config"));
        } else {
            configurationPropertiesFile = new File(System.getProperty("user.dir"), "riscoss.properties");
        }

        configurationProperties = new Properties();
        if (configurationPropertiesFile.exists()) {
            try {
                configurationProperties.load(new FileInputStream(configurationPropertiesFile));
            } catch (IOException e) {
                logger.error(String.format("Error while loading properties from %s", configurationPropertiesFile), e);
            }
        } else {
            logger.warn(String.format("Property file %s doesn't exist", configurationPropertiesFile));
        }
    }

    public Map<String, String> getConfiguration(String toolId)
    {
        Map<String, String> result = new HashMap<String, String>();

        for (Map.Entry entry : configurationProperties.entrySet()) {
            String key = (String) entry.getKey();
            if (key.startsWith(toolId + ".")) {
                result.put(key.substring(toolId.length() + 1), entry.getValue().toString());
            }
        }

        return result;
    }
}
