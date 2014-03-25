package eu.riscoss.api.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Product.
 *
 * @version $Id$
 */
public class Product extends Scope
{
    /**
     * The company this product is associated to.
     */
    Company company;

    /**
     * The list of components associated to this product.
     */
    Set<Component> components = new HashSet<Component>();

    public Product()
    {
    }

    public Company getCompany()
    {
        return company;
    }

    public void setCompany(Company company)
    {
        this.company = company;
    }

    public Set<Component> getComponents()
    {
        return components;
    }

    public void setComponents(Set<Component> components)
    {
        this.components = components;
    }

    @Override public String toString()
    {
        return String.format("[Product - id: %s, name: %s]", id, name);
    }
}
