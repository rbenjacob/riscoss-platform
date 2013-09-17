package eu.riscoss.internal;

import java.util.List;

import javax.inject.Inject;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.script.service.ScriptService;

import eu.riscoss.api.RISCOSSPlatform;
import eu.riscoss.api.ToolFactory;

/**
 * This is the script service for exposing RISCOSS Platform services inside wiki scripts. Bound to the $services.riscoss
 * variable.
 *
 * @version $Id$
 */
@Component("riscoss")
public class RISCOSSPlatformScriptService implements ScriptService
{
    @Inject
    private RISCOSSPlatform riscossPlatform;

    @Inject
    private ComponentManager cm;

    public ToolFactory getToolFactory(String toolId)
    {
        return riscossPlatform.getToolFactory(toolId);
    }

    public List<ToolFactory> getToolFactories()
    {
        return riscossPlatform.getToolFactories();
    }
}
