package eu.riscoss.rdr.model;

import java.util.Date;

public interface RiskData
{
    String getId();

    String getTarget();

    Date getDate();

    RiskDataType getType();

    Object getValue();
}
