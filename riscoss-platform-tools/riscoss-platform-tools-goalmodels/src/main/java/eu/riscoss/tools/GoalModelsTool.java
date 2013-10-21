package eu.riscoss.tools;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.riscoss.api.RISCOSSPlatform;
import eu.riscoss.api.Tool;
import eu.riscoss.api.model.GoalModel;
import eu.riscoss.api.model.Scope;
import eu.riscoss.api.model.questionnaire.Answer;
import eu.riscoss.api.model.questionnaire.Answers;
import eu.riscoss.api.model.questionnaire.Question;
import eu.riscoss.api.model.questionnaire.Questionnaire;
import eu.riscoss.api.model.questionnaire.QuestionnaireListener;

/**
 * Tool managing models i* according to the answers to some questions. The model should have the following constraints
 * <ul>
 * <li>ONE tool for the model associated to ONE scope
 * <li>ONE question can be used by MULTIPLE model elements (<actor><ielement><ielementLink>) (<actorLink> to be decided)
 * <li>only ONE operation can be applied over ONE model element
 * </ul>
 * Allowed operations over an i* element
 * <ul>
 * <li>changing the complete name for an i* element (<actor>, <ielement>), op='refine', the new value for the attribute
 * name' is the answer to the corresponding question.
 * <li>changing part of the name for an i* element (<actor>, <ielement>), op='refine', the new value for the attribute
 * 'name' is the same attribute replacing the text #QUESTION" by the answer to the corresponding question.
 * <li>choosing IE Links, the operation should be placed in the <ielementLink> node, and the answers should be the
 * identifiers for the linked elements that will be remains in the model
 * <li>applying a pattern to an i* element (<ielement>), op='pattern' and pattern='name pattern'. This operation does
 * not have any question associated, it is ONLY applied after a choosing operation (FOR CHANGE IN THE FUTURE IF
 * NECESSARY).
 * </ul>
 * 
 * @author llopez
 * @version $Id$
 */

public class GoalModelsTool implements Tool, QuestionnaireListener
{
    protected static final Logger LOGGER = LoggerFactory.getLogger(GoalModelsTool.class);

    protected final RISCOSSPlatform riscossPlatform;

    private Status status;

    private Scope currentScope;

    private GoalModel goalModel;

    private Questionnaire questionnaire;

    public GoalModelsTool(RISCOSSPlatform riscossPlatform)
    {
        this.riscossPlatform = riscossPlatform;
        this.status = Status.INITIALIZED;
        this.goalModel = new GoalModel();
    }

    @Override
    public void execute(Scope scope, Map<String, String> parameters)
    {
        LOGGER.info(String.format("Running %s on %s", GoalModelsToolFactory.TOOL_ID, scope));
        this.status = Status.STARTED;
        this.currentScope = scope;

        String pattern = parameters.get("pattern");
        if (pattern == null)
            pattern = "TheCompany";

        loadModel(pattern);
        pendingQuestions();
    }

    @Override
    public Status getStatus()
    {
        return status;
    }

    @Override
    public void stop()
    {
        LOGGER.info("Stopping not supported");
        // this.status = Status.STOPPED;
    }

    /**
     * Loads the corresponding model to the tool
     */
    private void loadModel(String pattern)
    {
        // Process the model in order to have it in the model in org.w3c.dom.Document format
        // We ask the platform for the model associated to this scope
        LOGGER.info("looking for a goal model in scope: " + currentScope);
        goalModel = riscossPlatform.getGoalModel(currentScope.getId());
        // If there is no model associated to the scope yet, the model will be constructed from the corresponding
        // pattern
        if (goalModel == null) {
            // LLC: The models corresponding to patterns are stored jointly with the scope's models. Pattern uses the
            // prefix PTRN_ to be differenced from the others.
            // goalModel = riscossPlatform.getGoalModel("PTRN_" + currentScope.getClass().toString());
            // LLC: If we use a pattern, the new model that will be modified will be identified using the scope ID
            // goalModel.setId(currentScope.getId());
            LOGGER.info("looking for the goal model pattern for scope: " + currentScope);
            GoalModel patternModel = riscossPlatform.getGoalModel(pattern);
            goalModel = patternModel.clone(currentScope.getId());
        }
    }

    /**
     * Generates the list of pending questions associated to a model. These questions will be registered in the RISCOSS
     * platform.
     */
    private void pendingQuestions()
    {
        // the model decides the first set of questions
        questionnaire = new Questionnaire();
        processNewQuestions(goalModel.getFirstPendingElement(), new Answers(new Questionnaire()));

        // The new questions are register to the platform to be asked to the user
        if (!questionnaire.isEmpty())
            riscossPlatform.registerQuestionnaire(currentScope, questionnaire, this);
    }

