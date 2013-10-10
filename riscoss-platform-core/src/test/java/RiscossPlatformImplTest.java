import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.test.ComponentManagerRule;
import org.xwiki.test.annotation.ComponentList;

import eu.riscoss.api.RISCOSSPlatform;
import eu.riscoss.api.model.Company;
import eu.riscoss.api.model.GoalModel;
import eu.riscoss.api.model.Indicator;
import eu.riscoss.api.model.Measurement;
import eu.riscoss.api.model.OSSComponent;
import eu.riscoss.api.model.Product;
import eu.riscoss.api.model.Project;
import eu.riscoss.api.model.RiskModel;
import eu.riscoss.api.model.Scope;
import eu.riscoss.api.model.questionnaire.Answer;
import eu.riscoss.api.model.questionnaire.Answers;
import eu.riscoss.api.model.questionnaire.Question;
import eu.riscoss.api.model.questionnaire.Questionnaire;
import eu.riscoss.api.model.questionnaire.QuestionnaireListener;
import eu.riscoss.internal.RISCOSSPlatformImpl;

/**
 * RiscossPlatformImplTest.
 *
 * @version $Id$
 */
@ComponentList({ HibernateSessionProviderImpl.class, RISCOSSPlatformImpl.class })
public class RiscossPlatformImplTest
{
    @Rule
    public ComponentManagerRule componentManagerRule = new ComponentManagerRule();

    private RISCOSSPlatform riscossPlatform;

    private boolean dataInitialized = false;

    /* Used in submit answes test */
    private boolean answersReceived = false;

    @Before
    public void before() throws ComponentLookupException
    {
        if (!dataInitialized) {
            riscossPlatform = componentManagerRule.getInstance(RISCOSSPlatform.class);

            /* Create some scopes */
            Company company = new Company();
            company.setId("c");
            company.setName("c");
            riscossPlatform.storeScope(company);

            OSSComponent ossComponent = new OSSComponent();
            ossComponent.setId("ossc");
            ossComponent.setName("ossc");
            riscossPlatform.storeScope(ossComponent);

            Product product = new Product();
            product.setId("prod");
            product.setName("prod");
            product.setCompany(company);
            product.getComponents().add(ossComponent);
            riscossPlatform.storeScope(product);

            Project project = new Project();
            project.setId("proj");
            project.setName("proj");
            riscossPlatform.storeScope(project);

            /* Store some measurements */
            for (int i = 0; i < 10; i++) {
                Measurement measurement = new Measurement();
                measurement.setType("m1");
                measurement.setScope(ossComponent);
                measurement.setValue(String.format("%d", i));
                riscossPlatform.storeMeasurement(measurement);
            }

            for (int i = 0; i < 10; i++) {
                Measurement measurement = new Measurement();
                measurement.setType("m2");
                measurement.setScope(ossComponent);
                measurement.setValue(String.format("%d", i));
                riscossPlatform.storeMeasurement(measurement);
            }

            /* Store some indicators */
            for (int i = 0; i < 10; i++) {
                Indicator indicator = new Indicator();
                indicator.setType("i1");
                indicator.setScope(ossComponent);
                indicator.setValue(String.format("%d", i));
                riscossPlatform.storeIndicator(indicator);
            }

            for (int i = 0; i < 10; i++) {
                Indicator indicator = new Indicator();
                indicator.setType("i2");
                indicator.setScope(ossComponent);
                indicator.setValue(String.format("%d", i));
                riscossPlatform.storeIndicator(indicator);
            }

            /* Create risk model */
            RiskModel riskModel = new RiskModel();
            riskModel.setId("r");
            riskModel.setXml("xml");
            riscossPlatform.storeRiskModel(riskModel);

            /* Create goal model */
            GoalModel goalModel = new GoalModel();
            goalModel.setId("g");
            goalModel.setXml("xml");
            riscossPlatform.storeGoalModel(goalModel);

            /* Store some questions */
            for (int i = 0; i < 3; i++) {
                Question question = new Question();
                question.setId("Q" + i);
                question.setQuestion("Q" + i + "?");
                question.setType(Question.Type.FREETEXT);
                question.setHelp("Help" + i);
                riscossPlatform.storeQuestion(question);
            }
        } else {
            dataInitialized = true;
        }
    }

