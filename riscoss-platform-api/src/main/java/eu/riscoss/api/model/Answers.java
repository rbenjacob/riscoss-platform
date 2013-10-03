package eu.riscoss.api.model;

import java.util.HashMap;

import eu.riscoss.api.model.Answer;

/**
 * The Answers provides is used to store the answers of a questionnaire.
 *
 * @version $Id$
 */
public class Answers
{
    private HashMap<String, Answer> answers;
    private Questionnaire questionnaire;
    
    public Answers(Questionnaire q)
    {
        answers=new HashMap<String, Answer>();
        questionnaire=q;
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
        boolean allQuestionsAnswered=true;
        Question[] questions=questionnaire.getQuestions();
        for(int i=0; i<questions.length; i++)
            allQuestionsAnswered &= answers.containsKey(questions[i].getId());
        return allQuestionsAnswered;
    }
    
    public boolean areAllMandatoryQuestionsAnswered()
    {
        boolean allMandatoryQuestionsAnswered=true;
        Question[] questions=questionnaire.getQuestions();
        for(int i=0; i<questions.length; i++)
            if(questions[i].isMandatory())
                allMandatoryQuestionsAnswered &= answers.containsKey(questions[i].getId());
        return allMandatoryQuestionsAnswered;
    }
}
