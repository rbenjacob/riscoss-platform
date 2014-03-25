package eu.riscoss.api.model;

import java.util.Date;

/**
 * This class is used to store a measurement.
 *
 * @version $Id$
 */
public class Measurement
{
    /**
     * The measurement id.
     */
    private Long id;

    /**
     * The measurement type. This gives a reference to the semantics of the measurement (e.g., 'number of commits per
     * day')
     */
    private String type;

    /**
     * The target of the measurement.
     */
    private Scope scope;

    /**
     * The measurement type.
     */
    private Date date;

    /**
     * The measurement value.
     */
    private String value;

    /**
     * Default constructor
     */
    public Measurement()
    {
        date = new Date();
    }

    public Long getId()
    {
        return id;
    }

    public Scope getScope()
    {
        return scope;
    }

    public String getType()
    {
        return type;
    }

    public Date getDate()
    {
        return date;
    }

    public String getValue()
    {
        return value;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public void setScope(Scope scope)
    {
        this.scope = scope;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    @Override public String toString()
    {
        return String.format("[Measurement - scope: %s, type: %s, value: %s]", scope.getId(), type, value);
    }
}