    @Test
    public void getScopeTest()
    {
        Scope scope = riscossPlatform.getScope("c");

        Assert.assertEquals("c", scope.getId());
    }

    @Test
    public void getScopesTest()
    {
        List<Scope> scopes = riscossPlatform.getScopes();

        Assert.assertEquals(4, scopes.size());
    }

    @Test
    public void getScopesByTypeTest()
    {
        List<OSSComponent> scopes = riscossPlatform.getScopesByType(OSSComponent.class);

        Assert.assertEquals(1, scopes.size());
    }

    @Test
    public void getMeasurementsTest()
    {
        OSSComponent ossComponent = new OSSComponent();
        ossComponent.setId("ossc");
        ossComponent.setName("ossc");

        List<Measurement> measurements = riscossPlatform.getMeasurements(ossComponent, 0, 20);
        Assert.assertEquals(20, measurements.size());
    }

    @Test
    public void getMeasurementsForNonExistingScopeTest()
    {
        OSSComponent ossComponent = new OSSComponent();
        ossComponent.setId("ossc1");
        ossComponent.setName("ossc1");

        List<Measurement> measurements = riscossPlatform.getMeasurements(ossComponent, 0, 20);
        Assert.assertEquals(0, measurements.size());
    }

    @Test
    public void getMeasurementsWithTypeTest()
    {
        OSSComponent ossComponent = new OSSComponent();
        ossComponent.setId("ossc");
        ossComponent.setName("ossc");

        List<Measurement> measurements = riscossPlatform.getMeasurements(ossComponent, "m1", 0, 10);
        Assert.assertEquals(10, measurements.size());

        measurements = riscossPlatform.getMeasurements(ossComponent, "m2", 0, 10);
        Assert.assertEquals(10, measurements.size());

        measurements = riscossPlatform.getMeasurements(ossComponent, "m1", 3, 1);
        Assert.assertEquals(1, measurements.size());
        Assert.assertEquals("3", measurements.get(0).getValue());
    }

    @Test
    public void getMeasurementsWithNonExistingTypeTest()
    {
        OSSComponent ossComponent = new OSSComponent();
        ossComponent.setId("ossc");
        ossComponent.setName("ossc");

        List<Measurement> measurements = riscossPlatform.getMeasurements(ossComponent, "NULL", 0, 10);
        Assert.assertEquals(0, measurements.size());
    }

    @Test
    public void getIndicatorsTest()
    {
        OSSComponent ossComponent = new OSSComponent();
        ossComponent.setId("ossc");
        ossComponent.setName("ossc");

        List<Indicator> indicators = riscossPlatform.getIndicators(ossComponent, 0, 20);
        Assert.assertEquals(20, indicators.size());
    }

    @Test
    public void getIndicatorsForNonExistingScopeTest()
    {
        OSSComponent ossComponent = new OSSComponent();
        ossComponent.setId("ossc1");
        ossComponent.setName("ossc1");

        List<Indicator> indicators = riscossPlatform.getIndicators(ossComponent, 0, 20);
        Assert.assertEquals(0, indicators.size());
    }

    @Test
    public void getIndicatorsWithTypeTest()
    {
        OSSComponent ossComponent = new OSSComponent();
        ossComponent.setId("ossc");
        ossComponent.setName("ossc");

        List<Indicator> indicators = riscossPlatform.getIndicators(ossComponent, "i1", 0, 10);
        Assert.assertEquals(10, indicators.size());

        indicators = riscossPlatform.getIndicators(ossComponent, "i2", 0, 10);
        Assert.assertEquals(10, indicators.size());

        indicators = riscossPlatform.getIndicators(ossComponent, "i1", 3, 1);
        Assert.assertEquals(1, indicators.size());
        Assert.assertEquals("3", indicators.get(0).getValue());
    }

