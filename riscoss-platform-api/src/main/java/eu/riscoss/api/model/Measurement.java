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
    private String id;

    /**
     * The measurement type. This gives a reference to the semantics of the measurement (e.g., 'number of commits per
     * day')
     */
    private String type;

    /**
     * The measurement type.
     */
    private Date date;

    /**
     * The measurement value.
     */
    private String value;

    /**
     * @param id the measurement id.
     * @param type the measurement type.
     * @param value the measurement value.
     */
    public Measurement(String id, String type, String value)
    {
        this(id, type, new Date(), value);
    }

    /**
     * @param id the measurement id.
     * @param type the measurement type.
     * @param date the measurement date.
     * @param value the measurement value.
     */
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
