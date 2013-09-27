package eu.riscoss.api.model;

import eu.riscoss.api.model.Answer;

/**
 * The QuestionProcessor provides helper class to process a question after it is answered.
 *
 * @version $Id$
 */
public abstract class QuestionProcessor
{
    private Answer answer;

    public void setAnswer(Answer answer)
    {
        this.answer = answer;
    }

    /**
     * @param answer is the answer provided to the question
     */
    public abstract void process();
}
