package eu.riscoss.rdr.api.model;

import java.util.Date;

public class Session
{
    private String id;

    private Date startDate;

    private Date endDate;

    public Session()
    {
    }

    public String getId()
    {
        return id;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public boolean isOpen()
    {
        return endDate == null;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }

    public String toString()
    {
        return String.format("[%s %s %s]", id, startDate, endDate);
    }
}
