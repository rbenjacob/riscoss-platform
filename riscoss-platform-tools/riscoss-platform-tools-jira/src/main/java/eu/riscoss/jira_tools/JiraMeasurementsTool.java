package eu.riscoss.jira_tools;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.auth.AnonymousAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import eu.riscoss.api.RISCOSSPlatform;
import eu.riscoss.api.Tool;
import eu.riscoss.api.model.Measurement;
import eu.riscoss.api.model.Scope;

/**
 * GitMeasurementsTool.
 * 
 * @version $Id$
 */
public class JiraMeasurementsTool implements Tool
{
    protected static final Logger LOGGER = LoggerFactory.getLogger(JiraMeasurementsTool.class);

    protected final RISCOSSPlatform riscossPlatform;

    private final static int maxBufferIssue = 250;

    private final long secondsOfDay = 86400;

    public static class JiraLogStatistics
    {
        public int openBugs;

        public double timeToResolveABug;

        public double timeToResolveABlockingOrCriticalBug;

    }

    public JiraMeasurementsTool(RISCOSSPlatform riscossPlatform)
    {
        this.riscossPlatform = riscossPlatform;
    }

    @Override
    public void execute(Scope scope, Map<String, String> parameters)
    {
        LOGGER.info(String.format("Running %s on %s", JiraMeasurementsToolFactory.TOOL_ID, scope));

        String jiraURL = parameters.get(JiraMeasurementsToolFactory.JIRA_URL_PARAMETER);
        boolean anonymousAuthentication =
            Boolean.parseBoolean(parameters.get(JiraMeasurementsToolFactory.JIRA_ANONYMOUS_AUTHENTICATION_PARAMETER));
        String username = parameters.get(JiraMeasurementsToolFactory.JIRA_USERNAME_PARAMETER);
        String password = parameters.get(JiraMeasurementsToolFactory.JIRA_PASSWORD_PARAMETER);
        String initialDate = parameters.get(JiraMeasurementsToolFactory.INITIAL_DATE_PARAMETER);

        if (jiraURL == null) {
            LOGGER.error("jira URL is null.");
        }

        JiraMeasurementsTool.JiraLogStatistics statistics = null;
        try {
            statistics = getStatistics(jiraURL, anonymousAuthentication, username, password, initialDate);
        } catch (URISyntaxException e) {
            LOGGER.error(String.format("Error while executing analysis on %s", jiraURL), e);
        }

        if (statistics != null) {
            Measurement measurement = new Measurement();
            measurement.setScope(scope);
            measurement.setType("open-bugs");
            measurement.setValue(Integer.toString(statistics.openBugs));
            riscossPlatform.storeMeasurement(measurement);

            measurement = new Measurement();
            measurement.setScope(scope);
            measurement.setType("time-to-resolve-a-bug");
            measurement.setValue(String.format("%.2f", statistics.timeToResolveABug));
            riscossPlatform.storeMeasurement(measurement);

            measurement = new Measurement();
            measurement.setScope(scope);
            measurement.setType("time-to-resolve-a-blocking-or-critical-bug");
            measurement.setValue(String.format("%.2f", statistics.timeToResolveABlockingOrCriticalBug));
            riscossPlatform.storeMeasurement(measurement);

            LOGGER.info(String.format("Analysis completed [%d, %d, %d]. Results stored", statistics.openBugs,
                statistics.timeToResolveABug, statistics.timeToResolveABlockingOrCriticalBug));
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

    protected JiraMeasurementsTool.JiraLogStatistics getStatistics(String jiraURL, boolean anonymousAuthentication,
        String username, String password, String initialDate) throws URISyntaxException
    {

        int issueIndex = 0;
        int totalIssues;
        int numberOfOpenBugs = 0;
        double totalCriticalBugFixTime = 0;
        int counterCriticalBugs = 0;
        double totalBugFixTime = 0;
        int counterCloseBugs = 0;
        boolean correctExecution = true;
        Issue is;
        String issueBug;
        Interval interval;

        final JiraRestClientFactory jiraRestFactory = new AsynchronousJiraRestClientFactory();
        final JiraRestClient restClient;

        /*
         * Type Authentication instance
         */
        if (anonymousAuthentication) {
            restClient = jiraRestFactory.create(new URI(jiraURL), new AnonymousAuthenticationHandler());
        } else {
            restClient = jiraRestFactory.createWithBasicHttpAuthentication(new URI(jiraURL), username, password);
        }

        final IssueRestClient client = restClient.getIssueClient();

        SearchRestClient searchClient = restClient.getSearchClient();
        final String jql = "created >= \"" + initialDate + "\"";
        SearchResult results = searchClient.searchJql(jql).claim();

        /*
         * Calculate Total Issues
         */
        totalIssues = results.getTotal();

        while (issueIndex < totalIssues) {
            results = searchClient.searchJql(jql, maxBufferIssue, issueIndex, null).claim();
            for (final BasicIssue issue : results.getIssues()) {
                is = client.getIssue(issue.getKey()).claim();

                if (is != null) {
                    if (is.getIssueType().getName().toUpperCase().equals("BUG")) {
                        issueBug = is.getStatus().getName().toUpperCase();

                        /*
                         * Bug open if -->status !closed and ! done
                         */
                        if ((!issueBug.equals(IssueStatus.CLOSED.toString()))
                            && (!issueBug.equals(IssueStatus.DONE.toString()))) {
                            numberOfOpenBugs++;
                        }

                        /*
                         * Bug close if--> status closed or status done or status resolved Number of days= date of bug
                         * create - date of bug last update
                         */
                        if ((issueBug.equals(IssueStatus.CLOSED.toString()))
                            || (issueBug.equals(IssueStatus.RESOLVED.toString()))
                            || (issueBug.equals(IssueStatus.DONE.toString()))) {

                            interval = new Interval(is.getCreationDate(), is.getUpdateDate());
                            totalBugFixTime += (double) interval.toDuration().getStandardSeconds() / secondsOfDay;
                            counterCloseBugs++;

                            /*
                             * close bug with Priority CRITICAL OR BLOCKER
                             */
                            if (is.getPriority() != null) {
                                if ((is.getPriority().getName().toUpperCase().equals(IssuePriority.CRITICAL.toString()))
                                    || (is.getPriority().getName().toUpperCase().equals(IssuePriority.BLOCKER
                                        .toString()))) {
                                    totalCriticalBugFixTime +=
                                        (double) interval.toDuration().getStandardSeconds() / secondsOfDay;
                                    counterCriticalBugs++;
                                }
                            }

                        } else {
                        }

                    }
                    issueIndex++;
                }
            }
        }

        if (correctExecution) {
            JiraMeasurementsTool.JiraLogStatistics stats = new JiraMeasurementsTool.JiraLogStatistics();
            stats.openBugs = numberOfOpenBugs;
            stats.timeToResolveABug = totalBugFixTime / counterCloseBugs;
            stats.timeToResolveABlockingOrCriticalBug = totalCriticalBugFixTime / counterCriticalBugs;
            return stats;
        }

        return null;
    }

}