    /**
     * Processes the answers related to some questions in order to modify the model depending on them.<br>
     * If these answers correspond to questions related to a model transformation operation, only 1 model transformation
     * is processed at the same time. <br>
     * Once the answers have been processed, new questions related to the changed model will be register in the RISCOSS
     * platform. This procedure also inform to the platform when the process is finished.
     * 
     * @param answers list of answers to be processed in order to change the model.
     * @see eu.riscoss.api.model.questionnaire.QuestionnaireListener#questionnaireAnswered(eu.riscoss.api.model.questionnaire.Answers)
     */
    @Override
    public void questionnaireAnswered(Answers answers)
    {
        questionnaire = new Questionnaire();
        // process the changes in the model depending on the answers
        for (Question question : answers.getQuestions()) {
            NodeList elements = goalModel.getElementsByQuestion(question.getId());
            for (int i = 0; i < elements.getLength(); i++) {
                Node element = elements.item(i);
                processModelElement(element, answers);
                // The following questions will be guided for the modified element
                processNewQuestions(element, answers);
            }
        }

        // The model modified is stored in the platform after processing all the answers
        riscossPlatform.storeGoalModel(goalModel);
        // The new questions are register to the platform to be asked to the user
        if (!questionnaire.isEmpty())
            riscossPlatform.registerQuestionnaire(currentScope, questionnaire, this);
    }

    /**
     * Processes the information for ONE element. Only one question per node (FOR NOW)
     * 
     * @param element the valid i* element as org.w3c.doc.Node (NOT null).
     * @param answers the list of answers that modified the element depending on the operation related to this element.
     *            (FUTURE: maybe we need a combination of questions/answers to apply the operation).
     */
    private void processModelElement(Node element, Answers answers)
    {
        GoalModel.ElementOperation op = goalModel.getOperation(element);
        Answer answer = answers.getAnswer(goalModel.getQuestion(element));

        switch (op) {
            case REFINEMENT:
                processRefinement(element, answer);
                break;
            case CHOOSING:
                processChoosing(element, answer);
                break;
            /*
             * case PATTERN: //Not here, the elements associated to patterns do not have questions, so they are not
             * processed because of an answer processPattern(element); break;
             */
            default:
                break;
        }
        // the element is marked as processed
        goalModel.setProcessed(element);
    }

    /**
     * Makes the i* element changes corresponding to the operation REFINES over a specific i* element. <br>
     * Only ONE-valued answer for questions related to a node to be refined.
     * 
     * @param node the valid i* element as org.w3c.doc.Node to be refined (not null).
     * @param answer the answer containing the new name for the i* element.
     */
    private void processRefinement(Node node, Answer answer)
    {
        String refinedName = answer.getValue();
        String nodeName = goalModel.getName(node);
        if (nodeName.contains("#QUESTION#")) {
            refinedName = nodeName.replace("#QUESTION#", refinedName);
        }
        goalModel.setName(node, refinedName);
    }

    /**
     * Makes the i* element changes corresponding to the operation CHOOSE over a specific i* element. <br>
     * Possible MULTI-valued answer for questions related to a node decomposition to be chosen.
     * 
     * @param node the valid i* element as org.w3c.doc.Node to be refined (not null). The valid nodes to apply this
     *            operation are the links (ielementLink)
     * @param answer the answer containing the information about which decomposition remains. The decomposition not
     *            related to any answer is removed from the model.
     */
    private void processChoosing(Node node, Answer answer)
    {
        GoalModel.ElementType type = goalModel.getType(node);
        if (type == GoalModel.ElementType.IE_LINK) {
            NodeList linked = node.getChildNodes();
            for (int i = 0; i < linked.getLength(); i++) {
                Node child = linked.item(i);
                String id = goalModel.getIdentifier(child);
                // maybe is a node defined in other part of the model (using iref)
                if (!goalModel.isValidID(id)) {
                    id = goalModel.getIRef(node);
                }
                // Sometimes the child is not an i* element (e.g. end line plus tabs)
                if (!goalModel.isValidID(id)) {
                    continue;
                }
                // if (answer.isAnAnswer(id)) {
                // the logic of the values cannot be included in the class Answer
                if (answer.getValues().contains(id)) {
                    processPattern(child);
                } else {
                    // But if this element is referenciated in another part of the model it has been moved
                    // TODO: Review, this code througt an exceptions but it seems to work.
                    NodeList references = goalModel.getIEsByIRef(id);
                    String newID = "";
                    for (int j = 0; j < references.getLength(); j++) {
                        Node element = references.item(j);
                        if (j == 0) {
                            // We moved the element to the first reference
                            element.getParentNode().replaceChild(child, element);
                            newID = goalModel.getIdentifier(references.item(j));
                        } else {
                            // and use the identifier of the first reference in the others
                            goalModel.setIRef(element, newID);
                        }
                    }
                    // The decomposition is deleted from this ielementLink
                    node.removeChild(child);
                    i--;
                }
            }
        }
    }

