package eu.riscoss.internal;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.query.Query;
import org.xwiki.query.QueryManager;

import eu.riscoss.api.ToolConfigurationProvider;

/**
 * ToolConfigurationProvider.
 *
 * @version $Id$
 */
@Component
@Singleton
public class XWikiToolConfigurationProvider implements ToolConfigurationProvider
{
    @Inject
    private Logger logger;

    @Inject
    private QueryManager queryManager;

    public XWikiToolConfigurationProvider()
    {
    }

    public Map<String, String> getConfiguration(String toolId)
    {
        Map<String, String> result = new HashMap<String, String>();

        Properties properties = new Properties();

        try {
            List<Object> configurations = queryManager.createQuery(
                    "select obj.configuration from Document doc, doc.object(RISCOSSPlatform.ToolConfigurationClass) as obj where obj.id = :id",
                    Query.XWQL).bindValue("id", toolId).execute();

            String configuration = (String) configurations.get(0);

            properties.load(new StringReader(configuration));

            for (Map.Entry entry : properties.entrySet()) {
                result.put(entry.getKey().toString(), entry.getValue().toString());
            }
        } catch (Exception e) {
            /* Just log the exception and return an empty configuration */
            logger.error(String.format("Error getting configuration for tool %s", toolId), e);
        }

        return result;
    }
}
