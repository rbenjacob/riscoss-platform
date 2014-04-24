package eu.riscoss.rdr;

import java.util.Date;

import eu.riscoss.rdr.model.RiskData;
import eu.riscoss.rdr.model.RiskDataType;
import eu.riscoss.rdr.model.internal.RiskDataImpl;

public class RiskDataFactory
{
    public static RiskData createRiskData(String id, String target, Date date, RiskDataType type, Object value)
    {
        RiskDataImpl riskData = new RiskDataImpl();
        riskData.setId(id);
        riskData.setTarget(target);
        riskData.setDate(date);
        riskData.setType(type);
        riskData.setValue(value);

        return riskData;
    }

}
