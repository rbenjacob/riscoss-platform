package eu.riscoss.api.model;

import java.util.ArrayList;
import java.util.List;

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
    List<Component> components;

    public Product()
    {
        components = new ArrayList<Component>();
    }

    public Company getCompany()
    {
        return company;
    }

    public void setCompany(Company company)
    {
        this.company = company;
    }

    public List<Component> getComponents()
    {
        return components;
    }

    public void setComponents(List<Component> components)
    {
        this.components = components;
    }

    @Override public String toString()
    {
        return String.format("[Product - id: %s, name: %s]", id, name);
    }
}