    /**
     * Makes the changed to the model corresponding to the apply a PATTERN over a specific i* element. <br>
     * This change is not directly associated to a question. If a node with this operation remains in the model, the
     * pattern is applied.<br>
     * All the matches between the model and the pattern will be done using IDs, because the element name or type can be
     * changed for some reasons (refinements) THIS FUNCTION IS ONLY CALLED AFTER A CHOOSING, FOR THE REMAINING CHILDREN
     * 
     * @param node the valid i* element as a org.w2c.doc.Node responsible for the pattern appliance (not null). This
     *            procedure also checks if the element has a pattern operation associated.
     */
    private void processPattern(Node node)
    {
        GoalModel.ElementOperation op = goalModel.getOperation(node);

        if (op == GoalModel.ElementOperation.PATTERN) // Just in case
        {
            LOGGER.info(goalModel.getPattern(node));
            GoalModel pattern = riscossPlatform.getGoalModel(goalModel.getPattern(node));
            // All the element from the pattern have to be moved to the model, in the case of the matching element,
            // the element will be replaced
            NodeList patternElements = pattern.getModelElements();

            for (int i = 0; i < patternElements.getLength(); i++) { // actors & dependencies
                Node patternNode = patternElements.item(i);
                String patternNodeId = goalModel.getIdentifier(patternNode);
                if (!goalModel.isValidID(patternNodeId)) {
                    continue;
                }
                Node modelNode = goalModel.getElementById(patternNodeId);
                if (modelNode == null) {
                    // If the node is new, it is copied
                    goalModel.addElementFromOtherModel(patternNode);
                } else {
                    // If the node is already in the model, it is replaced
                    GoalModel.ElementType type = goalModel.getType(patternNode);
                    // TODO: For now, the actors from the patters are not copied to the model, maybe they are already
                    // refined, only the elements inside the actor are copied
                    if (type == GoalModel.ElementType.ACTOR) {
                        NodeList patternNodes = patternNode.getChildNodes();
                        for (int j = 0; j < patternNodes.getLength(); j++) {
                            patternNodeId = goalModel.getIdentifier(patternNodes.item(j));
                            if (!goalModel.isValidID(patternNodeId)) {
                                continue;
                            }

                            if (goalModel.getIdentifier(node).equalsIgnoreCase(patternNodeId)) {
                                // the node to apply the pattern operation: REPLACED FOR THE NODE IN THE PATTERN
                                goalModel.replaceChildFromOtherModel(node.getParentNode(), node, patternNodes.item(j));
                            } else {
                                Node inModel = goalModel.getElementById(patternNodeId);
                                if (inModel == null) {
                                    goalModel.addChildFromOtherModel(modelNode, patternNodes.item(j));
                                } else {
                                    goalModel.addChildsFromOtherModel(inModel, patternNodes.item(j));
                                }
                            }
                        }
                    } else {
                        // dependencies
                        // TODO: For now the dependency in the model is replaced by the dependency from the pattern,
                        // maybe we need to replace only the childs (<depender><dependy>)
                        goalModel.replaceChildFromOtherModel(modelNode.getParentNode(), modelNode, patternNode);
                    }
                }
            }
        }
        // In this case the node is not marked as processed because is possible that the node in the pattern should have
        // questions associated
    }

    /********************************************************
     * Functions & Procedures to find out the new questions *
     ********************************************************/
    /**
     * Generates the new list of questions. The questions already answered by the known list of answers are not included
     * because the user already answered.
     * 
     * @param element the valid i* element (not null) as org.w3c.doc.Node that guides the search of new questions. If
     *            this node is null, the tool decides the policy to search new questions.
     * @param answers the list of answers to process the model, used not to repeat a question to the user.
     */
    private void processNewQuestions(Node element, Answers answers)
    {
        // TODO: review, in the previous version this function could be call using a null as a element, this is the code
        // if (element == null)
        // element = istarModel.getFirstPendingElement(model);

        // element == null is the base case, it stops the recursive calls
        if (element != null) {
            // 1. Processing the pending questions for the element
            processPendingElementQuestions(element, answers);

            // 2. If the element does not have pending questions, we need to process the pending question for the parent
            // node
            if (questionnaire.isEmpty() /* && processParent */) {
                processNewQuestions(goalModel.getParent(element), answers);
            }
        }
        LOGGER.info("new questions proceeded");
    }

