package eu.riscoss;

import org.xwiki.component.embed.EmbeddableComponentManager;

import eu.riscoss.api.RISCOSSPlatform;

/**
 * StandaloneRISCOSSPlatform.
 *
 * @version $Id$
 */
public final class StandaloneRISCOSSPlatform
{
    public static RISCOSSPlatform getRISCOSSPlatform() throws Exception
    {
        EmbeddableComponentManager ecm = new EmbeddableComponentManager();
        ecm.initialize(StandaloneRISCOSSPlatform.class.getClassLoader());

        return ecm.getInstance(RISCOSSPlatform.class);
    }
}
