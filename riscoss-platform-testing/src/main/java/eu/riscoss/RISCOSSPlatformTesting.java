package eu.riscoss;

import org.xwiki.component.embed.EmbeddableComponentManager;

import eu.riscoss.api.RISCOSSPlatform;

/**
 * RISCOSSPlatformTesting.
 *
 * @version $Id$
 */
public final class RISCOSSPlatformTesting
{
    public static RISCOSSPlatform getRISCOSSPlatform() throws Exception
    {
        EmbeddableComponentManager ecm = new EmbeddableComponentManager();
        ecm.initialize(Main.class.getClassLoader());

        return ecm.getInstance(RISCOSSPlatform.class);
    }
}
