package eu.riscoss.api.model;

/**
 * OSSComponent.
 *
 * @version $Id$
 */
public class OSSComponent extends Component
{
    /**
     * The web site for the OSSComponent.
     */
    String webSite;

    OSSCommunity ossCommunity;

    /**
     * Default constructor.
     */
    public OSSComponent()
    {
    }

    public String getWebSite()
    {
        return webSite;
    }

    public void setWebSite(String webSite)
    {
        this.webSite = webSite;
    }

    public OSSCommunity getOssCommunity()
    {
        return ossCommunity;
    }

    public void setOssCommunity(OSSCommunity ossCommunity)
    {
        this.ossCommunity = ossCommunity;
    }

    @Override public String toString()
    {
        return String.format("[OSSComponent - id: %s, name: %s]", id, name);
    }
}
