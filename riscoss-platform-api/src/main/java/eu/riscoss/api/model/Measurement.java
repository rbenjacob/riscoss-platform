package eu.riscoss.api.model;

import java.util.Date;

/**
 * Measurement.
 *
 * @version $Id$
 */
public class Measurement
{
    private String id;

    private String type;

    private Date date;

    private String value;

    public Measurement(String id, String type, String value)
    {
        this(id, type, new Date(), value);
    }

    public Measurement(String id, String type, Date date, String value)
    {
        this.id = id;
        this.type = type;
        this.date = date;
        this.value = value;
    }

    public Date getDate()
    {
        return date;
    }

    public String getId()
    {
        return id;
    }

    public String getType()
    {
        return type;
    }

    public String getValue()
    {
        return value;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
}
