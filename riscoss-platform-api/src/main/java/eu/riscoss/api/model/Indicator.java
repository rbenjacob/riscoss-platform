package eu.riscoss.api.model;

import java.util.Date;

/**
 * This class is used to store a indicator.
 *
 * @version $Id$
 */
public class Indicator
{
    /**
     * The indicator id.
     */
    private Long id;

    /**
     * The indicator type. This gives a reference to the semantics of the indicator.
     */
    private String type;

    /**
     * The target of the indicator.
     */
    private Scope scope;

    /**
     * The indicator type.
     */
    private Date date;

    /**
     * The indicator value.
     */
    private String value;

    public Indicator()
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
}
