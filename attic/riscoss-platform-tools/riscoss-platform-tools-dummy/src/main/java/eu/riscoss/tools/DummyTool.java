package eu.riscoss.tools;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.riscoss.api.RISCOSSPlatform;
import eu.riscoss.api.Tool;
import eu.riscoss.api.model.Scope;

/**
 * DummyTool.
 *
 * @version $Id$
 */
public class DummyTool implements Tool
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DummyTool.class);

    private final RISCOSSPlatform riscossPlatform;

    public DummyTool(RISCOSSPlatform riscossPlatform)
    {
        this.riscossPlatform = riscossPlatform;
    }

    @Override public void execute(Scope target, Map<String, String> parameters)
    {
        LOGGER.info(String.format("Running on %s with RISCOSS platform %s", target, riscossPlatform));
    }

    @Override public Status getStatus()
    {
        return Status.DONE;
    }

    @Override public void stop()
    {
        LOGGER.info("Stopping");
    }
}
