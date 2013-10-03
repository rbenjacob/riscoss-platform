/**
 * 
 */
package eu.riscoss.api.model;

import java.util.ArrayList;

/**
 * @author David
 *
 */
public class Questionnaire
{
    /**
     * The list of questions
     */
    private ArrayList<Question> questions;

    /**
     * basic constructor
     */
    public Questionnaire()
    {
        questions=new ArrayList<Question>();
    }

    /**
     * @return the questions
     */
    public Question[] getQuestions()
    {
        return questions.toArray(new Question[0]);
    }

    /**
     * @param question the question to add
     */
    public void addQuestions(Question question)
    {
        this.questions.add(question);
    }

}
