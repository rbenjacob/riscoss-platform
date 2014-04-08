package eu.riscoss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.riscoss.rdr.RiskDataRepositoryFactory;
import eu.riscoss.rdr.SessionClosedException;
import eu.riscoss.rdr.api.RiskDataRepository;
import eu.riscoss.rdr.api.model.RiskData;
import eu.riscoss.rdr.api.model.RiskDataType;
import eu.riscoss.rdr.api.model.Session;

public class RiskDataRepositoryTest
{
    private static RiskDataRepository riskDataRepository;

    @BeforeClass
    public static void beforeClass() throws Exception
    {
        riskDataRepository = RiskDataRepositoryFactory.create(new HibernateSessionProviderImpl());
    }

    @Test
    public void createSessionTest()
    {
        Session session = riskDataRepository.createSession();

        List<Session> sessions = riskDataRepository.getSessions(0, 1);

        assertTrue(sessions.size() > 0);
        assertEquals(session.getId(), sessions.get(0).getId());
    }

    @Test
    public void closeSessionTest()
    {
        Session session = riskDataRepository.createSession();
        riskDataRepository.closeSession(session);

        List<Session> sessions = riskDataRepository.getSessions(0, 1);

        assertTrue(sessions.size() > 0);
        assertEquals(session.getId(), sessions.get(0).getId());
        assertFalse(sessions.get(0).isOpen());
    }

    @Test
    public void storeAndGetRiskDataWithExplicitSession() throws Exception
    {
        Session session = riskDataRepository.createSession();
        for (int i = 0; i < 5; i++) {
            RiskData riskData = new RiskData();
            riskData.setId("rd" + i);
            riskData.setTarget("foo");
            riskData.setType(RiskDataType.NUMBER);
            riskData.setValue("10");
            riskDataRepository.storeRiskData(session, riskData);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        riskDataRepository.closeSession(session);

        List<RiskData> riskData = riskDataRepository.getRiskData(session, 0, 5);

        assertTrue(riskData.size() == 5);
    }

    @Test
    public void storeAndGetRiskDataFromLastClosedSession() throws Exception
    {
        Session session1 = riskDataRepository.createSession();
        for (int i = 0; i < 3; i++) {
            RiskData riskData = new RiskData();
            riskData.setId("rd" + i);
            riskData.setTarget("foo");
            riskData.setType(RiskDataType.NUMBER);
            riskData.setValue("10");
            riskDataRepository.storeRiskData(session1, riskData);
        }

        Session session2 = riskDataRepository.createSession();
        for (int i = 0; i < 5; i++) {
            RiskData riskData = new RiskData();
            riskData.setId("rd" + i);
            riskData.setTarget("foo");
            riskData.setType(RiskDataType.NUMBER);
            riskData.setValue("10");
            riskDataRepository.storeRiskData(session2, riskData);
        }

        riskDataRepository.closeSession(session2);

        Session session = riskDataRepository.getClosedSessions(0, 1).get(0);
        List<RiskData> riskData = riskDataRepository.getRiskData(session, 0, 5);

        assertTrue(riskData.size() == 5);
        assertEquals(riskData.get(0).getSession().getId(), session2.getId());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        riskDataRepository.closeSession(session1);

        session = riskDataRepository.getClosedSessions(0, 1).get(0);
        riskData = riskDataRepository.getRiskData(session, 0, 5);

        assertTrue(riskData.size() == 3);
    }

    @Test(expected = SessionClosedException.class)
    public void storeRiskDataToClosedSession() throws Exception
    {
        Session session = riskDataRepository.createSession();
        riskDataRepository.closeSession(session);

        RiskData riskData = new RiskData();
        riskData.setId("rd");
        riskData.setTarget("foo");
        riskData.setType(RiskDataType.NUMBER);
        riskData.setValue("10");
        riskDataRepository.storeRiskData(session, riskData);

    }

    public void getOpenClosedSessions() throws Exception
    {
        List<Session> openSessions = new ArrayList<Session>();
        for (int i = 0; i < 2; i++) {
            openSessions.add(riskDataRepository.createSession());
        }

        List<Session> closedSessions = new ArrayList<Session>();
        for (int i = 0; i < 3; i++) {
            Session session = riskDataRepository.createSession();
            riskDataRepository.closeSession(session);
            closedSessions.add(session);
        }

        List<Session> sessions = riskDataRepository.getOpenSessions(0, 10);
        for (Session session : sessions) {
            boolean found = false;
            for (Session openSession : openSessions) {
                if (session.getId().equals(openSession.getId())) {
                    found = true;
                    break;
                }
            }

            assertTrue(found);
        }
        
        sessions = riskDataRepository.getClosedSessions(0, 10);
        for (Session session : sessions) {
            boolean found = false;
            for (Session closedSession : closedSessions) {
                if (session.getId().equals(closedSession.getId())) {
                    found = true;
                    break;
                }
            }

            assertTrue(found);
        }
    }

}
