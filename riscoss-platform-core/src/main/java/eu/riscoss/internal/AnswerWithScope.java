package eu.riscoss.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import eu.riscoss.api.model.Scope;

/**
 * This class is used to persist an answer to a question and associate it to a scope so that the platform can return
 * whether this question has been answered for a given scope.
 *
 * TODO: Refactor the model classes in the API so that this can be modeled directly there.
 *
 * @version $Id$
 */
public class AnswerWithScope implements Serializable
{
    /**
     * A separator used in serialized possible answers string to distinguish the elements.
     */
    private static final String SEPARATOR = "##";

    private Scope scope;

    private String questionId;

    private String values;

    public AnswerWithScope()
    {
    }

    public String getQuestionId()
    {
        return questionId;
    }

    public void setQuestionId(String questionId)
    {
        this.questionId = questionId;
    }

    public String getValues()
    {
        return values;
    }

    public void setValues(String values)
    {
        this.values = values;
    }

    public void setValues(List<String> values)
    {
        StringBuffer sb = new StringBuffer();

        for (String s : values) {
            sb.append(s);
            sb.append(SEPARATOR);
        }

        this.values = sb.toString();
    }

    public List<String> getValuesAsList()
    {
        List<String> result = new ArrayList<String>();

        if (values != null) {
            String[] components = values.split(SEPARATOR);
            for (String component : components) {
                result.add(component);
            }
        }

        return result;
    }

    public Scope getScope()
    {
        return scope;
    }

    public void setScope(Scope scope)
    {
        this.scope = scope;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AnswerWithScope)) {
            return false;
        }

        AnswerWithScope that = (AnswerWithScope) o;

        if (!questionId.equals(that.questionId)) {
            return false;
        }
        if (!scope.equals(that.scope)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = scope.hashCode();
        result = 31 * result + questionId.hashCode();
        return result;
    }
}
