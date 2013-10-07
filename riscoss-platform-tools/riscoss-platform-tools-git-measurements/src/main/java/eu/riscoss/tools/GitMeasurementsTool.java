package eu.riscoss.tools;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.riscoss.api.RISCOSSPlatform;
import eu.riscoss.api.Tool;
import eu.riscoss.api.model.Measurement;
import eu.riscoss.api.model.Scope;

/**
 * GitMeasurementsTool.
 *
 * @version $Id$
 */
public class GitMeasurementsTool implements Tool
{
    protected static final Logger LOGGER = LoggerFactory.getLogger(GitMeasurementsTool.class);

    protected final RISCOSSPlatform riscossPlatform;

    protected final String gitPath;

    protected final String bashPath;

    protected final String awkPath;

    public static class GitLogStatistics
    {
        public int commits;

        public int filesChanged;

        public int linesAdded;

        public int linesRemoved;
    }

    public GitMeasurementsTool(String gitPath, String bashPath, String awkPath, RISCOSSPlatform riscossPlatform)
    {
        if (gitPath == null) {
            throw new IllegalArgumentException("No git path provided");
        }

        if (bashPath == null) {
            throw new IllegalArgumentException("No bash path provided");
        }

        if (awkPath == null) {
            throw new IllegalArgumentException("No awk path provided");
        }

        this.gitPath = gitPath;
        this.bashPath = bashPath;
        this.awkPath = awkPath;
        this.riscossPlatform = riscossPlatform;
    }

    @Override public void execute(Scope scope, Map<String, String> parameters)
    {
        LOGGER.info(String.format("Running %s on %s", GitMeasurementsToolFactory.TOOL_ID, scope));

        File tempDir = riscossPlatform.getTempDirectory(GitMeasurementsToolFactory.TOOL_ID);
        String repositoryURI = parameters.get(GitMeasurementsToolFactory.REPOSITORY_URI_PARAMETER);

        if (repositoryURI == null) {
            LOGGER.error("Repository URI is null.");
        }

        String repositoryName = new File(repositoryURI).getName();
        File destination = new File(tempDir, repositoryName);

        try {
            if (!destination.exists()) {
                cloneRepository(repositoryURI, destination);
            } else {
                updateRepository(destination);
            }

            GitMeasurementsTool.GitLogStatistics statistics = getStatistics(destination);

            if (statistics != null) {
                Measurement measurement = new Measurement();
                measurement.setScope(scope);
                measurement.setType("files-changed-per-commit");
                measurement.setValue(
                        String.format("%.2f", (double) statistics.filesChanged / (double) statistics.commits));
                riscossPlatform.storeMeasurement(measurement);

                measurement = new Measurement();
                measurement.setScope(scope);
                measurement.setType("lines-added-per-commit");
                measurement
                        .setValue(String.format("%.2f", (double) statistics.linesAdded / (double) statistics.commits));
                riscossPlatform.storeMeasurement(measurement);

                measurement = new Measurement();
                measurement.setScope(scope);
                measurement.setType("lines-removed-per-commit");
                measurement.setValue(
                        String.format("%.2f", (double) statistics.linesRemoved / (double) statistics.commits));
                riscossPlatform.storeMeasurement(measurement);

                LOGGER.info(String.format("Analysis completed [%d, %d, %d, %d]. Results stored", statistics.commits,
                        statistics.filesChanged, statistics.linesAdded, statistics.linesRemoved));
            }
        } catch (Exception e) {
            LOGGER.error(String.format("Error while executing analysis on %s", repositoryURI), e);
        }
    }

    @Override public Status getStatus()
    {
        return Status.DONE;
    }

    @Override public void stop()
    {
        LOGGER.info("Stopping");
    }

    protected GitMeasurementsTool.GitLogStatistics getStatistics(File repository) throws Exception
    {
        String[] cmd = {
                bashPath,
                "-c",
                String.format(
                        "cd %s;  %s log --pretty=oneline --shortstat | %s '/^ / { commits++; files_changed += $1; lines_added += $4; lines_removed += $6 } END {printf(\"OK %%i %%i %%i %%i\", commits, files_changed, lines_added, lines_removed)}'",
                        repository, gitPath, awkPath)
        };

        Process p = Runtime.getRuntime().exec(cmd);
        int result = p.waitFor();

        if (result != 0) {
            return null;
        }

        InputStream inputStream = p.getInputStream();
        String output = IOUtils.toString(inputStream);
        String[] components = output.split(" ");
        if ("OK".equals(components[0])) {
            GitMeasurementsTool.GitLogStatistics stats = new GitMeasurementsTool.GitLogStatistics();
            stats.commits = Integer.parseInt(components[1]);
            stats.filesChanged = Integer.parseInt(components[2]);
            stats.linesAdded = Integer.parseInt(components[3]);
            stats.linesRemoved = Integer.parseInt(components[4]);

            return stats;
        }

        return null;
    }

    protected boolean cloneRepository(String repositoryURI, File destination) throws Exception
    {
        String[] cmd = {
                gitPath,
                "clone",
                repositoryURI.toString(),
                destination.toString()
        };

        Process p = Runtime.getRuntime().exec(cmd);
        int result = p.waitFor();

        return result == 0;
    }

    protected boolean updateRepository(File repository) throws Exception
    {
        String[] cmd = {
                gitPath,
                "pull",
                repository.toString()
        };

        Process p = Runtime.getRuntime().exec(cmd);
        int result = p.waitFor();

        return result == 0;
    }
}
