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
public class JiraMeasurementsTool implements Tool {
	protected static final Logger LOGGER = LoggerFactory
			.getLogger(JiraMeasurementsTool.class);

	protected final RISCOSSPlatform riscossPlatform;

	private final static int maxBufferIssue = 250;

	private final long secondsOfDay = 86400;

	public static class JiraLogStatistics {
		public int openBugs;
		public double timeToResolveABug;
		public double timeToResolveABlockingOrCriticalBug;
		public int numberOfFeatureRequests;
		public int numberOfOpenFeatureRequests;
		public int numberOfClosedFeatureRequestsPerUpdate;
		public int numberOfClosedBugsPerUpdate;


	}

	public JiraMeasurementsTool(RISCOSSPlatform riscossPlatform) {
		this.riscossPlatform = riscossPlatform;
	}

	@Override
	public void execute(Scope scope, Map<String, String> parameters) {
		LOGGER.info(String.format("Running %s on %s",
				JiraMeasurementsToolFactory.TOOL_ID, scope));

		String jiraURL = parameters
				.get(JiraMeasurementsToolFactory.JIRA_URL_PARAMETER);
		boolean anonymousAuthentication = Boolean
				.parseBoolean(parameters
						.get(JiraMeasurementsToolFactory.JIRA_ANONYMOUS_AUTHENTICATION_PARAMETER));
		String username = parameters
				.get(JiraMeasurementsToolFactory.JIRA_USERNAME_PARAMETER);
		String password = parameters
				.get(JiraMeasurementsToolFactory.JIRA_PASSWORD_PARAMETER);
		String initialDate = parameters
				.get(JiraMeasurementsToolFactory.INITIAL_DATE_PARAMETER);

		if (jiraURL == null) {
			LOGGER.error("jira URL is null.");
		}

		JiraMeasurementsTool.JiraLogStatistics statistics = null;
		try {
			statistics = getStatistics(jiraURL, anonymousAuthentication,
					username, password, initialDate);
		} catch (URISyntaxException e) {
			LOGGER.error(String.format("Error while executing analysis on %s",
					jiraURL), e);
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
			measurement.setValue(String.format("%.2f",
					statistics.timeToResolveABug));
			riscossPlatform.storeMeasurement(measurement);

			measurement = new Measurement();
			measurement.setScope(scope);
			measurement.setType("time-to-resolve-a-blocking-or-critical-bug");
			measurement.setValue(String.format("%.2f",
					statistics.timeToResolveABlockingOrCriticalBug));
			riscossPlatform.storeMeasurement(measurement);

			measurement = new Measurement();
			measurement.setScope(scope);
			measurement.setType("number-of-feature-requests");
			measurement.setValue(Integer.toString(statistics.numberOfFeatureRequests));
			riscossPlatform.storeMeasurement(measurement);
			
			measurement = new Measurement();
			measurement.setScope(scope);
			measurement.setType("number-of-closed-feature-requests-per-pdate;");
			measurement.setValue(Integer.toString(statistics.numberOfClosedFeatureRequestsPerUpdate));
			riscossPlatform.storeMeasurement(measurement);
			
			
			measurement = new Measurement();
			measurement.setScope(scope);
			measurement.setType("number-Of-Open-Feature-Requests");
			measurement.setValue(Integer.toString(statistics.numberOfOpenFeatureRequests));
			riscossPlatform.storeMeasurement(measurement);
			
			measurement = new Measurement();
			measurement.setScope(scope);
			measurement.setType("number-Of-Closed-Bugs-Per-Update");
			measurement.setValue(Integer.toString(statistics.numberOfClosedBugsPerUpdate));
			riscossPlatform.storeMeasurement(measurement);
			
			

			LOGGER.info(String.format(
					"Analysis completed [%d, %f, %f,%d,%d,%d,%d]. Results stored",
					statistics.openBugs, statistics.timeToResolveABug,
					statistics.timeToResolveABlockingOrCriticalBug,
					statistics.numberOfFeatureRequests,
					statistics.numberOfClosedFeatureRequestsPerUpdate,
					statistics.numberOfOpenFeatureRequests,
					statistics.numberOfClosedBugsPerUpdate));
		}
	}

	@Override
	public Status getStatus() {
		return Status.DONE;
	}

	@Override
	public void stop() {
		LOGGER.info("Stopping");
	}

