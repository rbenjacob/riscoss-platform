package eu.riscoss.api.model;

/**
 * This class is used to store a question.
 *
 * @version $Id$
 */
public class Question
{
    /**
     * The possible types for a question.
     */
    enum Type
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
        CHOICE
    }

    /**
     * The possible types for a question.
     */
    enum Topic
    {
        /**
         * A measurement question.
         */
        MEASUREMENT,
        /**
         * A goal model question.
         */
        GOALMODEL,
        /**
         * A strategy question.
         */
        STRATEGY
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
     * The question topic.
     */
    private Topic topic;
    

    /**
     * The question itself.
     */
    private String question;

    /**
     * [optional] The question help/tip. Text to help the user to answer the question.
     */
    private String help;

    /**
     * The question posible answers. It will be null for free and numeric questions.
     */
    private Answer[] answers;
}
