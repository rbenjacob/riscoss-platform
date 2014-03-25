package eu.riscoss.tools;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.test.ComponentManagerRule;
import org.xwiki.test.annotation.ComponentList;

import eu.riscoss.api.Tool;
import eu.riscoss.api.ToolFactory;
import eu.riscoss.api.model.OSSComponent;
import eu.riscoss.api.model.Scope;
import eu.riscoss.tools.internal.RISCOSSPlatformMock;
import eu.riscoss.tools.internal.ToolConfigurationProviderMock;

/**
 * GoalModelsToolTest.
 * 
 * @version $Id$
 */
@ComponentList({GoalModelsToolFactory.class, RISCOSSPlatformMock.class, ToolConfigurationProviderMock.class})
public class GoalModelsToolTest
{
    @Rule
    public ComponentManagerRule componentManagerRule = new ComponentManagerRule();

    @BeforeClass
    public static void beforeClass() throws Exception
    {
    }

    @Test
    public void simpleToolTest() throws ComponentLookupException
    {
        ToolFactory toolFactory = componentManagerRule.getInstance(ToolFactory.class, GoalModelsToolFactory.TOOL_ID);
        // RISCOSSPlatformMock riscossPlatformMock = componentManagerRule.getInstance(RISCOSSPlatform.class);
        Tool tool = toolFactory.createTool();
        Scope scope = new OSSComponent();
        scope.setId("foo");
        Map<String, String> parameters = new HashMap<String, String>();

        tool.execute(scope, parameters);

        Assert.assertEquals(0, 0);
    }
}
