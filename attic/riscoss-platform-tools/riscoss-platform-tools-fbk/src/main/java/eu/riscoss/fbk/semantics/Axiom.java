package eu.riscoss.fbk.semantics;

import java.util.ArrayList;

public class Axiom
{
	public static final int ALL = 0;
	public static final int ATLEAST = 1;
	
	private ArrayList<Condition> conditions = new ArrayList<Condition>();
	public String p_then;
	
	int operator = ALL;
	
	public Axiom( String p_then, String ... p_if )
	{
		this.p_then = p_then;
		
		for( String c : p_if )
			conditions.add( new Condition( c, "true", Operator.Equals ) );
	}
	
	public Axiom( String p_then, Condition ... p_if )
	{
		this.p_then = p_then;
		
		for( Condition c : p_if )
			conditions.add( c );
	}
	
	public Axiom( String p_then, int operator, String ... p_if )
	{
		this.p_then = p_then;
		
		for( String c : p_if )
			conditions.add( new Condition( c, "true", Operator.Equals ) );
		
//		this.p_if = p_if;
		this.operator = operator;
	}
	
	public String getIf( int i )
	{
		return conditions.get( i ).getPredicate();
		
//		return p_if[i];
	}
	
	public String[] getIf()
	{
		String[] p_if = new String[conditions.size()];
		
		for( int i = 0; i < conditions.size(); i++ )
		{
			p_if[i] = conditions.get( i ).getPredicate();
		}
		
		return p_if;
	}

	public Iterable<Condition> conditions()
	{
		return conditions;
	}
}