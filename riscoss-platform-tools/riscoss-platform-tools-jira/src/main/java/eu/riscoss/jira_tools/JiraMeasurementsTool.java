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
import com.atlassian.jira.rest.client.api.ProjectRestClient;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.Version;
import com.atlassian.jira.rest.client.auth.AnonymousAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import eu.riscoss.api.RISCOSSPlatform;
import eu.riscoss.api.Tool;
import eu.riscoss.api.model.Measurement;
import eu.riscoss.api.model.Scope;

/**
 * JiraMeasurementsTool.
 * 
 * @version $Id$
 */
public class JiraMeasurementsTool implements Tool
{
    protected static final Logger LOGGER = LoggerFactory.getLogger(JiraMeasurementsTool.class);

    protected final RISCOSSPlatform riscossPlatform;

    private final static int maxBufferIssue = 250;

    private final long secondsOfDay = 86400;

    private final double security_factor = 0.03;

    public static class JiraLogStatistics
    {
        public int openBugs;

        public double timeToResolveABug;

        public double timeToResolveABlockingOrCriticalBug;

        public int numberOfFeatureRequests;

        public int numberOfOpenFeatureRequests;

        public double numberOfClosedFeatureRequestsPerUpdate;

        public double numberOfClosedBugsPerUpdate;

        public boolean presenceOfSecurityBugsCorrected;

        public int numberOfSecurityBugs;

        public double timeToResolveASecurityBug;

    }

    /**
     * Creates a new JiraMeasurementTool
     * 
     * @param riscossPlatform the riscoss platform
     */
    public JiraMeasurementsTool(RISCOSSPlatform riscossPlatform)
    {
        this.riscossPlatform = riscossPlatform;
    }

    /**
     * Stores all the measures obtained from monitoring.
     * 
     * @param scope target of the measures.
     * @param statistics results of monitoring.
     */
    private void storeAllMeasures(Scope scope, JiraMeasurementsTool.JiraLogStatistics statistics)
    {
        storeMeasure(scope, "open-bugs", Integer.toString(statistics.openBugs));
        storeMeasure(scope, "time-to-resolve-a-bug", String.format("%.2f", statistics.timeToResolveABug));
        storeMeasure(scope, "time-to-resolve-a-blocker-or-critical-bug",
            String.format("%.2f", statistics.timeToResolveABlockingOrCriticalBug));
        storeMeasure(scope, "number-of-feature-requests", Integer.toString(statistics.numberOfFeatureRequests));
        storeMeasure(scope, "number-of-closed-feature-requests-per-date",
            Double.toString(statistics.numberOfClosedFeatureRequestsPerUpdate));
        storeMeasure(scope, "number-of-open-feature-requests", Integer.toString(statistics.numberOfOpenFeatureRequests));
        storeMeasure(scope, "number-of-closed-bugs-per-update", Double.toString(statistics.numberOfClosedBugsPerUpdate));
        storeMeasure(scope, "presence-of-security-bugs-corrected",
            Boolean.toString(statistics.presenceOfSecurityBugsCorrected));
        storeMeasure(scope, "number-of-security-bugs", Integer.toString(statistics.numberOfSecurityBugs));
        storeMeasure(scope, "time-to-resolve-a-security-bug",
            String.format("%2f", statistics.timeToResolveASecurityBug));

        LOGGER.info(String.format("Analysis completed [%d, %f, %f,%d,%f,%d,%f,%b,%d,%f]. Results stored",
            statistics.openBugs, statistics.timeToResolveABug, statistics.timeToResolveABlockingOrCriticalBug,
            statistics.numberOfFeatureRequests, statistics.numberOfClosedFeatureRequestsPerUpdate,
            statistics.numberOfOpenFeatureRequests, statistics.numberOfClosedBugsPerUpdate,
            statistics.presenceOfSecurityBugsCorrected, statistics.numberOfSecurityBugs,
            statistics.timeToResolveASecurityBug));
    }

