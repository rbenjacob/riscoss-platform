package eu.riscoss.api.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Component.
 *
 * @version $Id$
 */
public class Component extends Scope
{
    private String version;

    List<Product> products;

    /**
     * Default constructor.
     */
    public Component()
    {
        products = new ArrayList<Product>();
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public List<Product> getProducts()
    {
        return products;
    }

    public void setProducts(List<Product> products)
    {
        this.products = products;
    }
}
