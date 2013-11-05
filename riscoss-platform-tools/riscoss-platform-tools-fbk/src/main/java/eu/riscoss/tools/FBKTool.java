package eu.riscoss.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.riscoss.api.RISCOSSPlatform;
import eu.riscoss.api.Tool;
import eu.riscoss.api.model.GoalModel;
import eu.riscoss.api.model.ImpactModel;
import eu.riscoss.api.model.Indicator;
import eu.riscoss.api.model.RiskModel;
import eu.riscoss.api.model.Scope;
import eu.riscoss.fbk.io.XmlLoader;
import eu.riscoss.fbk.io.XmlTransformer;
import eu.riscoss.fbk.io.XmlWriter;
import eu.riscoss.fbk.language.Analysis;
import eu.riscoss.fbk.language.Model;
import eu.riscoss.fbk.language.Program;
import eu.riscoss.fbk.language.Proposition;
import eu.riscoss.fbk.language.Relation;
import eu.riscoss.fbk.language.Solution;
import eu.riscoss.fbk.risk.RiskEvaluation;
import eu.riscoss.fbk.risk.RiskIdentification;
import eu.riscoss.fbk.util.XmlNode;
import eu.riscoss.fbk.util.XmlReport;

/**
 * FBKTool.
 *
 * @version $Id$
 */
public class FBKTool implements Tool
{
	private static final Logger LOGGER = LoggerFactory.getLogger(FBKTool.class);
	
	private final RISCOSSPlatform platform;
	
	private Map<String,String> conf;
	
	public FBKTool(RISCOSSPlatform riscossPlatform)
	{
		this.platform = riscossPlatform;
	}
	
	void merge( Model sourceModel, Model additionModel )
	{
		for( Proposition p : additionModel.propositions() ) {
			try {
				Proposition p2 = p.clone();
				
				if( sourceModel.getProposition( p2.getId() ) != null ) continue;
				
				sourceModel.addProposition( p2 );
			}
			catch( Exception ex ) {
				ex.printStackTrace();
			}
		}
		
		for( Relation r : sourceModel.relations() ) {
			try {
				Relation newr = r.clone();
				
				newr.setTarget( sourceModel.getProposition( r.getTarget().getId() ) );
				
				for( Proposition s : r.getSources() ) {
					newr.addSource( sourceModel.getProposition( s.getId() ) );
				}
				
				sourceModel.addRelation( newr );
			}
			catch( CloneNotSupportedException e ) {
				System.err.println( "Skipping relation " + r );
				//				e.printStackTrace();
			}
		}
		
		for( Relation r : additionModel.relations() ) {
			try {
				Relation newr = r.clone();
				
				newr.setTarget( sourceModel.getProposition( r.getTarget().getId() ) );
				
				for( Proposition s : r.getSources() ) {
					newr.addSource( sourceModel.getProposition( s.getId() ) );
				}
				
				sourceModel.addRelation( newr );
			}
			catch( CloneNotSupportedException e ) {
				e.printStackTrace();
			}
		}
	}
	
	Model iStarML2Model( String istarml )
	{
		String innerxml = "";
		
		try {
			innerxml = new XmlTransformer().IStarML2InnerXml( istarml );
			
			//			System.out.println( innerxml );
		}
		catch( Exception ex ) {
			ex.printStackTrace();
		}
		
		XmlNode xml = XmlNode.loadString( innerxml );
		
		Program program = new Program();
		
		new XmlLoader().load( xml, program );
		
		return program.getModel();
	}
	
	Model innerXml2Model( String innerXml )
	{
		Program program = new Program();
		new XmlLoader().load( XmlNode.loadString(innerXml), program );
		return program.getModel();
	}
	
	void load( String innerXml, Program program )
	{
		new XmlLoader().load( XmlNode.loadString(innerXml), program );
	}
	
	String[] valueList( String value )
	{
		return value.split( "[,]" );
	}
	
