package eu.riscoss.api.model;

import eu.riscoss.api.model.Answer;

/**
 * The QuestionProcessor provides helper class to process a question after it is answered.
 *
 * @version $Id$
 */
public abstract class QuestionProcessor
{
    protected Answer answer;

    public void setAnswer(Answer answer)
    {
        this.answer = answer;
    }

    /**
     * This method is to process the "answer" provided. 
     */
    public abstract void process();
}
