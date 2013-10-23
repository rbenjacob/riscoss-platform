package eu.riscoss.tools;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.riscoss.api.RISCOSSPlatform;
import eu.riscoss.api.Tool;
import eu.riscoss.api.model.Scope;

/**
 * KPATool.java
 *
 * @version $Id$
 */
public class KPATool implements Tool
{
	private static final Logger LOGGER = LoggerFactory.getLogger(KPATool.class);
	
	private final RISCOSSPlatform riscossPlatform;

	public KPATool(RISCOSSPlatform riscossPlatform)
	{
		this.riscossPlatform = riscossPlatform;
	}

	@Override public void execute( Scope target, Map<String, String> parameters)
	{}
	
	@Override public Status getStatus()
	{
		return Status.DONE;
	}

	@Override public void stop()
	{
		LOGGER.info("Stopping");
	}
}
