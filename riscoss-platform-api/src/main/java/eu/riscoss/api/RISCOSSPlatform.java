package eu.riscoss.api;

import java.util.List;

import org.xwiki.component.annotation.Role;

import eu.riscoss.api.model.Measurement;
import eu.riscoss.api.model.Answer;
import eu.riscoss.api.model.QuestionProcessor;
import eu.riscoss.api.model.QuestionnaireProcessor;

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
     *  The registerQuestion method should ask the question identified by 
     *  questionId to the user. The platform shall process the question 
     *  once it is answered by the user. If the question is already answered
     *  the platform shall process the question without asking it again. 
     *  
     *  @param questionId the id of the question to be answered.
     *  @param questionProcessor instance that can "process()" the question once answered.
     */
    void registerQuestion(String questionId, QuestionProcessor questionProcessor);

    /**
     *  The registerQuestionnaire method should ask the questions identified by 
     *  questionIds to the user. The platform shall process the questions
     *  once all of them are answered by the user. If some question is already 
     *  answered the platform shall not asking it again, but shall provide the answer
     *  to the Questionnaire processor. 
     *  
     *  @param questionIds the ids of the questions to be answered.
     *  @param questionnaireProcessor instance that can "process()" the questions once answered.
     */
    void registerQuestionnarie(String[] questionIds, QuestionnaireProcessor questionnaireProcessor);
}
