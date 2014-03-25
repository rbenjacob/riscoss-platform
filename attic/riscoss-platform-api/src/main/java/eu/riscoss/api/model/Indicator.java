package eu.riscoss.api.model;

import java.io.Serializable;
import java.util.Date;

/**
 * This class is used to store a indicator.
 *
 * @version $Id$
 */
public class Indicator implements Serializable
{
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Indicator)) {
            return false;
        }

        Indicator indicator = (Indicator) o;

        if (!date.equals(indicator.date)) {
            return false;
        }
        if (!scope.equals(indicator.scope)) {
            return false;
        }
        if (!type.equals(indicator.type)) {
            return false;
        }
        if (!value.equals(indicator.value)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = type.hashCode();
        result = 31 * result + scope.hashCode();
        result = 31 * result + date.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}
