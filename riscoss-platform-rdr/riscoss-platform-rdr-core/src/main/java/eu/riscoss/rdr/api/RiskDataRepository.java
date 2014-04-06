package eu.riscoss.rdr.api;

import java.util.List;

import eu.riscoss.rdr.SessionClosedException;
import eu.riscoss.rdr.api.model.RiskData;
import eu.riscoss.rdr.api.model.Session;

public interface RiskDataRepository
{
    Session createSession();

    void closeSession(Session session);

    List<Session> getSessions(int offset, int limit);

    List<RiskData> getRiskData(int offset, int limit);

    List<RiskData> getRiskData(Session session, int offset, int limit);

    void storeRiskData(Session session, RiskData riskData) throws SessionClosedException;
}
