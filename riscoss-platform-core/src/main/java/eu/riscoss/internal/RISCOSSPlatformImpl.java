package eu.riscoss.internal;

import java.io.File;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;

import eu.riscoss.HibernateSessionProvider;
import eu.riscoss.api.RISCOSSPlatform;
import eu.riscoss.api.ToolFactory;
import eu.riscoss.api.model.GoalModel;
import eu.riscoss.api.model.Indicator;
import eu.riscoss.api.model.Measurement;
import eu.riscoss.api.model.RiskModel;
import eu.riscoss.api.model.Scope;
import eu.riscoss.api.model.questionnaire.Question;
import eu.riscoss.api.model.questionnaire.Questionnaire;
import eu.riscoss.api.model.questionnaire.QuestionnaireListener;

/**
 * RISCOSSPlatformImpl.
 *
 * @version $Id$
 */
@Component
@Singleton
public class RISCOSSPlatformImpl implements RISCOSSPlatform
{
    @Inject
    private Logger logger;

    @Inject
    private ComponentManager componentManager;

    @Inject
    private HibernateSessionProvider hibernateSessionProvider;

    @Override public ToolFactory getToolFactory(String toolId)
    {
        try {
            return componentManager.getInstance(ToolFactory.class, toolId);
        } catch (ComponentLookupException e) {
            logger.error(String.format("Error while retrieving tool factories", e));
        }

        return null;
    }

    @Override public List<ToolFactory> getToolFactories()
    {
        try {
            return componentManager.getInstanceList(ToolFactory.class);
        } catch (ComponentLookupException e) {
            logger.error(String.format("Error while retrieving tool factories", e));
        }

        return Collections.EMPTY_LIST;
    }

    @Override public File getTempDirectory(String namespace)
    {
        //TODO: Maybe this should be returned by a component that is implemented by the hosting platform.
        File tempDirectory = new File(System.getProperty("java.io.tmpdir"), namespace);
        tempDirectory.mkdir();

        return tempDirectory;
    }

    @Override public Question getQuestion(String questionId)
    {
        //TODO: Implement this
        return null;
    }

    @Override public void registerQuestionnaire(Scope target, Questionnaire questionnaire,
            QuestionnaireListener questionnaireListener)
    {
        //TODO: Implement this
    }

    @Override public List<Questionnaire> getRegisteredQuestionnaires()
    {
        //TODO: Implement this
        return null;
    }

    @Override public Scope getScope(String id)
    {
        Session session = hibernateSessionProvider.getSession();
        session.beginTransaction();

        try {
            Query query = session.createQuery("from Scope as S where S.id = :id");
            query.setParameter("id", id);
            List<Scope> scopes = query.list();

            if (scopes.size() != 0) {
                return scopes.get(0);
            }
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.getTransaction().commit();
        }

        return null;
    }

    @Override public List<Scope> getScopes()
    {
        Session session = hibernateSessionProvider.getSession();
        session.beginTransaction();

        try {
            Query query = session.createQuery("from Scope");
            return query.list();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.getTransaction().commit();
        }

        return Collections.EMPTY_LIST;
    }

    @Override public <T> List<T> getScopesByType(Class<T> scopeClass)
    {
        Session session = hibernateSessionProvider.getSession();
        session.beginTransaction();

        try {
            Query query = session.createQuery(String.format("from %s", scopeClass.getName()));

            return (List<T>) query.list();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.getTransaction().commit();
        }

        return Collections.EMPTY_LIST;
    }

    @Override public void storeScope(Scope scope)
    {
        hibernateStore(scope);
    }

    @Override public List<Measurement> getMeasurements(Scope scope, int offset, int length)
    {
        Session session = hibernateSessionProvider.getSession();
        session.beginTransaction();
        try {
            Query query = session.createQuery("from Measurement as M where M.scope.id = :scopeId");
            query.setParameter("scopeId", scope.getId());
            query.setFirstResult(offset);
            query.setMaxResults(length);

            return query.list();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.getTransaction().commit();
        }

        return Collections.EMPTY_LIST;
    }

    @Override public List<Measurement> getMeasurements(Scope scope, String type, int offset, int length)
    {
        Session session = hibernateSessionProvider.getSession();
        session.beginTransaction();
        try {
            Query query = session.createQuery("from Measurement as M where M.scope.id = :scopeId AND M.type = :type");
            query.setParameter("scopeId", scope.getId());
            query.setParameter("type", type);
            query.setFirstResult(offset);
            query.setMaxResults(length);

            return query.list();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.getTransaction().commit();
        }

        return Collections.EMPTY_LIST;
    }

    @Override public void storeMeasurement(Measurement measurement)
    {
        hibernateStore(measurement);
    }

    @Override public List<Indicator> getIndicators(Scope scope, int offset, int length)
    {
        Session session = hibernateSessionProvider.getSession();
        session.beginTransaction();
        try {
            Query query = session.createQuery("from Indicator as I where I.scope.id = :scopeId");
            query.setParameter("scopeId", scope.getId());
            query.setFirstResult(offset);
            query.setMaxResults(length);

            return query.list();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.getTransaction().commit();
        }

        return Collections.EMPTY_LIST;
    }

    @Override public List<Indicator> getIndicators(Scope scope, String type, int offset, int length)
    {
        Session session = hibernateSessionProvider.getSession();
        session.beginTransaction();
        try {
            Query query = session.createQuery("from Indicator as I where I.scope.id = :scopeId AND  I.type = :type");
            query.setParameter("scopeId", scope.getId());
            query.setParameter("type", type);
            query.setFirstResult(offset);
            query.setMaxResults(length);

            return query.list();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.getTransaction().commit();
        }

        return Collections.EMPTY_LIST;
    }

    @Override public void storeIndicator(Indicator indicator)
    {
        hibernateStore(indicator);
    }

    @Override public List<RiskModel> getRiskModels()
    {
        Session session = hibernateSessionProvider.getSession();
        session.beginTransaction();
        try {
            Query query = session.createQuery("from RiskModel");

            return query.list();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.getTransaction().commit();
        }

        return Collections.EMPTY_LIST;
    }

    @Override public RiskModel getRiskModel(String id)
    {
        Session session = hibernateSessionProvider.getSession();
        session.beginTransaction();
        try {
            Query query = session.createQuery("from RiskModel as R where R.id = :id");
            query.setParameter("id", id);
            List<RiskModel> riskModels = query.list();

            if (riskModels.size() != 0) {
                return riskModels.get(0);
            }
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.getTransaction().commit();
        }

        return null;
    }

    @Override public void storeRiskModel(RiskModel riskModel)
    {
        hibernateStore(riskModel);
    }

    @Override public List<GoalModel> getGoalModels()
    {
        Session session = hibernateSessionProvider.getSession();
        session.beginTransaction();
        try {
            Query query = session.createQuery("from GoalModel");

            return query.list();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.getTransaction().commit();
        }

        return Collections.EMPTY_LIST;
    }

    @Override public GoalModel getGoalModel(String id)
    {
        Session session = hibernateSessionProvider.getSession();
        session.beginTransaction();
        try {
            Query query = session.createQuery("from GoalModel as G where G.id = :id");
            query.setParameter("id", id);
            List<GoalModel> goalModels = query.list();

            if (goalModels.size() != 0) {
                return goalModels.get(0);
            }
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.getTransaction().commit();
        }

        return null;
    }

    @Override public void storeGoalModel(GoalModel goalModel)
    {
        hibernateStore(goalModel);
    }

    private void hibernateStore(Object object)
    {
        Session session = hibernateSessionProvider.getSession();
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
