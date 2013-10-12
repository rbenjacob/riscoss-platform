//package eu.riscoss.fbk.dlv;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import eu.riscoss.fbk.dlv.DlvReasoner.Emitter;
//import eu.riscoss.fbk.language.Program;
//import eu.riscoss.fbk.language.Proposition;
//import eu.riscoss.fbk.language.Relation;
//import eu.riscoss.fbk.risk.RiskSemantics;
//import eu.riscoss.fbk.semantics.Axiom;
//import eu.riscoss.fbk.semantics.Rule;
//import eu.riscoss.fbk.semantics.Semantics;
//
//public abstract class DlvRuleset
//{
//	Map<String, Emitter>	functions	= new HashMap<String, Emitter>();
//	
//	protected Semantics semantics = new RiskSemantics();
//	
////	protected MultiMap<String,Axiom> axioms = new MultiMap<String,Axiom>();
////	protected MultiMap<String,Rule> rules = new MultiMap<String,Rule>();
//	
//	public void addEmitter( String stereotype, Emitter emitter )
//	{
//		functions.put( stereotype, emitter );
//	}
//	
//	public abstract void init();
//	
//	protected DlvRuleset()
//	{
//		init();
//		
//		addEmitter( "axioms", new Emitter() {
//			
//			@Override
//			public String toDatalog( Program program ) {
//				
//				StringBuilder b = new StringBuilder();
//				
//				for( Proposition p : program.getModel().propositions() )
//				{
//					if( semantics.getAxiomCount( p.getStereotype() ) < 1 ) continue;
//					
//					b.append( "% Axioms for for " + p.getStereotype() + " " + p.getId() + "\n" );
//					for( Axiom axiom : semantics.axioms( p.getStereotype() ) )
//					{
//						b.append( axiom.p_then + "(" + p.getId() + ") :- " );
//						b.append( join( p, axiom.getIf(), ", " ) );
//						b.append( ".\n" );
//					}
//					b.append( "\n" );
//				}
//				
//				return b.toString();
//			}
//		});
//		
//		addEmitter( "rules", new Emitter() {
//			@Override
//			public String toDatalog( Program program ){
//				
//				StringBuilder b = new StringBuilder();
//				
//				for( String stereotype : program.getModel().relationTypes() )
//				{
//					List<Rule> list = semantics.rules( stereotype );
//					
//					if( list.size() < 1 ) continue;
//					
//					for( Relation r : program.getModel().relations( stereotype ) )
//					{
//						for( Rule rule : list )
//						{
//							b.append( emitRelation( r, rule ) );
//						}
//					}
//				}
//				
//				return b.toString();
//			}} );
//		
//	}
//	
//	protected String join( Proposition p, String[] values, String separator )
//	{
//		String joint = "";
//		String sep = "";
//		for( String val : values )
//		{
//			joint += sep + val + "(" + p.getId() + ")";
//			sep = separator;
//		}
//		return joint;
//	}
//	
//	protected String emitRelation( Relation r, Rule propag )
//	{
//		StringBuilder b = new StringBuilder();
//		
//			if( r.getOperator() == Relation.AND )
//			{
//				b.append( "%" + (r.isNegative() ? " NOT" : "") + " and-" + r.getStereotype() + "( "
//						+ r.getTarget().getId() + ", " + r.getSources() + " ).\n" );
//				
//				b.append( propag.targetPred + "(" + r.getTarget().getId() + ") :- " );
//				
//				String sep = "";
//				for( Proposition p : r.getSources() )
//				{
//					b.append( sep + propag.sourcePred + "(" + p.getId() + ")" );
//					sep = ", ";
//				}
//				b.append( ".\n" );
//				
//				if( propag.alternativeTargetPred != null )
//				{
//					for( Proposition p : r.getSources() )
//					{
//						b.append( propag.alternativeTargetPred + "(" + r.getTarget().getId() + ") :- not " + propag.sourcePred + "("
//								+ p.getId() + ").\n" );
//					}
//				}
//			} else
//			{
//				b.append( "%" + (r.isNegative() ? " NOT" : "") + " or-" + r.getStereotype() + "( "
//						+ r.getTarget().getId() + ", " + r.getSources() + " ).\n" );
//				
//				if( propag.alternativeTargetPred != null )
//				{
//					b.append( propag.alternativeTargetPred + "(" + r.getTarget().getId() + ") :- " );
//					
//					String sep = "";
//					for( Proposition p : r.getSources() )
//					{
//						b.append( sep + "not " + propag.sourcePred + "(" + p.getId() + ")" );
//						sep = ", ";
//					}
//					b.append( ".\n" );
//				}
//				
//				for( Proposition p : r.getSources() )
//				{
//					b.append( propag.targetPred + "(" + r.getTarget().getId() + ") :- " + 
//							propag.sourcePred + "(" + p.getId() + ").\n" );
//				}
//			}
//			
//			b.append( "\n" );
//		
//		return b.toString();
//	}
//	
//	protected String emitRule( Proposition s, String lhs, String ... rhs )
//	{
//		String ret = lhs + "(" + s.getId() + ") :- ";
//		
//		for( String r : rhs )
//			ret += r + "(" + s.getId() + ").\n";
//		
//		return ret;
//	}
//}
