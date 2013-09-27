package eu.riscoss.api;

/**
 * The QuestionProcessor provides the API for processing a questions after it is answered.
 *
 * @version $Id$
 */
public abstract class QuestionProcessor
{
    /**
     * @param answer is the answer provided to the question
     */
    void process(Answer answer);
}
