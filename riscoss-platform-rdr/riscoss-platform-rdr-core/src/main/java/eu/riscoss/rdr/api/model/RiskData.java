package eu.riscoss.rdr.api.model;

import java.io.Serializable;
import java.util.Date;

public class RiskData implements Serializable
{
    private static final long serialVersionUID = 5733314975852733360L;

    private String id;

    private Session session;

    private String target;

    private Date date;

    private RiskDataType type;

    private String value;

    public RiskData()
    {
    }

    public String getId()
    {
        return id;
    }

    public String getTarget()
    {
        return target;
    }

    public Date getDate()
    {
        return date;
    }

    public RiskDataType getType()
    {
        return type;
    }

    public Object getValue()
    {
        return value;
    }

    public Session getSession()
    {
        return session;
    }

    public void setSession(Session session)
    {
        this.session = session;
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

    public void setValue(String value)
    {
        this.value = value;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((session == null) ? 0 : session.hashCode());
        result = prime * result + ((target == null) ? 0 : target.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RiskData other = (RiskData) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (session == null) {
            if (other.session != null)
                return false;
        } else if (!session.equals(other.session))
            return false;
        if (target == null) {
            if (other.target != null)
                return false;
        } else if (!target.equals(other.target))
            return false;
        if (type != other.type)
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

}
