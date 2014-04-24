package eu.riscoss.rdr.model.internal;

import java.util.Date;

import eu.riscoss.rdr.model.RiskData;
import eu.riscoss.rdr.model.RiskDataType;

/**
 * Created by fm on 4/24/14.
 */
public class RiskDataImpl implements RiskData
{
    private String id;

    private String target;

    private Date date;

    private RiskDataType type;

    private Object value;

    @Override public String getId()
    {
        return id;
    }

    @Override public String getTarget()
    {
        return target;
    }

    @Override public Date getDate()
    {
        return date;
    }

    @Override public RiskDataType getType()
    {
        return type;
    }

    @Override public Object getValue()
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

    public void setDate(Date date)
    {
        this.date = date;
    }

    public void setType(RiskDataType type)
    {
        this.type = type;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }
}
