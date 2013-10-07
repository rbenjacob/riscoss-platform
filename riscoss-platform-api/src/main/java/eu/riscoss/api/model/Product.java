package eu.riscoss.api.model;

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

    public Company getCompany()
    {
        return company;
    }

    public void setCompany(Company company)
    {
        this.company = company;
    }

    @Override public String toString()
    {
        return String.format("[Product - id: %s, name: %s]", id, name);
    }
}
