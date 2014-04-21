package eu.riscoss.rdr.api.internal;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.riscoss.rdr.SessionClosedException;
import eu.riscoss.rdr.api.HibernateSessionProvider;
import eu.riscoss.rdr.api.RiskDataRepository;
import eu.riscoss.rdr.api.model.RiskData;
import eu.riscoss.rdr.api.model.Session;

public class RiskDataRepositoryImpl implements RiskDataRepository
{
    private Logger logger = LoggerFactory.getLogger(RiskDataRepositoryImpl.class);

    private HibernateSessionProvider hibernateSessionProvider;

    public RiskDataRepositoryImpl(HibernateSessionProvider hibernateSessionProvider)
    {
        this.hibernateSessionProvider = hibernateSessionProvider;
    }

    @Override
    public Session createSession()
    {
        Session session = new Session();
        session.setId(UUID.randomUUID().toString());
        session.setStartDate(new Date());

        hibernateStore(session);

        return session;
    }

    @Override
    public void closeSession(Session session)
    {
        Session actualSession = getSession(session.getId());

        if (actualSession.isOpen()) {
            actualSession.setEndDate(new Date());
            hibernateStore(actualSession);
        }
    }

    @Override
    public List<Session> getSessions(int offset, int limit)
    {
        org.hibernate.Session hibernateSession = hibernateSessionProvider.getSession();
        hibernateSession.beginTransaction();

        try {
            Query query = hibernateSession.createQuery("FROM Session AS S ORDER BY S.startDate DESC");
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            List<Session> sessions = query.list();

            return sessions;
        } catch (Exception e) {
            logger.error("Unable to get sessions.", e);
            hibernateSession.getTransaction().rollback();
        } finally {
            hibernateSession.getTransaction().commit();
        }

        return Collections.EMPTY_LIST;
    }

    @Override
    public List<Session> getSessions(String target, int offset, int limit)
    {
        org.hibernate.Session hibernateSession = hibernateSessionProvider.getSession();
        hibernateSession.beginTransaction();

        try {
            Query query = hibernateSession.createQuery(
                    "SELECT DISTINCT S FROM RiskData AS RD, Session AS S WHERE RD.session.id = S.id  AND RD.target = :target ORDER BY S.startDate DESC");
            query.setParameter("target", target);
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            List<Session> sessions = query.list();

            return sessions;
        } catch (Exception e) {
            logger.error("Unable to get sessions.", e);
            hibernateSession.getTransaction().rollback();
        } finally {
            hibernateSession.getTransaction().commit();
        }

        return Collections.EMPTY_LIST;
    }

    @Override
    public List<Session> getOpenSessions(int offset, int limit)
    {
        org.hibernate.Session hibernateSession = hibernateSessionProvider.getSession();
        hibernateSession.beginTransaction();

        try {
            Query query =
                    hibernateSession.createQuery("FROM Session AS S WHERE S.endDate IS NULL ORDER BY S.startDate DESC");
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            List<Session> sessions = query.list();

            return sessions;
        } catch (Exception e) {
            logger.error("Unable to get sessions.", e);
            hibernateSession.getTransaction().rollback();
        } finally {
            hibernateSession.getTransaction().commit();
        }

        return Collections.EMPTY_LIST;
    }

    @Override
    public List<Session> getOpenSessions(String target, int offset, int limit)
    {
        org.hibernate.Session hibernateSession = hibernateSessionProvider.getSession();
        hibernateSession.beginTransaction();

        try {
            Query query =
                    hibernateSession.createQuery(
                            "SELECT DISTINCT S FROM RiskData RD, Session AS S WHERE RD.session.id = S.id  AND RD.target = :target AND S.endDate IS NULL ORDER BY S.startDate DESC");
            query.setParameter("target", target);
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            List<Session> sessions = query.list();

            return sessions;
        } catch (Exception e) {
            logger.error("Unable to get sessions.", e);
            hibernateSession.getTransaction().rollback();
        } finally {
            hibernateSession.getTransaction().commit();
        }

        return Collections.EMPTY_LIST;
    }

