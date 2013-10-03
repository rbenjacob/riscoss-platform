package eu.riscoss.api.model;

import java.util.ArrayList;

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
    public enum Type
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
     * The question possible answers. It will be null for free text and numeric questions.
     */
    private ArrayList<String> possibleAnswers;
    
    /**
     * Is the question mandatory? if not the user may skip it.
     */
    private boolean mandatory;

    /**
     * basic constructor
     */
    public Question()
    {
        possibleAnswers=new ArrayList<String>();
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
    public String[] getPossibleAnswers()
    {
        return possibleAnswers.toArray(new String[0]);
    }

    /**
     * @param possibleAnswer a new possibleAnswer to set
     */
    public void addPossibleAnswer(String possibleAnswer)
    {
        this.possibleAnswers.add(possibleAnswer);
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
}
