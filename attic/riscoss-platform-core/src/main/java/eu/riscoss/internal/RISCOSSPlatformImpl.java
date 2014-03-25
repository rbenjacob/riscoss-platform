package eu.riscoss.internal;

import java.io.File;
import java.util.ArrayList;
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
import eu.riscoss.api.model.ImpactModel;
import eu.riscoss.api.model.Indicator;
import eu.riscoss.api.model.Measurement;
import eu.riscoss.api.model.RiskModel;
import eu.riscoss.api.model.Scope;
import eu.riscoss.api.model.questionnaire.Answer;
import eu.riscoss.api.model.questionnaire.Answers;
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
    /**
     * This class is used to store information about registered questionnaires.
     */
    private static class QuestionnaireEntry
    {
        Scope scope;

        Questionnaire questionnaire;

        QuestionnaireListener listener;
    }

    @Inject
    private Logger logger;

    @Inject
    private ComponentManager componentManager;

    @Inject
    private HibernateSessionProvider hibernateSessionProvider;

    private List<QuestionnaireEntry> registeredQuestionnaires;

    public RISCOSSPlatformImpl()
    {
        registeredQuestionnaires = new ArrayList<QuestionnaireEntry>();
    }

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

    @Override public Question getQuestion(String id)
    {
        Session session = hibernateSessionProvider.getSession();
        session.beginTransaction();

        try {
            Query query = session.createQuery("from Question as Q where Q.id = :id");
            query.setParameter("id", id);
            List<Question> questions = query.list();

            if (questions.size() != 0) {
                return questions.get(0);
            }
        } catch (Exception e) {
            logger.error("Error getting question", e);
            session.getTransaction().rollback();
        } finally {
            session.getTransaction().commit();
        }

        return null;
    }

    @Override public List<Question> getQuestions()
    {
        Session session = hibernateSessionProvider.getSession();
        session.beginTransaction();

        try {
            Query query = session.createQuery("from Question");
            return query.list();
        } catch (Exception e) {
            logger.error("Error getting questions", e);
            session.getTransaction().rollback();
        } finally {
            session.getTransaction().commit();
        }

        return Collections.EMPTY_LIST;
    }

    @Override public void storeQuestion(Question question)
    {
        hibernateStore(question);
    }

    @Override public void registerQuestionnaire(Scope scope, Questionnaire questionnaire,
            QuestionnaireListener questionnaireListener)
    {
        QuestionnaireEntry entry = new QuestionnaireEntry();
        entry.scope = scope;
        entry.questionnaire = questionnaire;
        entry.listener = questionnaireListener;

        registeredQuestionnaires.add(entry);
    }

    @Override public List<Questionnaire> getRegisteredQuestionnaires()
    {
        List<Questionnaire> result = new ArrayList<Questionnaire>();
        for (QuestionnaireEntry entry : registeredQuestionnaires) {
            result.add(entry.questionnaire);
        }

        return result;
    }

    @Override public List<Questionnaire> getRegisteredQuestionnaires(Scope scope)
    {
        List<Questionnaire> result = new ArrayList<Questionnaire>();
        for (QuestionnaireEntry entry : registeredQuestionnaires) {
            if (entry.scope.equals(scope)) {
                result.add(entry.questionnaire);
            }
        }

        return result;
    }

    //TODO: This should clearly be refactored wrt answer storage.
    @Override public void submitAnswers(Questionnaire questionnaire, Answers answers)
    {
        QuestionnaireEntry targetEntry = null;
        for (QuestionnaireEntry entry : registeredQuestionnaires) {
            if (entry.questionnaire.equals(questionnaire)) {
                targetEntry = entry;
                break;
            }
        }

        if (targetEntry != null) {
            /* Store question answers */
            for (Question question : questionnaire.getQuestions()) {
                Answer answer = answers.getAnswer(question.getId());
                if (answer != null) {
                    //TODO: The data model concerning questions/questionnaires is convoluted. Refactor it.
                    AnswerWithScope answerWithScope = new AnswerWithScope();
                    answerWithScope.setScope(targetEntry.scope);
                    answerWithScope.setQuestionId(question.getId());
                    answerWithScope.setValues(answer.getValues());
                    hibernateStore(answerWithScope);
                }
            }

            if (targetEntry.listener != null) {
                targetEntry.listener.questionnaireAnswered(answers);
            }

            registeredQuestionnaires.remove(targetEntry);
        }
    }

    @Override public Answer getAnswer(Scope scope, String questionId)
    {
        Session session = hibernateSessionProvider.getSession();
        session.beginTransaction();

        try {
            Query query = session.createQuery(
                    "from AnswerWithScope as AWS where AWS.scope.id = :scopeId AND AWS.questionId = :questionId");
            query.setParameter("scopeId", scope.getId());
            query.setParameter("questionId", questionId);
            List<AnswerWithScope> answerWithScopes = query.list();

            if (answerWithScopes.size() != 0) {
                AnswerWithScope answerWithScope = answerWithScopes.get(0);

                Answer answer = new Answer();
                for (String s : answerWithScope.getValuesAsList()) {
                    answer.addValue(s);
                }

                return answer;
            }
        } catch (Exception e) {
            logger.error("Error getting answers", e);
            session.getTransaction().rollback();
        } finally {
            session.getTransaction().commit();
        }

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
            logger.error("Error getting scope", e);
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
            logger.error("Error getting scopes", e);
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
            logger.error("Error getting scopes by type", e);
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
            logger.error("Error getting measurements", e);
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
            logger.error("Error getting measurements by type", e);
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
            logger.error("Error getting indicators", e);
            session.getTransaction().rollback();
        } finally {
            session.getTransaction().commit();
        }

        return Collections.EMPTY_LIST;
    }

    @Override public Indicator getIndicator(Scope scope, String type)
    {
        Session session = hibernateSessionProvider.getSession();
        session.beginTransaction();
        try {
            Query query = session.createQuery("from Indicator as I where I.scope.id = :scopeId AND  I.type = :type");
            query.setParameter("scopeId", scope.getId());
            query.setParameter("type", type);

            List<Indicator> indicators = query.list();

            if (indicators.size() != 0) {
                return indicators.get(0);
            }
        } catch (Exception e) {
            logger.error("Error getting indicator by type for a given scope", e);
            session.getTransaction().rollback();
        } finally {
            session.getTransaction().commit();
        }

        return null;
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
            logger.error("Error getting risk models", e);
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
            logger.error("Error getting risk model", e);
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
            logger.error("Error getting goal models", e);
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
            logger.error("Error getting goal model", e);
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

    @Override public List<ImpactModel> getImpactModels()
    {
        Session session = hibernateSessionProvider.getSession();
        session.beginTransaction();
        try {
            Query query = session.createQuery("from ImpactModel");

            return query.list();
        } catch (Exception e) {
            logger.error("Error getting impact models", e);
            session.getTransaction().rollback();
        } finally {
            session.getTransaction().commit();
        }

        return Collections.EMPTY_LIST;
    }

    @Override public ImpactModel getImpactModel(String id)
    {
        Session session = hibernateSessionProvider.getSession();
        session.beginTransaction();
        try {
            Query query = session.createQuery("from ImpactModel as I where I.id = :id");
            query.setParameter("id", id);
            List<ImpactModel> impactModels = query.list();

            if (impactModels.size() != 0) {
                return impactModels.get(0);
            }
        } catch (Exception e) {
            logger.error("Error getting impact model", e);
            session.getTransaction().rollback();
        } finally {
            session.getTransaction().commit();
        }

        return null;
    }

    @Override public void storeImpactModel(ImpactModel impactModel)
    {
        hibernateStore(impactModel);
    }

    private void hibernateStore(Object object)
    {
        Session session = hibernateSessionProvider.getSession();
        session.beginTransaction();
        try {
            session.saveOrUpdate(object);
        } catch (Exception e) {
            logger.error(String.format("Error storing %s", object), e);
            session.getTransaction().rollback();
        } finally {
            session.getTransaction().commit();
        }
    }
}
