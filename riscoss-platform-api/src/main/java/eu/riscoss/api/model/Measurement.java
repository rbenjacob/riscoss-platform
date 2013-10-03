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
     * The target of the measurement.
     */
    private String target;

    /**
     * The measurement type.
     */
    private Date date;

    /**
     * The measurement value.
     */
    private String value;

    /**
     * @param target the measurement target.
     * @param type the measurement type.
     * @param value the measurement value.
     */
    public Measurement(String target, String type, String value)
    {
        this(target, type, new Date(), value);
    }

    /**
     * @param target the measurement target.
     * @param type the measurement type.
     * @param date the measurement date.
     * @param value the measurement value.
     */
    public Measurement(String target, String type, Date date, String value)
    {
        this.target = target;
        this.type = type;
        this.date = date;
        this.value = value;
    }

    public String getId()
    {
        return id;
    }

    public String getTarget()
    {
        return target;
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

    public void setId(String id)
    {
        this.id = id;
    }

    public void setTarget(String target)
    {
        this.target = target;
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
