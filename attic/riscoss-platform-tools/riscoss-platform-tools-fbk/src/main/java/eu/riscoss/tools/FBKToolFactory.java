package eu.riscoss.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;

import eu.riscoss.api.ParameterDescription;
import eu.riscoss.api.RISCOSSPlatform;
import eu.riscoss.api.Tool;
import eu.riscoss.api.ToolConfigurationProvider;
import eu.riscoss.api.ToolFactory;

/**
 * DummyToolFactory.
 *
 * @version $Id$
 */
@Component
@Singleton
@Named(FBKToolFactory.TOOL_ID)
public class FBKToolFactory implements ToolFactory
{
	public static final String TOOL_ID = "fbktool";

	@Inject
	private RISCOSSPlatform riscossPlatform;

	@Inject
	private ToolConfigurationProvider tcp;
	
	String dlvExecutable = "";

	@Override public String getToolId()
	{
		return TOOL_ID;
	}

	@Override public List<ParameterDescription> getToolConfigurationParametersDescriptions()
	{
		Map<String,String> conf = tcp.getConfiguration( TOOL_ID );
		
		dlvExecutable = conf.get( "dlvExecutable" );
		
		return new ArrayList<ParameterDescription>();
	}

	@Override public List<ParameterDescription> getToolExecutionParametersDescriptions()
	{
		ArrayList<ParameterDescription> list = new ArrayList<ParameterDescription>();
		
//		list.add( new ParameterDescription( "dlvExecutable", "" ) );
		
		return list;
	}

	@Override public Tool createTool()
	{
		System.out.println( tcp.getConfiguration( "fbktool" ) );
		
		FBKTool tool = new FBKTool( riscossPlatform );
		
		tool.setToolConfigurationProvider( tcp.getConfiguration( "fbktool" ) );
		
		return tool;
	}
}
