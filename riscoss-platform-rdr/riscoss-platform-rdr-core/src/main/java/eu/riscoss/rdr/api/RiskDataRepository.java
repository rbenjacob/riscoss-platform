package eu.riscoss.rdr.api;

import java.util.List;

import eu.riscoss.rdr.api.internal.RiskDataEntity;
import eu.riscoss.rdr.model.RiskData;

public interface RiskDataRepository
{
    List<RiskData> getRiskData(String target, int offset, int limit);

    List<RiskData> getRiskData(String target, String id, int offset, int limit);

    void storeRiskData(RiskData riskData);
}
