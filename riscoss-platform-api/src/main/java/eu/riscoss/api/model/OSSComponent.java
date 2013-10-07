package eu.riscoss.api.model;

import java.util.ArrayList;

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

    /**
     * Default constructor.
     */
    public OSSComponent()
    {
        products = new ArrayList<Product>();
    }

    public String getWebSite()
    {
        return webSite;
    }

    public void setWebSite(String webSite)
    {
        this.webSite = webSite;
    }

    @Override public String toString()
    {
        return String.format("[OSSComponent - id: %s, name: %s]", id, name);
    }
}
