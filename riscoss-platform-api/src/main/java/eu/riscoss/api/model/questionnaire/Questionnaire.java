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
     * The questionnaire id. This is used for equality tests.
     */
    private String id;

    /**
     * The list of questions
     */
    private ArrayList<Question> questions;

    /**
     * Standard constructor. We enforce the presence of an id by declaring it as a parameter of the constructor.
     *
     * @param id the questionnaire id.
     */
    public Questionnaire(String id)
    {
        this.id = id;
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Questionnaire)) {
            return false;
        }

        Questionnaire that = (Questionnaire) o;

        if (!id.equals(that.id)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }
}
