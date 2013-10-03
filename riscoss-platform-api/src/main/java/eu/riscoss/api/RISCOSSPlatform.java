package eu.riscoss.api;

import java.util.List;

import org.xwiki.component.annotation.Role;

import eu.riscoss.api.model.Measurement;
import eu.riscoss.api.model.Answer;
import eu.riscoss.api.model.Question;
import eu.riscoss.api.model.QuestionProcessor;
import eu.riscoss.api.model.Questionnaire;
import eu.riscoss.api.model.QuestionnaireListener;
import eu.riscoss.api.model.QuestionnaireProcessor;
import eu.riscoss.api.model.Scope;

/**
 * RISCOSSPlatform. This interface provides all the methods for accessing all the functionalities provided by the
 * RISCOSS platform. such as methods for storing/retrieving entities stored in the "knowledge base".
 *
 * @version $Id$
 */
@Role
public interface RISCOSSPlatform
{
    /**
     * Retrieve the tool factory for creating instances of a given tool.
     *
     * @param toolId the id of the tool.
     * @return the factory for instantiating the tool.
     */
    ToolFactory getToolFactory(String toolId);

    /**
     * @return all the tool factories registered in the system.
     */
    List<ToolFactory> getToolFactories();

    /**
     * Store a measurement entity object in the platform knowledge base.
     *
     * @param measurement the measurement to be stored.
     */
    void storeMeasurement(Measurement measurement);
    
    /**
     *  The loadQuestion method will load a question from the RISCOSS DB. 
     *  
     *  @param questionId the id of the question to be retrieved from the RISCOSS DB.
     */
    Question loadQuestion(String questionId);

    /**
     *  The registerQuestionnaire method should ask the questions. 
     *  NOTE: If some question is already answered the platform shall not asking it again. 
     *  
     *  @param questionnaire the questions to be answered.
     *  @param target the target scope where the questions will be asked.
     *  @param answersProcessor the listener that will be used to process the answers.
     */
    void registerQuestionnaire(Questionnaire questionnaire, Scope target, QuestionnaireListener answersProcessor);
    
}
