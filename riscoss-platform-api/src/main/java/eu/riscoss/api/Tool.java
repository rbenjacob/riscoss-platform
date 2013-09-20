package eu.riscoss.api;

import java.util.Map;

/**
 * The Tool interface provides the API for executing the business logic implemented by a tool. A tool is a runnable
 * entity that is able to manipulate the knowledge base. For example, a tool could produce measurement on a given target
 * (e.g., a project, a component)
 *
 * @version $Id$
 */
public interface Tool
{
    /**
     * The possible status for a tool instance.
     */
    enum Status
    {
        /**
         * The tool has been initialized and is ready to be executed.
         */
        INITIALIZED,
        /**
         * The tool is executing its business logic.
         */
        STARTED,
        /**
         * The tool has been stopped.
         */
        STOPPED,
        /**
         * The tool has finished its execution.
         */
        DONE,
        /**
         * The tool had a failure while executing.
         */
        ERROR
    }

    /**
     * Execute the business logic implemented by the tool.
     *
     * It's the implementor responsibility to make this method thread safe and handle possible concurrency issues.
     *
     * @param target the id of the entity the tool should work on.
     * @param parameters a map containing the parameters to be passed business logic of the tool when executed.
     */
    void execute(String target, Map<String, String> parameters);

    /**
     * @return the status of the execution.
     */
    Status getStatus();

    /**
     * Stop the current execution.
     */
    void stop();
}
