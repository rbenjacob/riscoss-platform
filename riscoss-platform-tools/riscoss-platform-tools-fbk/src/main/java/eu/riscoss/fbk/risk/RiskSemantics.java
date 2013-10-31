package eu.riscoss.fbk.risk;

import eu.riscoss.fbk.semantics.Axiom;
import eu.riscoss.fbk.semantics.Condition;
import eu.riscoss.fbk.semantics.Operator;
import eu.riscoss.fbk.semantics.Rule;
import eu.riscoss.fbk.semantics.Semantics;

public class RiskSemantics extends Semantics
{
	public RiskSemantics()
	{
		addAttribute( "situation", "st" );
		
		putAxiom( "situation", 
				new Axiom( "st", 
						new Condition( e( "st" ), "true", Operator.Equals ) ) );
		putAxiom( "situation", 
				new Axiom( "su", 
						new Condition( e( "su" ), "true", Operator.Equals ),
						new Condition( e("st" ), "true", Operator.Not ) ) );
		putAxiom( "situation", 
				new Axiom( "sf", 
						new Condition( e( "sf" ), "true", Operator.Equals ),
						new Condition( e("su" ), "true", Operator.Not ),
						new Condition( e("st" ), "true", Operator.Not) ) );
		
		putAxiom( "event", 
				new Axiom( "possible", 
						new Condition( "ptx", "true", Operator.Equals ) ) );
		putAxiom( "event", 
				new Axiom( "critical", 
						new Condition( "ctx", "true", Operator.Equals ) ) );
		putAxiom( "event", 
				new Axiom( "threat", 
						new Condition( "possible", "true", Operator.Equals ),
						new Condition( "critical", "true", Operator.Equals ) ) );
		
		rules.put( "satisfy", 
				new Rule( "stx", 
						new Condition( "st", "true", Operator.Equals ) ) );
		rules.put( "break", 
				new Rule( "sfx", 
						new Condition( "st", "true", Operator.Equals ) ) );
		
		rules.put( "suffer", 
				new Rule( "ctx", 
						new Condition( "st", "true", Operator.Equals ) ) );
		rules.put( "suffer", 
				new Rule( "cux", 
						new Condition( "st", "true", Operator.Not ) ) );
		
		rules.put( "expose", 
				new Rule( "ptx", 
						new Condition( "st", "true", Operator.Equals ) ) );
		rules.put( "expose", 
				new Rule( "pux", 
						new Condition( "st", "true", Operator.Not ) ) );
		rules.put( "expose", 
				new Rule( "ptx", 
						new Condition( "possible", "true", Operator.Equals ) ) );
		rules.put( "expose", 
				new Rule( "pux", 
						new Condition( "possible", "true", Operator.Not ) ) );
		
		rules.put( "impact", 
				new Rule( "threated", 
						new Condition( "threat", "true", Operator.Equals ) ) );
		
		rules.put( "decomposition", 
				new Rule( Rule.Exists, "threated", 
						new Condition( "threated", "true", Operator.Equals ) ) );
		
		rules.put( "meansEnd", 
				new Rule( "threated", 
						new Condition( "threated", "true", Operator.Equals ) ) );
		
		rules.put( "contribution", 
				new Rule( "threated", 
						new Condition( "threated", "true", Operator.Equals ) ) );
		
		rules.put( "depender", 
				new Rule( "threated", 
						new Condition( "threated", "true", Operator.Equals ) ) );
		
		rules.put( "dependee", 
				new Rule( "threated", 
						new Condition( "threated", "true", Operator.Equals ) ) );
		
		rules.put( "indicate", 
				new Rule( "st", 
						new Condition( "st", "true", Operator.Equals ) ) );
	}
	
	protected void addAttribute( String stereotype, String attributeName )
	{
		
	}

	protected String e( String pred )
	{
		return pred + "x";
	}
	
}