	List<File> fileList( String value )
	{
		ArrayList<File> list = new ArrayList<File>();
		
		String[] values = valueList( value );
		
		for( String val : values ) {
			list.add( new File( val ) );
		}
		
		return list;
	}
	
	
	@Override public void execute( Scope target, Map<String, String> parameters)
	{
		Program program = new Program();
		
		for( RiskModel rm : platform.getRiskModels() )
		{
			load( rm.getXml(), program );
		}
		
		for( GoalModel gm : platform.getGoalModels() )
		{
			Model nm = iStarML2Model( gm.getXml() );
			
			merge( program.getModel(), nm );
		}
		
		for( ImpactModel im : platform.getImpactModels() )
		{
			load( im.getXml(), program );
		}
		
		List<Indicator> indicators = platform.getIndicators( target, 0, Integer.MAX_VALUE );
		
		for( Indicator indicator : indicators )
		{
			Proposition p = program.getModel().getProposition( indicator.getType() );
			
			if( p == null )
			{
				System.err.println( "Element " + indicator.getType() + " not found" );
				
				continue;
			}
			
			try
			{
				program.getScenario().addConstraint( p.getId(), "st", indicator.getValue() );
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
		}
		
		new XmlWriter().write( program, System.out );
		
		
		String reasonerName = parameters.get( "query" );
		
		program.getOptions().setValue( "dlvExecutable", conf.get( "dlvExecutable" ) );
		program.getOptions().setValue( "workingDir", conf.get( "workingDir" ) );
		
		Analysis analysis = selectAnalysis( reasonerName );
		
		analysis.run( program );
		
		Map<String,String> quickReport = new TreeMap<String,String>();
		
		XmlReport report = new XmlReport();
		report.setFilters( new XmlReport.Filter() {
			@Override
			public boolean contains(String value) {
				return true;
			}} );
		
		try
		{
			for( Solution sol : analysis.getResult().solutions() )
			{
				report.addSolution( sol );
				
				for( String var : sol.variables() )
				{
					for( String field : sol.fields( var ) )
					{
						if( field.equals( "threat" ) )
						{
							for( String val : sol.values( var, field ) )
							{
								quickReport.put( var, val );
								break;
							}
						}
						else if( field.equals( "threated" ) )
						{
							for( String val : sol.values( var, field ) )
							{
								quickReport.put( var, val );
								break;
							}
						}
						else if( field.equals( "st" ) )
						{
							for( String val : sol.values( var, field ) )
							{
								quickReport.put( var, val );
								break;
							}
						}
					}
					
//					XmlNode node = xsol.add( "variable" );
//					node.setAttr( "name", var );
//					for( String field : sol.fields( var ) )
//					{
//						for( String val : sol.values( var, field ) )
//						{
//							if( filter.contains( val ) )
//							{
//								XmlNode child = node.add( "property" );
//								child.setAttr( "label", field );
//								child.setAttr( "value", val );
//							}
//						}
//					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		/*
		 * This actually goes to catalina.out; 
		 * it should either go to riscoss knowledge base
		 * or sent back as result
		 */
		report.print( System.out );
		
		for( String k : quickReport.keySet() )
			System.out.println( k + "\t" + quickReport.get( k ) );
		
		//		new XmlWriter().write( program, System.out );
	}
	
	private Analysis selectAnalysis( String analysisName )
	{
		if( analysisName == null ) analysisName = "RiskEvaluation";
		
		final Map<String,Analysis> reasoners = new HashMap<String,Analysis>();
		
		System.out.println( "Selecting reasoner " + analysisName );
		
		reasoners.put( "RiskEvaluation", new RiskEvaluation() );
		reasoners.put( "RiskIdentification", new RiskIdentification() );
		
		Analysis analysis = reasoners.get( analysisName );
		
		if( analysis == null ) analysis = new RiskEvaluation();
		
		return analysis;
	}
	
	@Override public Status getStatus()
	{
		return Status.DONE;
	}
	
	@Override public void stop()
	{
		LOGGER.info("Stopping");
	}
	
	public void setToolConfigurationProvider(Map<String, String> map)
	{
		this.conf = map;
	}
}