    @Test
    public void getIndicatorsWithNonExistingTypeTest()
    {
        OSSComponent ossComponent = new OSSComponent();
        ossComponent.setId("ossc");
        ossComponent.setName("ossc");

        List<Indicator> indicators = riscossPlatform.getIndicators(ossComponent, "NULL", 0, 10);
        Assert.assertEquals(0, indicators.size());
    }

    @Test
    public void getRiskModelsTest()
    {
        List<RiskModel> riskModels = riscossPlatform.getRiskModels();

        Assert.assertEquals(1, riskModels.size());
    }

    @Test
    public void getRiskModel()
    {
        RiskModel riskModel = riscossPlatform.getRiskModel("r");

        Assert.assertEquals("r", riskModel.getId());
    }

    @Test
    public void getNonExistingRiskModel()
    {
        RiskModel riskModel = riscossPlatform.getRiskModel("NULL");

        Assert.assertNull(riskModel);
    }

    @Test
    public void getGoalModelsTest()
    {
        List<GoalModel> goalModels = riscossPlatform.getGoalModels();

        Assert.assertEquals(1, goalModels.size());
    }

    @Test
    public void getGoalModel()
    {
        GoalModel goalModel = riscossPlatform.getGoalModel("g");

        Assert.assertEquals("g", goalModel.getId());
    }

    @Test
    public void getNonExistingGoalModel()
    {
        GoalModel goalModel = riscossPlatform.getGoalModel("NULL");

        Assert.assertNull(goalModel);
    }

    @Test
    public void getQuestionsTest()
    {
        List<Question> questions = riscossPlatform.getQuestions();

        Assert.assertEquals(3, questions.size());
    }

    @Test
    public void getQuestionTest()
    {
        Question question = riscossPlatform.getQuestion("Q1");

        Assert.assertNotNull(question);
    }

    @Test
    public void registerQuestionnaireTest()
    {
        Scope scope = riscossPlatform.getScope("ossc");
        Question question = riscossPlatform.getQuestion("Q1");

        Questionnaire questionnaire = new Questionnaire("QU1");
        riscossPlatform.registerQuestionnaire(scope, questionnaire, new QuestionnaireListener()
        {
            @Override public void questionnaireAnswered(Answers answers)
            {
            }
        });

        List<Questionnaire> registeredQuestionnaires = riscossPlatform.getRegisteredQuestionnaires();

        Assert.assertEquals(1, registeredQuestionnaires.size());

        registeredQuestionnaires = riscossPlatform.getRegisteredQuestionnaires(scope);

        Assert.assertEquals(1, registeredQuestionnaires.size());
    }

    @Test
    public void submitAnswersTest()
    {
        answersReceived = false;

        Scope scope = riscossPlatform.getScope("ossc");
        Question question = riscossPlatform.getQuestion("Q1");

        Questionnaire questionnaire = new Questionnaire("QU2");
        riscossPlatform.registerQuestionnaire(scope, questionnaire, new QuestionnaireListener()
        {
            @Override public void questionnaireAnswered(Answers answers)
            {
                answersReceived = true;
            }
        });

        int numberOfregisteredQuestionnaires = riscossPlatform.getRegisteredQuestionnaires().size();

        Answers answers = new Answers(questionnaire);
        Answer answer = new Answer();
        answer.addValue("foo");
        answers.addAnswer("Q1", answer);
        riscossPlatform.submitAnswers(questionnaire, answers);

        List<Questionnaire> registeredQuestionnaires = riscossPlatform.getRegisteredQuestionnaires();

        Assert.assertEquals(numberOfregisteredQuestionnaires - 1, registeredQuestionnaires.size());

        Assert.assertEquals(true, answersReceived);
    }
}
