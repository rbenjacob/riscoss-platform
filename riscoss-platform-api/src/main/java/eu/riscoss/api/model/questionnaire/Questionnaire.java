/**
 *
 */
package eu.riscoss.api.model.questionnaire;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author David
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
        questions = new ArrayList<Question>();
    }

    /**
     * @return the questions
     */
    public List<Question> getQuestions()
    {
        return Collections.unmodifiableList(questions);
    }

    /**
     * @param question the question to add
     */
    public void addQuestion(Question question)
    {
        this.questions.add(question);
    }
}
