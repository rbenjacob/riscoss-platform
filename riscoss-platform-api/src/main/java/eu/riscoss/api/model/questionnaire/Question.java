package eu.riscoss.api.model.questionnaire;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is for represent a question.
 *
 * @version $Id$
 */
public class Question
{
    /**
     * The possible types for a question.
     */
    public static enum Type
    {
        /**
         * A free text question.
         */
        FREETEXT,
        /**
         * A numeric question.
         */
        NUMERIC,
        /**
         * A choice question.
         */
        CHOICE,
        /**
         * A multi-choice question.
         */
        MULTICHOICE
    }

    /**
     * A separator used in serialized possible answers string to distinguish the elements.
     */
    private static final String SEPARATOR = "##";

    /**
     * The question id.
     */
    private String id;

    /**
     * The question type.
     */
    private Type type;

    /**
     * The question itself.
     */
    private String question;

    /**
     * [optional] The question help/tip. Text to help the user to answer the question.
     */
    private String help;

    /**
     * Possible answers serialized as a raw string. Since we cannot directly persist lists (unless we complicate the
     * underlying model by creating a one-to-many association to another class PossibleAnswer), we store in this field a
     * serialized representation of the list as a String and persist that. The getter of the possibleAnswers field will
     * deserialize the content of this field into an actual list.
     */
    private String possibleAnswersRaw;

    /**
     * Is the question mandatory? if not the user may skip it.
     */
    private boolean mandatory;

    /**
     * Default constructor
     */
    public Question()
    {
    }

    /**
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * @return the type
     */
    public Type getType()
    {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Type type)
    {
        this.type = type;
    }

    /**
     * @return the question
     */
    public String getQuestion()
    {
        return question;
    }

    /**
     * @param question the question to set
     */
    public void setQuestion(String question)
    {
        this.question = question;
    }

    /**
     * @return the help
     */
    public String getHelp()
    {
        return help;
    }

    /**
     * @param help the help to set
     */
    public void setHelp(String help)
    {
        this.help = help;
    }

    /**
     * @return the possibleAnswers
     */
    public List<String> getPossibleAnswers()
    {
        return deserializePossibleAnswers(possibleAnswersRaw);
    }

    /**
     * @param possibleAnswer a new possibleAnswer to set
     */
    public void addPossibleAnswer(String possibleAnswer)
    {
        List<String> possibleAnswers = deserializePossibleAnswers(possibleAnswersRaw);
        possibleAnswers.add(possibleAnswer);
        setPossibleAnswersRaw(serializePossibleAnswers(possibleAnswers));
    }

    /**
     * @return the mandatory
     */
    public boolean isMandatory()
    {
        return mandatory;
    }

    /**
     * @param mandatory the mandatory to set
     */
    public void setMandatory(boolean mandatory)
    {
        this.mandatory = mandatory;
    }

    /**
     * @return the raw string encoding the possible answers.
     */
    public String getPossibleAnswersRaw()
    {
        return possibleAnswersRaw;
    }

    /**
     * This is not supposed to be called by clients. Use {@link Question#addPossibleAnswer(String)} instead.
     *
     * @param possibleAnswersRaw the raw string encoding the possible answers.
     */
    public void setPossibleAnswersRaw(String possibleAnswersRaw)
    {
        this.possibleAnswersRaw = possibleAnswersRaw;
    }

    private List<String> deserializePossibleAnswers(String data)
    {
        List<String> result = new ArrayList<String>();

        String[] components = data.split(SEPARATOR);
        for (String component : components) {
            result.add(component);
        }

        return result;
    }

    private String serializePossibleAnswers(List<String> data)
    {
        StringBuffer sb = new StringBuffer();

        for (String s : data) {
            sb.append(s);
            sb.append(SEPARATOR);
        }

        return sb.toString();
    }
}
