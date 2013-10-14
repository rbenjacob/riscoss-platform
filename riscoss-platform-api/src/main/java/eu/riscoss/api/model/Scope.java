package eu.riscoss.api.model;

/**
 * A scope is the base class for all the entities in the model that can be a target of some analysis (e.g., a company, a
 * component, a prouct)
 *
 * @version $Id$
 */
public abstract class Scope
{
    /**
     * The scope id.
     */
    public String id;

    /**
     * The scope name.
     */
    public String name;

    /**
     * Scope description
     */
    public String description;

    /**
     * Default constructor.
     */
    public Scope()
    {
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Scope)) {
            return false;
        }

        Scope scope = (Scope) o;

        if (!id.equals(scope.id)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }
}
