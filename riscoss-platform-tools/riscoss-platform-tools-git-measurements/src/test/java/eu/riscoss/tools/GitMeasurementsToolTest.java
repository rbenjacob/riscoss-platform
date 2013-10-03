package eu.riscoss.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.test.ComponentManagerRule;
import org.xwiki.test.annotation.ComponentList;

import eu.riscoss.api.RISCOSSPlatform;
import eu.riscoss.api.Tool;
import eu.riscoss.api.ToolFactory;
import eu.riscoss.api.model.Measurement;
import eu.riscoss.tools.internal.RISCOSSPlatformMock;
import eu.riscoss.tools.internal.ToolConfigurationProviderMock;

/**
 * GitMeasurementsToolTest.
 *
 * @version $Id$
 */
@ComponentList({ GitMeasurementsToolFactory.class, RISCOSSPlatformMock.class, ToolConfigurationProviderMock.class })
public class GitMeasurementsToolTest
{
    private static final String BASH_PATH = "/bin/bash";

    private static final String GIT_PATH = "/usr/bin/git";

    private static File gitRepository;

    @Rule
    public ComponentManagerRule componentManagerRule = new ComponentManagerRule();

    @BeforeClass
    public static void beforeClass() throws Exception
    {
        gitRepository = File.createTempFile("temp", String.format("%d", System.nanoTime()));
        gitRepository.delete();
        gitRepository.mkdirs();

        OutputStream fos = new FileOutputStream(new File(gitRepository, "foo.txt"));
        IOUtils.write("This is a test.", fos);
        fos.close();

        String[] cmd = {
                BASH_PATH,
                "-c",
                String.format("%s init %s; cd %s; %s add .; %s commit -m \"Initial import\"", GIT_PATH,
                        gitRepository.toString(), gitRepository.toString(), GIT_PATH, GIT_PATH)
        };

        Process p = Runtime.getRuntime().exec(cmd);
        p.waitFor();
    }

    @Test
    public void simpleToolTest() throws ComponentLookupException
    {
        ToolFactory toolFactory =
                componentManagerRule.getInstance(ToolFactory.class, GitMeasurementsToolFactory.TOOL_ID);
        RISCOSSPlatformMock riscossPlatformMock = componentManagerRule.getInstance(RISCOSSPlatform.class);

        Tool tool = toolFactory.createTool();

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(GitMeasurementsToolFactory.REPOSITORY_URI_PARAMETER, gitRepository.toString());
        tool.execute("foo", parameters);

        List<Measurement> measurements = riscossPlatformMock.getMeasurements();

        Assert.assertEquals(3, measurements.size());

        int typesFound = 0;
        for (Measurement measurement : measurements) {
            if ("files-changed-per-commit".equals(measurement.getType())) {
                Assert.assertEquals("1.00", measurement.getValue());
                typesFound++;
            }

            if ("lines-added-per-commit".equals(measurement.getType())) {
                Assert.assertEquals("1.00", measurement.getValue());
                typesFound++;
            }

            if ("lines-removed-per-commit".equals(measurement.getType())) {
                Assert.assertEquals("0.00", measurement.getValue());
                typesFound++;
            }
        }

        Assert.assertEquals(3, typesFound);
    }
}
