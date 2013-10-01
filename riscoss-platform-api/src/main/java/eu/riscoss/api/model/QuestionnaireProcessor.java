package eu.riscoss.api.model;

import java.util.Map;

import eu.riscoss.api.model.Answer;

/**
 * The QuestionProcessor provides helper class to process a question after it is answered.
 *
 * @version $Id$
 */
public abstract class Answers implements
{
    protected Map<String, Answer> answers;

    public void addAnswer(String questionId, Answer answer)
    {
        this.answers.put(questionId, answer);
    }

    /**
     * This method is to process the "answers" provided. 
     */
    public abstract void process();
}
