package eu.riscoss.api.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A company.
 *
 * @version $Id$
 */
public class Company extends Scope
{
    /**
     * The products associated to this company.
     */
    private List<Product> products;

    /**
     * Default constructor.
     */
    public Company()
    {
        products = new ArrayList<Product>();
    }

    public List<Product> getProducts()
    {
        return products;
    }

    public void setProducts(List<Product> products)
    {
        this.products = products;
    }

    @Override public String toString()
    {
        return String.format("[Company - id: %s, name: %s]", id, name);
    }
}
