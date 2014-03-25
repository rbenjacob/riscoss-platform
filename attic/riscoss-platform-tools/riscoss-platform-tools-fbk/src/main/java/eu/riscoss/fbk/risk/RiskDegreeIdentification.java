package eu.riscoss.fbk.risk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.riscoss.fbk.dlv.DlvReasoner;
import eu.riscoss.fbk.language.Analysis;
import eu.riscoss.fbk.language.Cost;
import eu.riscoss.fbk.language.Program;
import eu.riscoss.fbk.language.Proposition;
import eu.riscoss.fbk.language.Relation;
import eu.riscoss.fbk.language.Result;
import eu.riscoss.fbk.language.Scenario.Pair;
import eu.riscoss.fbk.semantics.Axiom;
import eu.riscoss.fbk.semantics.Rule;

public class RiskDegreeIdentification implements Analysis
{
	DlvReasoner reasoner = new DlvReasoner();
	
	String[] levels = new String[] { "n", "l", "m", "h", "x" };
	
	protected String join( Proposition p, String[] values, String level, String separator )
	{
		String joint = "";
		String sep = "";
		for( String val : values )
		{
			joint += sep + val + "_" + level + "(" + p.getId() + ")";
			sep = separator;
		}
		return joint;
	}
	
	public void run( Program program )
	{
		StringBuilder b = new StringBuilder();
		
		for( String type : program.getModel().propositionTypes() )
		{
			for( Proposition p : program.getModel().propositions( type ) )
			{
				if( reasoner.semantics.getAxiomCount( p.getStereotype() ) < 1 ) continue;
				
				b.append( "% Axioms for for " + p.getStereotype() + " " + p.getId() + "\n" );
				for( Axiom axiom : reasoner.semantics.axioms( p.getStereotype() ) )
				{
					for( String level : levels )
					{
						b.append( axiom.p_then + "_" + level + "(" + p.getId() + ") :- " );
						b.append( join( p, axiom.getIf(), level, ", " ) );
						b.append( ".\n" );
					}
				}
				b.append( "\n" );
			}
		}
		
		for( String type : program.getModel().relationTypes() )
		{
			List<Rule> list = reasoner.semantics.rules( type );
			
			if( list.size() < 1 ) continue;
			
			for( Relation r : program.getModel().relations( type ) )
			{
				for( Rule rule : list )
				{
					for( String level : levels )
					{
					if( r.getOperator() == Relation.AND )
					{
						b.append( "%" + (r.isNegative() ? " NOT" : "") + " and-" + r.getStereotype() + "( "
								+ r.getTarget().getId() + ", " + r.getSources() + " ).\n" );
						
						b.append( rule.targetPred + "_" + level + "(" + r.getTarget().getId() + ") :- " );
						
						String sep = "";
						for( Proposition p : r.getSources() )
						{
							b.append( sep + rule.getSourcePred() + "_" + level + "(" + p.getId() + ")" );
							sep = ", ";
						}
						b.append( ".\n" );
					}
					else
					{
						b.append( "%" + (r.isNegative() ? " NOT" : "") + " or-" + r.getStereotype() + "( "
								+ r.getTarget().getId() + ", " + r.getSources() + " ).\n" );
						
						for( Proposition p : r.getSources() )
						{
							b.append( rule.targetPred  + "_" + level+ "(" + r.getTarget().getId() + ") :- " + 
									rule.getSourcePred() + "_" + level + "(" + p.getId() + ").\n" );
						}
					}
					
					b.append( "\n" );
					}
				}
			}
		}
		
		b.append( "% Scenario\n" );
		
		for( Proposition s : program.getModel().propositions() ) // "situation" ) )
		{
			ArrayList<Pair> constraints = program.getScenario().constraintsOf( s.getId() );
			
			if( constraints.size() > 0 )
			{
				String sep = "";
				for( Pair p : constraints )
				{
					String value = p.label;
					b.append( sep + value + "(" + s.getId() + ")" );
					sep = " v ";
				}
				b.append( ".\n" );
			} else
			{
				String sep = "";
				for( String level : levels )
				{
					b.append( 
							sep + "sat(" + s.getId() + ", " + level + ")" );
					sep = " v ";
				}
				b.append( " v su(" + s.getId() + ").\n" );
			}
		}
		
		b.append( "\n" );
		
		b.append( "% Costs\n" );
		
		// First-come, higher-priority
		int priority = program.getScenario().getCostStructure().getCostTypeCount();
		
		for( String costname : program.getScenario().getCostStructure().costTypes() )
		{
			b.append( "% cost " + priority + ": " + costname + "\n" );
			
			HashMap<String, HashMap<Integer,Cost>> costmap = program.getScenario().getCostStructure()
					.getCosts( costname );
			
			for( String predicate : costmap.keySet() )
			{
				HashMap<Integer,Cost> costs = costmap.get( predicate );
				for( Integer value : costs.keySet() )
				{
					Cost cost = costs.get( value );
					
					for( String id : cost.variables() )
					{
						b.append( ":~ " + cost.getPredicate() + "(" + id + "). [" + cost.getValue()
								+ ":" + priority + "]\n" );
					}
				}
			}
			
			priority--;
		}
		
		b.append( "\n" );
		
		b.append( "% Objectives\n" );
		
		if( program.getQuery().objectivesCount() > 0 )
		{
			String sep = "";
			
			for( String id : program.getQuery().objectives() )
			{
				b.append( sep + program.getQuery().getObjective( id ) + "(" + id + ")" );
				sep = ", ";
			}
			b.append( " ?\n" );
		}
	}

	@Override
	public Result getResult()
	{
		return Result.NO_SOLUTIONS;
	}
}
