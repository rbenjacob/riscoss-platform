package eu.riscoss.api.model.questionnaire;

/**
 * The QuestionProcessor provides helper class to process a question after it is answered.
 *
 * @version $Id$
 */
public interface QuestionnaireListener
{
    /**
     * This method is to process the "answers" provided.
     *
     * @param answers the answers that have been provided.
     */
    void questionnaireAnswered(Answers answers);
}