    @Override
    public List<Session> getClosedSessions(int offset, int limit)
    {
        org.hibernate.Session hibernateSession = hibernateSessionProvider.getSession();
        hibernateSession.beginTransaction();

        try {
            Query query = hibernateSession.createQuery(
                    "FROM Session AS S WHERE S.endDate IS NOT NULL ORDER BY S.endDate DESC");
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            List<Session> sessions = query.list();

            return sessions;
        } catch (Exception e) {
            logger.error("Unable to get sessions.", e);
            hibernateSession.getTransaction().rollback();
        } finally {
            hibernateSession.getTransaction().commit();
        }

        return Collections.EMPTY_LIST;
    }

    @Override
    public List<Session> getClosedSessions(String target, int offset, int limit)
    {
        org.hibernate.Session hibernateSession = hibernateSessionProvider.getSession();
        hibernateSession.beginTransaction();

        try {
            Query query = hibernateSession
                    .createQuery(
                            "SELECT DISTINCT S FROM RiskData RD, Session AS S WHERE RD.session.id = S.id  AND RD.target = :target AND S.endDate IS NOT NULL ORDER BY S.endDate DESC");
            query.setParameter("target", target);
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            List<Session> sessions = query.list();

            return sessions;
        } catch (Exception e) {
            logger.error("Unable to get sessions.", e);
            hibernateSession.getTransaction().rollback();
        } finally {
            hibernateSession.getTransaction().commit();
        }

        return Collections.EMPTY_LIST;
    }

    @Override
    public List<RiskData> getRiskData(Session session, int offset, int limit)
    {
        org.hibernate.Session hibernateSession = hibernateSessionProvider.getSession();
        hibernateSession.beginTransaction();

        try {
            Query query = hibernateSession.createQuery("FROM RiskData AS RD WHERE RD.session.id = :sessionId");
            query.setParameter("sessionId", session.getId());
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            List<RiskData> riskData = query.list();

            return riskData;
        } catch (Exception e) {
            e.printStackTrace();
            hibernateSession.getTransaction().rollback();
        } finally {
            hibernateSession.getTransaction().commit();
        }

        return Collections.EMPTY_LIST;
    }

    @Override
    public List<RiskData> getRiskData(Session session, String target, int offset, int limit)
    {
        org.hibernate.Session hibernateSession = hibernateSessionProvider.getSession();
        hibernateSession.beginTransaction();

        try {
            Query query = hibernateSession.createQuery("FROM RiskData AS RD WHERE RD.session.id = :sessionId AND RD.target = :target");
            query.setParameter("sessionId", session.getId());
            query.setParameter("target", target);
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            List<RiskData> riskData = query.list();

            return riskData;
        } catch (Exception e) {
            e.printStackTrace();
            hibernateSession.getTransaction().rollback();
        } finally {
            hibernateSession.getTransaction().commit();
        }

        return Collections.EMPTY_LIST;
    }

    @Override
    public void storeRiskData(Session session, RiskData riskData) throws SessionClosedException
    {
        Session actualSession = getSession(session.getId());

        if (actualSession != null && actualSession.isOpen()) {
            riskData.setSession(actualSession);
            riskData.setDate(new Date());

            hibernateStore(riskData);
        } else {
            throw new SessionClosedException(String.format("Session %s is closed", session.getId()));
        }
    }

    public Session getSession(String id)
    {
        org.hibernate.Session hibernateSession = hibernateSessionProvider.getSession();
        hibernateSession.beginTransaction();

        try {
            Query query = hibernateSession.createQuery("FROM Session AS S WHERE S.id = :sessionId");
            query.setParameter("sessionId", id);
            List<Session> sessions = query.list();

            if (sessions.size() > 0) {
                return sessions.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            hibernateSession.getTransaction().rollback();
        } finally {
            hibernateSession.getTransaction().commit();
        }

        return null;
    }

    private void hibernateStore(Object object)
    {
        org.hibernate.Session session = hibernateSessionProvider.getSession();
        session.beginTransaction();
        try {
            session.saveOrUpdate(object);
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.getTransaction().commit();
        }
    }
}
