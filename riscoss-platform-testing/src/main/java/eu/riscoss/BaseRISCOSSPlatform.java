package eu.riscoss;

import java.io.File;
import java.util.List;

import eu.riscoss.api.RISCOSSPlatform;
import eu.riscoss.api.ToolFactory;
import eu.riscoss.api.model.Measurement;
import eu.riscoss.api.model.Scope;
import eu.riscoss.api.model.questionnaire.Question;
import eu.riscoss.api.model.questionnaire.Questionnaire;
import eu.riscoss.api.model.questionnaire.QuestionnaireListener;

/**
 * BaseRISCOSSPlatform provides an empty implementation of the RISCOSSPlatform that can be used as a base class for
 * mocking RISCOSSPlatform in unit testing.
 *
 * @version $Id$
 */
public class BaseRISCOSSPlatform implements RISCOSSPlatform
{
    @Override public Question getQuestion(String questionId)
    {
        return null;
    }

    @Override public ToolFactory getToolFactory(String toolId)
    {
        return null;
    }

    @Override public List<ToolFactory> getToolFactories()
    {
        return null;
    }

    @Override public void storeScope(Scope scope)
    {
    }

    @Override public void storeMeasurement(Measurement measurement)
    {
    }

    @Override public File getTempDirectory(String namespace)
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
}