    /**
     * Generates the new list of questions. This list of questions will be guided depending on the element type. The
     * questions already answered by the known list of answers are not included because the user already answered.
     * 
     * @param element the valid i* element (not null) as org.w3c.doc.Node that guides the search of new questions. If
     *            this node is null, the tool decides the policy to search new questions.
     * @param answers the list of answers to process the model, used not to repeat a question to the user.
     */
    private void processPendingElementQuestions(Node element, Answers answers)
    {
        if (element != null) {
            GoalModel.ElementType type = goalModel.getType(element);

            // 1. Process the questions to the actor itself
            if (!goalModel.isProcessed(element)) {
                addQuestion(element, answers);
            }

            // 2. Process the specific questions depending on the element type
            switch (type) {
                case ACTOR:
                    processPendingActorQuestions(element, answers);
                    break;
                case INTENTIONAL_ELEMENT:
                    processPendingIEQuestions(element, answers);
                    break;
                case IE_LINK:
                    processPendingIELinkQuestions(element, answers);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Generates the new list of questions associated to an actor. The questions already answered by the known list of
     * answers are not included because the user already answered.
     * 
     * @param actor the actor (not null) as org.w3c.doc.Node that guides the search of new questions. If this node is
     *            null, the tool decides the policy to search new questions.
     * @param answers the list of answers to process the model, used not to repeat a question to the user.
     */
    private void processPendingActorQuestions(Node actor, Answers answers)
    {
        // 2.1. Process the questions related to the dependencies to an actor and not IE
        addQuestions(goalModel.getPendingActorOutgoingDependencies(actor), answers);
        // 2.2. If there is no questions at level of the element, then the first non-processed element (anyone)
        if (questionnaire.isEmpty()) {
            processPendingElementQuestions(goalModel.getFirstPendingElement(actor), answers);
        }
        // 2.3 If there is no more questions inside the actor, the outgoing dependencies that outgoing an IE inside the
        // actor
        if (questionnaire.isEmpty())
            addQuestions(goalModel.getPendingActorIEOutgoingDependencies(actor), answers);
    }

    /**
     * Generates the new list of questions associated to an intentional element (IE) inside an actor. The questions
     * already answered by the known list of answers are not included because the user already answered.
     * 
     * @param ielement the IE (not null) as org.w3c.doc.Node that guides the search of new questions. If this node is
     *            null, the tool decides the policy to search new questions.
     * @param answers the list of answers to process the model, used not to repeat a question to the user.
     */
    private void processPendingIEQuestions(Node ielement, Answers answers)
    {
        // 2.1. Process the questions related to the dependencies to the IE
        addQuestions(goalModel.getPendingIEDependencies(ielement), answers);
        // 2.2 If there is no questions at level of the element, then the first non-processed element (anyone)
        if (questionnaire.isEmpty()) {
            processPendingElementQuestions(goalModel.getFirstPendingElement(ielement), answers);
        }
    }

    /**
     * Generates the new list of questions associated to an intentional element (IE) link inside an actor. The questions
     * already answered by the known list of answers are not included because the user already answered.
     * 
     * @param ielementLink the IE link (not null) as org.w3c.doc.Node that guides the search of new questions. If this
     *            node is null, the tool decides the policy to search new questions.
     * @param answers the list of answers to process the model, used not to repeat a question to the user.
     */
    private void processPendingIELinkQuestions(Node ielementLink, Answers answers)
    {
        // 2.1. If there is no questions at level of the element, then the first non-processed element (anyone)
        if (questionnaire.isEmpty()) {
            processPendingElementQuestions(goalModel.getFirstPendingElement(ielementLink), answers);
        }
    }

    /**
     * Adds the question associated to an i* element (if it has) to the private list of questions. The questions already
     * answered by the known list of answers are not included because the user already answered.
     * 
     * @param element the i* element as org.w3c.doc.Node.
     * @param answers the list of answers to process the model, used not to repeat a question to the user. The questions
     *            already answered by the known list of answers are not included because the user already answered.
     */
    private void addQuestion(Node element, Answers answers)
    {
        if (element != null) {
            String questionId = goalModel.getQuestion(element);
            // If the question is already included in the questionnaire or the list of answers,
            // it won't be added again as a question.
            if (!questionId.isEmpty() && questionnaire.getQuestion(questionId) == null
                && answers.getAnswer(questionId) == null) {
                questionnaire.addQuestion(riscossPlatform.getQuestion(questionId));
            }
        }
    }

    /**
     * Adds the questions associated to a list of i* elements (if they have) to the private list of questions. The
     * questions already answered by the known list of answers are not included because the user already answered.
     * 
     * @param elements list of i* elements as org.w3c.doc.NodeList
     * @param answers the list of answers to process the model, used not to repeat a question to the user. The questions
     *            already answered by the known list of answers are not included because the user already answered.
     */
    private void addQuestions(NodeList elements, Answers answers)
    {
        for (int i = 0; i < elements.getLength(); i++) {
            addQuestion(elements.item(i), answers);
        }
    }
}
