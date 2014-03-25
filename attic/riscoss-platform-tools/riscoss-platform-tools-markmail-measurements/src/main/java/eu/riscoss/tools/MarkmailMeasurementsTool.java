package eu.riscoss.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.riscoss.api.RISCOSSPlatform;
import eu.riscoss.api.Tool;
import eu.riscoss.api.model.Measurement;
import eu.riscoss.api.model.Scope;

/**
 * MarkmailMeasurementsTool.
 *
 * @version $Id$
 */
public class MarkmailMeasurementsTool implements Tool
{
    protected static final Logger LOGGER = LoggerFactory.getLogger(MarkmailMeasurementsTool.class);

    protected final RISCOSSPlatform riscossPlatform;

    public static class MarkmailStatistics
    {
        public int postsPerDay;
    }

    public MarkmailMeasurementsTool(RISCOSSPlatform riscossPlatform)
    {
        this.riscossPlatform = riscossPlatform;
        LOGGER.info("Markmail measurements tool initialized");
    }

    @Override
    public void execute(Scope scope, Map<String, String> parameters)
    {
        LOGGER.info(String.format("Running %s on %s", MarkmailMeasurementsToolFactory.TOOL_ID, scope));

        String markmailURI = parameters.get(MarkmailMeasurementsToolFactory.MARKMAIL_URI_PARAMETER);

        if (markmailURI == null) {
            LOGGER.error("Markmail URI is null.");
        }

        try {

            MarkmailMeasurementsTool.MarkmailStatistics statistics = getStatistics(markmailURI);

            if (statistics != null) {
                Measurement measurement = new Measurement();
                measurement.setScope(scope);
                measurement.setType("posts-per-day");
                measurement.setValue(String.format("%.2f", (double) statistics.postsPerDay));
                riscossPlatform.storeMeasurement(measurement);

                LOGGER.info(String.format("Analysis completed [%d]. Results stored", statistics.postsPerDay));
            }
        } catch (Exception e) {
            LOGGER.error(String.format("Error while executing analysis on %s", markmailURI), e);
        }
    }

    @Override
    public Status getStatus()
    {
        return Status.DONE;
    }

    @Override
    public void stop()
    {
        LOGGER.info("Stopping");
    }

    protected MarkmailStatistics getStatistics(String markmailURI) throws Exception
    {

        URL url;

        // get URL content
        url = new URL(markmailURI);
        URLConnection conn = url.openConnection();

        // open the stream and put it into BufferedReader
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String inputLine;
        String postsPerDay = "";
        while ((inputLine = br.readLine()) != null) {

            if (inputLine.contains(" messages per day")) {
                int endIndex = inputLine.indexOf(" messages per day");
                while (!Character.isDigit(inputLine.charAt(endIndex - 1))) {
                    endIndex--;
                }
                int beginIndex = endIndex;
                while (Character.isDigit(inputLine.charAt(beginIndex - 1))) {
                    beginIndex--;
                }
                postsPerDay = inputLine.substring(beginIndex, endIndex);
            }
        }

        br.close();

        MarkmailStatistics stats = new MarkmailStatistics();
        stats.postsPerDay = Integer.parseInt(postsPerDay);

        return stats;
    }
}
