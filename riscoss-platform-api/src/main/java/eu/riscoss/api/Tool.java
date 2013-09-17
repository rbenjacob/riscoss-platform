package eu.riscoss.api;

import java.util.Map;

/**
 * Tool.
 *
 * @version $Id$
 */
public interface Tool
{
    enum Status
    {
        INITIALIZED,
        STARTED,
        STOPPED,
        DONE
    }

    void execute(String target, Map<String, String> parameters);

    Status getStatus();

    void stop();
}
