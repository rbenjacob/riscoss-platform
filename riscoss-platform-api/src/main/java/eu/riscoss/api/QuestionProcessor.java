package eu.riscoss.api;

import eu.riscoss.api.model.Answer;

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
    public abstract void process(Answer answer);
}
