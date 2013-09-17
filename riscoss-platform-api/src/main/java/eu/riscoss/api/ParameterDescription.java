package eu.riscoss.api;

/**
 * ParameterDescription.
 *
 * @version $Id$
 */
public class ParameterDescription
{
    private String id;

    private String description;

    public ParameterDescription(String id, String description)
    {
        this.id = id;
        this.description = description;
    }

    public String getId()
    {
        return id;
    }

    public String getDescription()
    {
        return description;
    }

    @Override public String toString()
    {
        return String.format("%s : %s", id, description);
    }
}
