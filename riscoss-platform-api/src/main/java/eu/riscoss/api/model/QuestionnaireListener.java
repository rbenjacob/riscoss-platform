package eu.riscoss.api.model;

/**
 * The QuestionProcessor provides helper class to process a question after it is answered.
 *
 * @version $Id$
 */
public interface QuestionnaireListener
{
    /**
     * This method is to process the "answers" provided. 
     */
    void process(Answers answers);
}
