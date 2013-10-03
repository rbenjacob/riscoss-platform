package eu.riscoss.internal;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;

import eu.riscoss.api.RISCOSSPlatform;
import eu.riscoss.api.ToolFactory;
import eu.riscoss.api.model.Question;
import eu.riscoss.api.model.Measurement;
import eu.riscoss.api.model.Answer;
import eu.riscoss.api.model.Questionnaire;
import eu.riscoss.api.model.QuestionnaireListener;
import eu.riscoss.api.model.Scope;

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

    @Override public void storeMeasurement(Measurement measurement)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
    
    @Override public Question loadQuestion(String questionId)
    {
        Question q=new Question();
        return q;
    }
    
    @Override public void registerQuestionnaire(Questionnaire questionnaire, Scope target, QuestionnaireListener answersProcessor)
    {

    }
}