    /**
     * Stores a single measurement
     * 
     * @param scope target of the measure
     * @param type type of the measure
     * @param value value of the measure
     */
    private void storeMeasure(Scope scope, String type, String value)
    {
        Measurement measurement = new Measurement();
        measurement.setScope(scope);
        measurement.setType(type);
        measurement.setValue(value);
        riscossPlatform.storeMeasurement(measurement);
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
            storeAllMeasures(scope, statistics);
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

    /**
     * Calculates the statistic of jira
     * 
     * @param jiraURL url of Jira
     * @param anonymousAuthentication true if the authentication is anonymous, false otherwise.
     * @param username the username to log in Jira
     * @param password the password to log in Jira
     * @param initialDate the initial date to perform the measures.
     * @return
     * @throws URISyntaxException the url of Jira is not a valid URI.
     */
    protected JiraMeasurementsTool.JiraLogStatistics getStatistics(String jiraURL, boolean anonymousAuthentication,
        String username, String password, String initialDate) throws URISyntaxException
    {

        int issueIndex = 0;
        int totalIssues;
        int totalBugs = 0;
        int numberOfOpenBugs = 0;
        double totalCriticalBugFixTime = 0;
        int counterCriticalBugs = 0;
        double totalBugFixTime = 0;
        double totalSecurityBugFixTime = 0;
        int counterSecurityBugs = 0;
        int counterCloseBugs = 0;
        int numberOfFeatureRequests = 0;
        int numberOfOpenFeatureRequests = 0;
        double numberOfClosedFeatureRequestsPerUpdate = 0;
        double numberOfClosedBugsPerUpdate = 0;
        boolean presenceOfSecurityBugsCorrected = false;
        boolean correctExecution = true;
        Issue is;
        String issueState;
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
        String jql = "created >= \"" + initialDate + "\"";
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
                    /*
                     * Measures for BUGs
                     */

                    if (is.getIssueType().getName().toUpperCase().equals("BUG")) {
                        issueState = is.getStatus().getName().toUpperCase();
                        totalBugs++;

                        /*
                         * Bug open if -->status !closed and ! done
                         */
                        if ((!issueState.equals(IssueStatus.CLOSED.toString()))
                            && (!issueState.equals(IssueStatus.DONE.toString()))) {
                            numberOfOpenBugs++;
                        }

                        /*
                         * Bug close if--> status closed or status done or status resolved Number of days= date of bug
                         * create - date of bug last update
                         */
                        if ((issueState.equals(IssueStatus.CLOSED.toString()))
                            || (issueState.equals(IssueStatus.RESOLVED.toString()))
                            || (issueState.equals(IssueStatus.DONE.toString()))) {

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

                            /*
                             * close bug is a secutiry issue
                             */
                            if (isSecurityIssue(is)) {
                                totalSecurityBugFixTime +=
                                    (double) interval.toDuration().getStandardSeconds() / secondsOfDay;
                                counterSecurityBugs++;
                            }

                        } else {

                            /*
                             * Other BUGs Measures
                             */

                        }

                    }
                    /*
                     * Measures for FEATUREs
                     */

                    else {
                        /*
                         * Issue is feature if type = "NEW FEATURE"
                         */
                        if (is.getIssueType().getName().toUpperCase().equals("NEW FEATURE")) {
                            issueState = is.getStatus().getName().toUpperCase();
                            numberOfFeatureRequests++;
                            /*
                             * Feature open if -->status !closed and ! done
                             */
                            if ((!issueState.equals(IssueStatus.CLOSED.toString()))
                                && (!issueState.equals(IssueStatus.DONE.toString()))) {
                                numberOfOpenFeatureRequests++;
                            }
                        }

                    }

                    issueIndex++;
                } // is !=null end
            }// for end
        } // while end

        /**
         * Metrics about all issues
         */

        // Number of Versions

        ProjectRestClient projectClient = restClient.getProjectClient();

        Project project;

        int countOfVersions = 0;

        Iterable<BasicProject> basicProjects = projectClient.getAllProjects().claim();

        for (BasicProject basicP : basicProjects) {

            project = projectClient.getProject(basicP.getKey()).claim();
            for (Version ver : project.getVersions()) {
                countOfVersions++;
            }
        }

        jql = "issuetype = Bug AND status in (Closed, Resolved)";
        results = searchClient.searchJql(jql).claim();
        numberOfClosedBugsPerUpdate = (double) results.getTotal() / (double) countOfVersions;

        jql = "issuetype = \"New Feature\" AND status in (Closed, Resolved)";
        results = searchClient.searchJql(jql).claim();
        numberOfClosedFeatureRequestsPerUpdate = (double) results.getTotal() / (double) countOfVersions;

        if (correctExecution) {

            JiraMeasurementsTool.JiraLogStatistics stats = new JiraMeasurementsTool.JiraLogStatistics();
            stats.openBugs = numberOfOpenBugs;
            stats.timeToResolveABug = totalBugFixTime / counterCloseBugs;
            stats.timeToResolveABlockingOrCriticalBug = totalCriticalBugFixTime / counterCriticalBugs;
            stats.numberOfFeatureRequests = numberOfFeatureRequests;
            stats.numberOfOpenFeatureRequests = numberOfOpenFeatureRequests;
            stats.numberOfClosedFeatureRequestsPerUpdate = numberOfClosedFeatureRequestsPerUpdate;
            stats.numberOfClosedBugsPerUpdate = numberOfClosedBugsPerUpdate;
            stats.numberOfSecurityBugs = (int) (totalBugs * security_factor);
            if (counterCloseBugs > 0) {
                presenceOfSecurityBugsCorrected = true;
            }
            stats.presenceOfSecurityBugsCorrected = presenceOfSecurityBugsCorrected;
            if (counterSecurityBugs > 0) {
                stats.timeToResolveASecurityBug = totalSecurityBugFixTime / counterSecurityBugs;
            }
            return stats;
        }

        return null;
    }

    /**
     * Returns if a given issue is related to security
     * 
     * @param is Issue
     * @return true if it is a security issue.
     */
    private boolean isSecurityIssue(Issue is)
    {
        String[] securityTerms =
            {"security", "secure", "attack", "vulnerability", "exploit", "sql injection", "cross-site scripting"};
        String issueSummary = is.getSummary() == null ? "" : is.getSummary().toLowerCase();
        String issueDescription = is.getDescription() == null ? "" : is.getDescription().toLowerCase();

        boolean found = false;
        for (int i = 0; i < securityTerms.length && !found; i++) {
            found = issueSummary.contains(securityTerms[i]) || issueDescription.contains(securityTerms[i]);
        }

        return found;
    }

}
