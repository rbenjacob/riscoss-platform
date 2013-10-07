package eu.riscoss.internal;

import java.io.File;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;

import eu.riscoss.HibernateSessionProvider;
import eu.riscoss.api.RISCOSSPlatform;
import eu.riscoss.api.ToolFactory;
import eu.riscoss.api.model.Measurement;
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

    @Override public void storeScope(Scope scope)
    {
        hibernateStore(scope);
    }

    @Override public void storeMeasurement(Measurement measurement)
    {
        hibernateStore(measurement);
    }

    @Override public File getTempDirectory(String namespace)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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

    private void hibernateStore(Object object)
    {
        Session session = hibernateSessionProvider.getSession();
        session.beginTransaction();
        session.saveOrUpdate(object);
        session.getTransaction().commit();
    }
}
