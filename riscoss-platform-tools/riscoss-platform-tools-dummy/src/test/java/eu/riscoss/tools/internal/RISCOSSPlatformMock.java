package eu.riscoss.tools.internal;

import java.io.File;
import java.util.List;

import org.xwiki.component.annotation.Component;

import eu.riscoss.api.RISCOSSPlatform;
import eu.riscoss.api.ToolFactory;
import eu.riscoss.api.model.Measurement;
import eu.riscoss.api.model.Scope;
import eu.riscoss.api.model.questionnaire.Question;
import eu.riscoss.api.model.questionnaire.Questionnaire;
import eu.riscoss.api.model.questionnaire.QuestionnaireListener;

/**
 * RISCOSSPlatformMock.
 *
 * @version $Id$
 */
@Component
public class RISCOSSPlatformMock implements RISCOSSPlatform
{
    @Override public ToolFactory getToolFactory(String type)
    {
        return null;
    }

    @Override public List<ToolFactory> getToolFactories()
    {
        return null;
    }

    @Override public void storeMeasurement(Measurement measurement)
    {
    }

    @Override public File getTempDirectory(String namespace)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public Question getQuestion(String questionId)
    {
        return null;
    }

    @Override public void registerQuestionnaire(Scope target, Questionnaire questionnaire,
            QuestionnaireListener questionnaireListener)
    {
    }

    @Override public List<Questionnaire> getRegisteredQuestionnaires()
    {
        return null;
    }

    @Override public void storeScope(Scope scope)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
