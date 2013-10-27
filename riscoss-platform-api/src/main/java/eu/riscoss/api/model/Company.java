package eu.riscoss.api.model;

import java.util.HashSet;
import java.util.Set;

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
    private Set<Product> products = new HashSet<Product>();

    /**
     * Default constructor.
     */
    public Company()
    {
    }

    public Set<Product> getProducts()
    {
        return products;
    }

    public void setProducts(Set<Product> products)
    {
        this.products = products;
    }

    @Override public String toString()
    {
        return String.format("[Company - id: %s, name: %s]", id, name);
    }
}
