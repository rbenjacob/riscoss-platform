package eu.riscoss.api;

import java.io.File;
import java.util.List;

import org.xwiki.component.annotation.Role;

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
 * RISCOSSPlatform. This interface provides all the methods for accessing all the functionalities provided by the
 * RISCOSS platform. such as methods for storing/retrieving entities stored in the "knowledge base".
 *
 * @version $Id$
 */
@Role
public interface RISCOSSPlatform
{
    /*****************************************************************************************************************
     * Tool oriented API.
     *****************************************************************************************************************/
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
     * Return a directory where to store temporary files.
     *
     * @param namespace the namespace used for isolating temporary files.
     * @return the temporary directory.
     */
    File getTempDirectory(String namespace);

    /*****************************************************************************************************************
     * Data model operations.
     *****************************************************************************************************************/
    /**
     * Retrieve a well defined scope from the platform knowledge base.
     *
     * @param scopeId the scope id.
     * @return the scope whose id is given by the parameter.
     */
    Scope getScope(String scopeId);

    /**
     * @return all the scopes stored in the platform knowledge base.
     */
    List<Scope> getScopes();

    /**
     * @param scopeClass the scope class (e.g., OSSComponents)
     * @return all the scopes of a given type stored in the platform knowledge base.
     */
    <T> List<T> getScopesByType(Class<T> scopeClass);

    /**
     * Store a scope in the knowledge base.
     *
     * @param scope the scope to be stored.
     */
    void storeScope(Scope scope);

    /**
     * Get the measurements for a given scope.
     *
     * @param scope the scope.
     * @param offset initial offset for results.
     * @param length results length.
     * @return a list of measurement.
     */
    List<Measurement> getMeasurements(Scope scope, int offset, int length);

    /**
     * Get the measurements of a given type for a given scope.
     *
     * @param scope the scope.
     * @param type the measurement type.
     * @param offset initial offset for results.
     * @param length results length.
     * @return a list of measurement.
     */
    List<Measurement> getMeasurements(Scope scope, String type, int offset, int length);

    /**
     * Store a measurement entity object in the platform knowledge base.
     *
     * @param measurement the measurement to be stored.
     */
    void storeMeasurement(Measurement measurement);

    /**
     * Get the indicators for a given scope.
     *
     * @param scope the scope.
     * @param offset initial offset for results.
     * @param length results length.
     * @return a list of indicators.
     */
    List<Indicator> getIndicators(Scope scope, int offset, int length);

    /**
     * Get the indicators of a given type for a given scope.
     *
     * @param scope the scope.
     * @param type the indicator type.
     * @return a list of measurement.
     */
    Indicator getIndicator(Scope scope, String type);

    /**
     * Store an indicator entity object in the platform knowledge base.
     *
     * @param indicator the indicator to be stored.
     */
    void storeIndicator(Indicator indicator);

    /**
     * @return the list of risk models stored in the platform knowledge base.
     */
    List<RiskModel> getRiskModels();

    /**
     * @param id the id of the risk model.
     * @return the risk model with the given id.
     */
    RiskModel getRiskModel(String id);

    /**
     * Store a risk model in the platform knowledge base.
     *
     * @param riskModel the risk model to be stored.
     */
    void storeRiskModel(RiskModel riskModel);

    /**
     * @return the list of goal models stored in the platform knowledge base.
     */
    List<GoalModel> getGoalModels();

    /**
     * @param id the id of the goal model.
     * @return the goal model with the given id.
     */
    GoalModel getGoalModel(String id);

    /**
     * Store a goal model in the platform knowledge base.
     *
     * @param goalModel the risk model to be stored.
     */
    void storeGoalModel(GoalModel goalModel);

    /**
     * @return the list of impact models stored in the platform knowledge base.
     */
    List<ImpactModel> getImpactModels();

    /**
     * @param id the id of the risk model.
     * @return the impact model with the given id.
     */
    ImpactModel getImpactModel(String id);

    /**
     * Store a impact model in the platform knowledge base.
     *
     * @param impactModel the impact model to be stored.
     */
    void storeImpactModel(ImpactModel impactModel);

    /*****************************************************************************************************************
     * Questionnaire oriented API.
     * TODO: Move this in a separate module
     *****************************************************************************************************************/
    /**
     * @return the questions stored in the platform.
     */
    List<Question> getQuestions();

    /**
     * The loadQuestion method will load a question from the RISCOSS DB.
     *
     * @param questionId the id of the question to be retrieved from the RISCOSS DB.
     */
    Question getQuestion(String questionId);

    /**
     * Store a question
     *
     * @param question the question to be stored.
     */
    void storeQuestion(Question question);

    /**
     * The registerQuestionnaire method should ask the questions. NOTE: If some question is already answered the
     * platform shall not asking it again.
     *
     * @param target the target scope where the questions will be asked.
     * @param questionnaire the questions to be answered.
     * @param questionnaireListener the listener that will be used to process the answers.
     */
    void registerQuestionnaire(Scope target, Questionnaire questionnaire, QuestionnaireListener questionnaireListener);

    /**
     * @return a list of questionnaires that need to be answered.
     */
    List<Questionnaire> getRegisteredQuestionnaires();

    /**
     * @param scope the scope.
     * @return a list of questionnaires that need to be answered for a given scope.
     */
    List<Questionnaire> getRegisteredQuestionnaires(Scope scope);

    /**
     * Store answers to a questionnaire. This method removes the questionnaire from the registered questionnaires.
     *
     * @param questionnaire the target questionnaire.
     * @param answers the answers to the questionnaire.
     */
    void submitAnswers(Questionnaire questionnaire, Answers answers);

    /**
     * @param scope the scope.
     * @param questionId the questionId.
     * @return the answer that were given to a question for a given scope.
     */
    Answer getAnswer(Scope scope, String questionId);
}