	protected JiraMeasurementsTool.JiraLogStatistics getStatistics(
			String jiraURL, boolean anonymousAuthentication, String username,
			String password, String initialDate) throws URISyntaxException {

		int issueIndex = 0;
		int totalIssues;
		int numberOfOpenBugs = 0;
		double totalCriticalBugFixTime = 0;
		int counterCriticalBugs = 0;
		double totalBugFixTime = 0;
		int counterCloseBugs = 0;
		int numberOfFeatureRequests=0;
		int numberOfOpenFeatureRequests=0;
		int numberOfClosedFeatureRequestsPerUpdate=0;
		int numberOfClosedBugsPerUpdate=0;
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
			restClient = jiraRestFactory.create(new URI(jiraURL),
					new AnonymousAuthenticationHandler());
		} else {
			restClient = jiraRestFactory.createWithBasicHttpAuthentication(
					new URI(jiraURL), username, password);
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
			results = searchClient.searchJql(jql, maxBufferIssue, issueIndex,
					null).claim();
			for (final BasicIssue issue : results.getIssues()) {
				is = client.getIssue(issue.getKey()).claim();

				if (is != null) {
					/*
					 * Measures for BUGs
					 */
					
					if (is.getIssueType().getName().toUpperCase().equals("BUG")) {
						issueState = is.getStatus().getName().toUpperCase();

						/*
						 * Bug open if -->status !closed and ! done
						 */
						if ((!issueState.equals(IssueStatus.CLOSED.toString()))
								&& (!issueState.equals(IssueStatus.DONE	.toString()))) {
							numberOfOpenBugs++;
						}

						/*
						 * Bug close if--> status closed or status done or
						 * status resolved Number of days= date of bug create -
						 * date of bug last update
						 */
						if ((issueState.equals(IssueStatus.CLOSED.toString()))
								|| (issueState.equals(IssueStatus.RESOLVED
										.toString()))
								|| (issueState.equals(IssueStatus.DONE.toString()))) {

							interval = new Interval(is.getCreationDate(),
									is.getUpdateDate());
							totalBugFixTime += (double) interval.toDuration()
									.getStandardSeconds() / secondsOfDay;
							counterCloseBugs++;

							/*
							 * close bug with Priority CRITICAL OR BLOCKER
							 */
							if (is.getPriority() != null) {
								if ((is.getPriority().getName().toUpperCase()
										.equals(IssuePriority.CRITICAL
												.toString()))
										|| (is.getPriority().getName()
												.toUpperCase()
												.equals(IssuePriority.BLOCKER
														.toString()))) {
									totalCriticalBugFixTime += (double) interval
											.toDuration().getStandardSeconds()
											/ secondsOfDay;
									counterCriticalBugs++;
								}
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
						if (is.getIssueType().getName().toUpperCase().equals("NEW FEATURE")) 
	                    {                             
							issueState=is.getStatus().getName().toUpperCase(); 
	                        numberOfFeatureRequests++;
	                        /*
	                         * Feature open if -->status !closed and ! done
	                         */
	                        if ((!issueState.equals(IssueStatus.CLOSED.toString()))
	                                && (!issueState.equals(IssueStatus.DONE.toString()))) {
	                        	numberOfOpenFeatureRequests++;
	                        } 
	                        else
	                        	/*
	    						 * Feature close if--> status closed or status done or
	    						 * status resolved Number of days= date of bug create -
	    						 * date of bug last update
	    						 */
	    						if ((issueState.equals(IssueStatus.CLOSED.toString()))
	    								|| (issueState.equals(IssueStatus.RESOLVED
	    										.toString()))
	    								|| (issueState.equals(IssueStatus.DONE.toString())))
	    							numberOfClosedFeatureRequestsPerUpdate++;
	                    }

					}
					
					issueIndex++;
				} // is !=null end
			}// for end
		} // while end

		if (correctExecution) {
			
			JiraMeasurementsTool.JiraLogStatistics stats = new JiraMeasurementsTool.JiraLogStatistics();
			stats.openBugs = numberOfOpenBugs;
			System.out.println("totalBT="+ totalBugFixTime+"/counterCB="+counterCloseBugs);
			stats.timeToResolveABug = totalBugFixTime / counterCloseBugs;
			stats.timeToResolveABlockingOrCriticalBug = totalCriticalBugFixTime
					/ counterCriticalBugs;
			System.out.println("totalCBT="+ totalCriticalBugFixTime+"/counterCCB="+counterCriticalBugs);
			stats.numberOfFeatureRequests= numberOfFeatureRequests;
			stats.numberOfOpenFeatureRequests=numberOfOpenFeatureRequests;
			stats.numberOfClosedFeatureRequestsPerUpdate=numberOfClosedFeatureRequestsPerUpdate;
			stats.numberOfClosedBugsPerUpdate=counterCloseBugs;
			return stats;
		}

		return null;
	}

}