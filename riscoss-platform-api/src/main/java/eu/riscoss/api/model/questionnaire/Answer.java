package eu.riscoss.api.model.questionnaire;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is used to store an values provided by the user to one question.
 *
 * @version $Id$
 */
public class Answer
{
    /**
     * The question values. It may be more than one if multi-choice
     */
    private List<String> values;

    /**
     * Constructor.
     */
    public Answer()
    {
        values = new ArrayList<String>();
    }

    /**
     * Get only one values (first one).
     *
     * @return first values of the list. May be null if not set.
     */
    public String getValue()
    {
        if (values.size() != 0) {
            return values.get(0);
        }

        return null;
    }

    /**
     * Get All answers
     *
     * @return the array of answers.
     */
    public List<String> getValues()
    {
        return Collections.unmodifiableList(values);
    }

    /**
     * Add an value.
     *
     * @param value the new value to add to the list
     */
    public void addValue(String value)
    {
        this.values.add(value);
    }
}
