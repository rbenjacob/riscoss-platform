package eu.riscoss.tools;

import org.junit.Rule;
import org.junit.Test;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.test.ComponentManagerRule;
import org.xwiki.test.annotation.ComponentList;

import eu.riscoss.api.RISCOSSPlatform;
import eu.riscoss.api.Tool;
import eu.riscoss.api.ToolFactory;
import eu.riscoss.tools.internal.RISCOSSPlatformMock;

/**
 * DummyToolFactoryTest.
 *
 * @version $Id$
 */
@ComponentList({ DummyToolFactory.class, RISCOSSPlatformMock.class })
public class DummyToolFactoryTest
{
    @Rule
    public ComponentManagerRule componentManagerRule = new ComponentManagerRule();

    @Test
    public void simpleToolTest() throws ComponentLookupException
    {
        ToolFactory toolFactory = componentManagerRule.getInstance(ToolFactory.class, DummyToolFactory.TOOL_ID);
        RISCOSSPlatformMock riscossPlatformMock = componentManagerRule.getInstance(RISCOSSPlatform.class);

        Tool tool = toolFactory.createTool();
        tool.execute("foo", null);

        /* Here we can use the RISCOSSPlatformMock in order to check what the tool did */
        //Assert.assertTrue(riscossPlatformMock...);
    }
}
