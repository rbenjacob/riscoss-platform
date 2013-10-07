package eu.riscoss.api.model.questionnaire;

import java.util.HashMap;
import java.util.List;

/**
 * The Answers provides is used to store the answers of a questionnaire.
 *
 * @version $Id$
 */
public class Answers
{
    /**
     * The questionnaire these answers refer to.
     */
    private Questionnaire questionnaire;

    /**
     * The actual answers in a map: QuestionId -> Answer.
     */
    private HashMap<String, Answer> answers;

    public Answers(Questionnaire questionnaire)
    {
        answers = new HashMap<String, Answer>();
        this.questionnaire = questionnaire;
    }

    public void addAnswer(String questionId, Answer answer)
    {
        answers.put(questionId, answer);
    }

    public Answer getAnswer(String questionId)
    {
        return answers.get(questionId);
    }

    public boolean areAllQuestionsAnswered()
    {
        boolean allQuestionsAnswered = true;

        List<Question> questions = questionnaire.getQuestions();

        for (Question question : questions) {
            allQuestionsAnswered &= answers.containsKey(question.getId());
        }

        return allQuestionsAnswered;
    }

    public boolean areAllMandatoryQuestionsAnswered()
    {

        boolean allMandatoryQuestionsAnswered = true;

        List<Question> questions = questionnaire.getQuestions();

        for (Question question : questions) {
            if (question.isMandatory()) {
                allMandatoryQuestionsAnswered &= answers.containsKey(question.getId());
            }
        }

        return allMandatoryQuestionsAnswered;
    }
}
