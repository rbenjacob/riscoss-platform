package eu.riscoss.api.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to store an answer provided by the user to one question.
 *
 * @version $Id$
 */
public class Answer
{
    /**
     * The question answer. It may be more than one if multi-choice
     */
    private List<String> answer;

    /**
     * Constructor.
     */
    public Answer()
    {
        answer=new ArrayList<String>();
    }

    /**
     * Get only one answer (first one).
     * 
     * @return first answer of the list. May be null if not set.
     */
    public String getAnswer()
    {
        return answer.get(0);
    }

    /**
     * Get All answers
     * 
     * @return the array of answers.
     */
    public String[] getAnswers()
    {
        return answer.toArray(new String[0]);
    }

    /**
     * Add an answer.
     * 
     * @param newAnswer the new answer to add to the list
     */
    public void addAnswer(String newAnswer)
    {
        this.answer.add(newAnswer);
    }
}
