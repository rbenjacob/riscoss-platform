package eu.riscoss;

import org.xwiki.component.embed.EmbeddableComponentManager;

import eu.riscoss.api.RISCOSSPlatform;

/**
 * RISCOSSPlatformHSQLDB.
 *
 * @version $Id$
 */
public final class RISCOSSPlatformHSQLDB
{
    public static RISCOSSPlatform getRISCOSSPlatform() throws Exception
    {
        EmbeddableComponentManager ecm = new EmbeddableComponentManager();
        ecm.initialize(RISCOSSPlatformHSQLDB.class.getClassLoader());

        return ecm.getInstance(RISCOSSPlatform.class);
    }
}
