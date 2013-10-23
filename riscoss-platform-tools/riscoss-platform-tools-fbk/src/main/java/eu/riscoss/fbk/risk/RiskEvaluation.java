package eu.riscoss.fbk.risk;


import java.io.PrintStream;
import java.util.Iterator;

import eu.riscoss.fbk.language.Analysis;
import eu.riscoss.fbk.language.Program;
import eu.riscoss.fbk.language.Proposition;
import eu.riscoss.fbk.language.Relation;
import eu.riscoss.fbk.language.Result;
import eu.riscoss.fbk.language.Scenario.Pair;
import eu.riscoss.fbk.language.Solution;
import eu.riscoss.fbk.lp.Chunk;
import eu.riscoss.fbk.lp.LPKB;
import eu.riscoss.fbk.lp.Label;
import eu.riscoss.fbk.lp.Node;
import eu.riscoss.fbk.lp.Solver;
import eu.riscoss.fbk.semantics.Axiom;
import eu.riscoss.fbk.semantics.Condition;
import eu.riscoss.fbk.semantics.Rule;
import eu.riscoss.fbk.semantics.Semantics;
import eu.riscoss.fbk.sysex.WorkingDirectory;

public class RiskEvaluation implements Iterable<Solution>, Analysis
{
	Program		program;
	
	LPKB		kb = new LPKB();
	
	Semantics	semantics = new RiskSemantics();
	
	
	public RiskEvaluation()
	{
	}
	
	public void setProgram( Program program )
	{
		this.program = program;
	}
	
	public void run( WorkingDirectory dir )
	{
		run( program );
	}
	
	public void run( Program program )
	{
		setProgram( program );
		
		kb = mkgraph();
		
		kb.getGraph().propagate();
		
//		printSolutions( System.out );
	}
	
	LPKB mkgraph()
	{
		LPKB kb = new LPKB();
		
		for( String type : program.getModel().propositionTypes() )
		{
			for( Proposition p : program.getModel().propositions( type ) )
			{
				for( Axiom a : semantics.axioms( type ) )
				{
					{
						eu.riscoss.fbk.lp.Relation r_comb = new eu.riscoss.fbk.lp.Relation( 
								new Solver.AndSolver(false) );
						
						eu.riscoss.fbk.lp.Relation r_neg = new eu.riscoss.fbk.lp.Relation( 
								new Solver.NotSolver(false) );
						
						eu.riscoss.fbk.lp.Relation r_pos = new eu.riscoss.fbk.lp.Relation( 
								new Solver.AndSolver( false ) );
						
						Node target = kb.store( p, a.p_then );
						Node node_pos = kb.store( p, a.p_then + "::if" );
						Node node_neg = kb.store( p, a.p_then + "::not" );
						
						r_comb.setTargetNode( target );
						r_comb.addSourceNode( node_pos );
						r_comb.addSourceNode( node_neg );
						
						r_pos.setTargetNode( node_pos );
						r_neg.setTargetNode( node_neg );
						
						for( Condition cond : a.conditions() )
						{
							Node source = kb.store( p, cond.getPredicate() );
							
							if( cond.isPositive() )
								r_pos.addSourceNode( source );
							else
								r_neg.addSourceNode( source );
						}
						
						if( r_neg.getSources().size() < 1 )
							r_neg.addSourceNode( kb.FALSE );
						
						kb.addRelation( r_comb );
						kb.addRelation( r_neg );
						kb.addRelation( r_pos );
						
						r_comb.informNodes();
						r_neg.informNodes();
						r_pos.informNodes();
					}
				}
				
				for( Pair pair : program.getScenario().constraintsOf( p.getId() ) )
				{
					Node node = kb.getNode( p.getId(), pair.label );
					
					float value = 1f;
					
					if( pair.value != null )
					{
						try
						{
							value = acquireValue( pair.value );
						}
						catch( Exception ex )
						{
							System.err.println( "\"" + pair.value + "\" is not a real in the range [0,1]" );
						}
					}
					
					if( node != null )
					{
						node.setSatLabel( new Label( value ) );
					}
				}
			}
		}
		
		for( String type : program.getModel().relationTypes() )
		{
			for( Relation r : program.getModel().relations( type ) )
			{
				for( Rule rule : semantics.rules.list( type ) )
				{
					Node target = kb.store( r.getTarget(), rule.targetPred );
					
					float w = getWeight( r );
					
					eu.riscoss.fbk.lp.Relation rel = new eu.riscoss.fbk.lp.Relation();
					
					rel.setWeight( ( w >= 0 ) ? w : -w );
					
					if( rule.connective == Rule.All )
					{
						rel.setSatSolver( new Solver.AndSolver( w < 0 ) );
						rel.setDenSolver( new Solver.OrSolver( !(w < 0 ) ) );
					}
					else
					{
						rel.setSatSolver( new Solver.OrSolver( w < 0 ) );
						rel.setDenSolver( new Solver.AndSolver( !(w < 0 ) ) );
					}
					rel.setTargetNode( target );
					rel.setMnemonic( r.getSources() + " -" + type + "(" + r.getProperty( "weight", "1" ) + ")-> " + r.getTarget() );
					
					for( int i = 0; i < r.getSourceCount(); i++ )
					{
						Node source = kb.store( r.getSources().get( i ), rule.getSourcePred() );
						
						rel.addSourceNode( source );
					}
					
					kb.addRelation( rel );
					
					rel.informNodes();
				}
			}
		}
		
		return kb;
	}
	
	private float getWeight( Relation r )
	{
		try
		{
			return Float.parseFloat( r.getProperty( "weight", "1" ) );
		}
		catch( Exception ex ) {}
		
		return 1;
	}

	private float acquireValue( String value ) throws Exception
	{
		try
		{
			return Float.parseFloat( value );
		}
		catch( Exception ex )
		{
			if( value == null ) throw ex;
			
			if( value.equals( "st" ) ) return 1;
			if( value.endsWith( "sat" ) ) return 1;
		}
		
		return 0;
	}

	public void printSolutions( PrintStream out )
	{
		for( String id : kb.index() )
		{
			Chunk c = kb.index().getChunk( id );
			
			for( String pred : c.predicates() )
			{
				Node node = kb.index().getNode( id, pred );
				
				out.println( 
						c.getProposition().getId() + "." + pred + ": " + 
								node.getSatLabel().getValue() + ", " + 
								node.getDenLabel().getValue() );
			}
		}
	}
	
	class SolutionIterator implements Iterator<Solution>
	{
		boolean done = false;
		
		@Override
		public boolean hasNext()
		{
			return done == false;
		}
		
		@Override
		public Solution next()
		{
			
			Solution sol = new Solution();
			
			for( Proposition p : program.getModel().propositions() )
			{
				Chunk c = kb.index().getChunk( p.getId() );
				
				if( c != null )
				{
					for( String pred : c.predicates() )
					{
						Node node = c.getPredicate( pred );
						sol.addValue( 
								c.getProposition().getId(), 
								pred, 
								"" + ((Label)node.getSatLabel()).getValue() + " - " + ((Label)node.getDenLabel()).getValue() );
					}
				}
			}
			
			done = true;
			
			return sol;
		}
		
		@Override
		public void remove() {}
	}
	
	@Override
	public Iterator<Solution> iterator() {
		return new SolutionIterator();
	}

	@Override
	public Result getResult()
	{
		return new Result() {

			@Override
			public String getDescription() {
				return "";
			}

			@Override
			public Iterable<Solution> solutions() {
				return RiskEvaluation.this;
			}};
	}
}
