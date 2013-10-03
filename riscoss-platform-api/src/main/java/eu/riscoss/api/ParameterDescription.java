package eu.riscoss.api;

/**
 * This class is used in the {@link ToolFactory} class for returning information about the parameters a tool need in
 * order to operate. This information can be used in order to dynamically generate user interfaces for configuring the
 * tools.
 *
 * @version $Id$
 */
public class ParameterDescription
{
    /**
     * The id of the parameter.
     */
    private String id;

    /**
     * The description of the parameter.
     */
    private String description;

    /**
     * The default value.
     */
    private String defaultValue;

    /**
     * @param id the parameter id.
     * @param description the parameter description.
     */
    public ParameterDescription(String id, String description)
    {
        this(id, description, null);
    }

    /**
     * @param id the parameter id.
     * @param description the parameter description.
     * @param defaultValue the default value.
     */
    public ParameterDescription(String id, String description, String defaultValue)
    {
        this.id = id;
        this.description = description;
        this.defaultValue = defaultValue;
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

    public String getDefaultValue()
    {
        return defaultValue;
    }
}
